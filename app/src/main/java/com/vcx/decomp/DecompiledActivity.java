package com.vcx.decomp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class DecompiledActivity extends AppCompatActivity {
    public static final String EXTRA_SO_URI = "extra_so_uri";
    
    private ProgressBar progressIndicator;
    private TextView tvTitle, tvStatus, btnOpen, btnSave, btnConfig;
    private BottomNavigationView bottomNav;
    private ViewPager2 viewPager;
    private DecompData[] tabsData;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private SharedPreferences prefs;
    private Map<Integer, Boolean> tabVisible = new HashMap<>();

    private final int[] TAB_IDS = {R.id.tab_overview, R.id.tab_funcs, R.id.tab_xrefs, R.id.tab_strings, 
                                   R.id.tab_names, R.id.tab_pseudoc, R.id.tab_imports, R.id.tab_exports, 
                                   R.id.tab_overview2, R.id.tab_output};
    private final String[] TAB_KEYS = {"overview", "funcs", "xrefs", "strings", "names", "pseudoc", 
                                       "imports", "exports", "overview2", "output"};
    private final String[] TAB_NAMES = {"Overview", "Functions", "XRefs", "Strings", "Names", 
                                        "Pseudo C", "Imports", "Exports", "Overview2", "Output"};

    private static class DecompData {
        String title, jsonData;
        DecompData(String t, String d) { title = t; jsonData = d; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decompiled);
        initViews();
        startAnalysis();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvStatus = findViewById(R.id.tvStatus);
        progressIndicator = findViewById(R.id.progressIndicator);
        bottomNav = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.view_pager);
        btnOpen = findViewById(R.id.btnOpen);
        btnSave = findViewById(R.id.btnSave);
        btnConfig = findViewById(R.id.btnConfig);
        
        prefs = getSharedPreferences("decomp_prefs", MODE_PRIVATE);
        loadTabVisibility();
        
        btnConfig.setOnClickListener(v -> showTabConfigDialog());
        btnOpen.setOnClickListener(v -> tvStatus.setText("Open clicked"));
        btnSave.setOnClickListener(v -> tvStatus.setText("Save clicked"));
    }

    private void loadTabVisibility() {
        tabVisible.clear();
        for (int i = 0; i < TAB_IDS.length; i++) {
            tabVisible.put(TAB_IDS[i], prefs.getBoolean(TAB_KEYS[i], i < 5));
        }
    }

    private void showTabConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Configure 10 Tabs");
        
        boolean[] checked = new boolean[TAB_NAMES.length];
        for (int i = 0; i < TAB_NAMES.length; i++) {
            checked[i] = tabVisible.get(TAB_IDS[i]);
        }
        
        builder.setMultiChoiceItems(TAB_NAMES, checked, (dialog, which, isChecked) -> {
            tabVisible.put(TAB_IDS[which], isChecked);
        });
        
        builder.setPositiveButton("Apply", (dialog, which) -> {
            saveTabVisibility();
            updateBottomNav();
            recreate();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveTabVisibility() {
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < TAB_IDS.length; i++) {
            editor.putBoolean(TAB_KEYS[i], tabVisible.get(TAB_IDS[i]));
        }
        editor.apply();
    }

    private void updateBottomNav() {
        Menu menu = bottomNav.getMenu();
        for (int i = 0; i < TAB_IDS.length; i++) {
            menu.findItem(TAB_IDS[i]).setVisible(tabVisible.get(TAB_IDS[i]));
        }
    }

    private void startAnalysis() {
        String soPath = getIntent().getStringExtra(EXTRA_SO_URI);
        if (soPath == null) {
            tvTitle.setText("No file selected");
            return;
        }

        tvTitle.setText("Decompiling: " + Uri.parse(soPath).getLastPathSegment());
        tvStatus.setText("Analyzing...");
        
        new Thread(() -> {
            String[] result = nativeDecompile(soPath);
            mainHandler.post(() -> {
                setupTabs(result);
                progressIndicator.setVisibility(View.GONE);
                tvStatus.setVisibility(View.GONE);
                bottomNav.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private void setupTabs(String[] nativeResult) {
        List<DecompData> visibleTabs = new ArrayList<>();
        if (tabVisible.getOrDefault(R.id.tab_overview, true)) visibleTabs.add(new DecompData("Overview", nativeResult[1]));
        if (tabVisible.getOrDefault(R.id.tab_funcs, true)) visibleTabs.add(new DecompData("Functions", nativeResult[3]));
        if (tabVisible.getOrDefault(R.id.tab_xrefs, true)) visibleTabs.add(new DecompData("XRefs", nativeResult[5]));
        if (tabVisible.getOrDefault(R.id.tab_strings, true)) visibleTabs.add(new DecompData("Strings", nativeResult[7]));
        if (tabVisible.getOrDefault(R.id.tab_names, true)) visibleTabs.add(new DecompData("Names", nativeResult[9]));
        if (tabVisible.getOrDefault(R.id.tab_pseudoc, true)) visibleTabs.add(new DecompData("Pseudo C", nativeResult[11]));
        if (tabVisible.getOrDefault(R.id.tab_imports, true)) visibleTabs.add(new DecompData("Imports", nativeResult[13]));
        if (tabVisible.getOrDefault(R.id.tab_exports, true)) visibleTabs.add(new DecompData("Exports", nativeResult[15]));
        if (tabVisible.getOrDefault(R.id.tab_overview2, false)) visibleTabs.add(new DecompData("Overview2", nativeResult[1]));
        if (tabVisible.getOrDefault(R.id.tab_output, true)) visibleTabs.add(new DecompData("Output", "Analysis complete
No warnings"));
        
        tabsData = visibleTabs.toArray(new DecompData[0]);
        viewPager.setAdapter(new FragmentAdapter(this));
        updateBottomNav();
        bottomNav.setOnItemSelectedListener(item -> {
            for (int i = 0; i < TAB_IDS.length; i++) {
                if (item.getItemId() == TAB_IDS[i] && tabVisible.get(TAB_IDS[i])) {
                    viewPager.setCurrentItem(findTabIndex(TAB_IDS[i]));
                    return true;
                }
            }
            return false;
        });
    }

    private int findTabIndex(int tabId) {
        for (int i = 0; i < tabsData.length; i++) {
            if (tabVisible.get(tabId)) {
                for (int j = 0; j < TAB_IDS.length; j++) {
                    if (TAB_IDS[j] == tabId) return i++;
                }
            }
        }
        return 0;
    }

    public DecompData[] getTabsData() { return tabsData; }

    private static native String[] nativeDecompile(String soPath);

    static {
        System.loadLibrary("decomp");
    }

    private static class FragmentAdapter extends FragmentStateAdapter {
        FragmentAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public Fragment createFragment(int position) {
            return DecompFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return tabsData != null ? tabsData.length : 0;
        }
    }

    public static class DecompFragment extends Fragment {
        private static final String ARG_POSITION = "position";
        private RecyclerView recyclerView;
        private DecompAdapter adapter;

        public static DecompFragment newInstance(int position) {
            DecompFragment fragment = new DecompFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            recyclerView = new RecyclerView(requireContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            int position = getArguments().getInt(ARG_POSITION);
            DecompiledActivity activity = (DecompiledActivity) requireActivity();
            adapter = new DecompAdapter(activity.getTabsData()[position].jsonData);
            recyclerView.setAdapter(adapter);
            return recyclerView;
        }
    }

    public static class DecompAdapter extends RecyclerView.Adapter<DecompAdapter.ViewHolder> {
        private JSONArray dataArray;
        private String[] dataLines;

        DecompAdapter(String jsonData) {
            try {
                if (jsonData.startsWith("[")) {
                    dataArray = new JSONArray(jsonData);
                } else {
                    dataLines = jsonData.split(System.lineSeparator());
                }
            } catch (Exception e) {
                dataLines = new String[]{"Parse error"};
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(32, 32, 16, 32);
            tv.setTextSize(12);
            tv.setTextColor(0xFFFFFFFF);
            tv.setBackgroundColor(0xFF1A1A1A);
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            try {
                if (dataArray != null && position < dataArray.length()) {
                    holder.textView.setText(dataArray.getJSONObject(position).toString(2));
                } else if (dataLines != null && position < dataLines.length) {
                    holder.textView.setText(dataLines[position]);
                }
            } catch (Exception e) {
                holder.textView.setText("Error at line " + position);
            }
        }

        @Override
        public int getItemCount() {
            return dataArray != null ? dataArray.length() : (dataLines != null ? dataLines.length : 1);
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View view) {
                super(view);
                textView = (TextView) view;
            }
        }
    }
}