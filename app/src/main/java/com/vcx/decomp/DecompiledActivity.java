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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vcx.decomp.R;

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

    private final int[] TAB_IDS = {R.id.tab_overview, R.id.tab_code, R.id.tab_symbols, R.id.tab_data, R.id.tab_output};
    private final String[] TAB_KEYS = {"overview", "code", "symbols", "data", "output"};

    public enum AdapterType {
        OVERVIEW, FUNCTIONS, PSEUDOC, XREFS, NAMES, STRINGS, IMPORTS, EXPORTS, OUTPUT, NONE
    }

    private static class DecompData {
        String title, topData, bottomData;
        AdapterType topType, bottomType;
        DecompData(String t, String top, String bot, AdapterType topT, AdapterType botT) { 
            title = t; 
            topData = top != null ? top : ""; 
            bottomData = bot != null ? bot : "";
            topType = topT;
            bottomType = botT;
        }
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
        btnOpen.setOnClickListener(v -> tvStatus.setText(R.string.open));
        btnSave.setOnClickListener(v -> tvStatus.setText(R.string.save));
    }

    private void loadTabVisibility() {
        tabVisible.clear();
        for (int i = 0; i < TAB_IDS.length; i++) {
            tabVisible.put(TAB_IDS[i], prefs.getBoolean(TAB_KEYS[i], true));
        }
    }

    private void showTabConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.configure_tabs);
        
        String[] tabNames = getResources().getStringArray(R.array.tab_names);
        boolean[] checked = new boolean[TAB_IDS.length];
        for (int i = 0; i < TAB_IDS.length; i++) {
            checked[i] = tabVisible.get(TAB_IDS[i]);
        }
        
        builder.setMultiChoiceItems(tabNames, checked, (dialog, which, isChecked) -> {
            tabVisible.put(TAB_IDS[which], isChecked);
        });
        
        builder.setPositiveButton(R.string.apply, (dialog, which) -> {
            saveTabVisibility();
            updateBottomNav();
            finish();
            startActivity(getIntent());
        });
        builder.setNegativeButton(R.string.cancel, null);
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
            tvTitle.setText(R.string.no_file);
            return;
        }

        tvTitle.setText(getString(R.string.decompiling_file, Uri.parse(soPath).getLastPathSegment()));
        tvStatus.setText(R.string.analyzing);
        
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
        if (tabVisible.getOrDefault(R.id.tab_overview, true)) 
            visibleTabs.add(new DecompData(getString(R.string.tab_overview), nativeResult[1], "", AdapterType.OVERVIEW, AdapterType.NONE));
        if (tabVisible.getOrDefault(R.id.tab_code, true)) 
            visibleTabs.add(new DecompData("Code", nativeResult[3] != null ? nativeResult[3] : "", nativeResult[11] != null ? nativeResult[11] : "", AdapterType.FUNCTIONS, AdapterType.PSEUDOC));
        if (tabVisible.getOrDefault(R.id.tab_symbols, true)) 
            visibleTabs.add(new DecompData("Symbols", nativeResult[5] != null ? nativeResult[5] : "", nativeResult[9] != null ? nativeResult[9] : "", AdapterType.XREFS, AdapterType.NAMES));
        if (tabVisible.getOrDefault(R.id.tab_data, true)) 
            visibleTabs.add(new DecompData("Data", nativeResult[7] != null ? nativeResult[7] : "", nativeResult[13] != null ? nativeResult[13] : "", AdapterType.STRINGS, AdapterType.IMPORTS));
        if (tabVisible.getOrDefault(R.id.tab_output, true)) 
            visibleTabs.add(new DecompData(getString(R.string.tab_output), nativeResult[15] != null ? nativeResult[15] : "", getString(R.string.analysis_complete), AdapterType.EXPORTS, AdapterType.OUTPUT));
        
        tabsData = visibleTabs.toArray(new DecompData[0]);
        viewPager.setAdapter(new FragmentAdapter(this));
        updateBottomNav();
        bottomNav.setOnItemSelectedListener(item -> {
            for (int i = 0; i < TAB_IDS.length; i++) {
                if (item.getItemId() == TAB_IDS[i] && tabVisible.get(TAB_IDS[i])) {
                    viewPager.setCurrentItem(findVisibleTabIndex(TAB_IDS[i]));
                    return true;
                }
            }
            return false;
        });
    }

    private int findVisibleTabIndex(int tabId) {
        int index = 0;
        for (int i = 0; i < TAB_IDS.length; i++) {
            if (TAB_IDS[i] == tabId) return index;
            if (tabVisible.getOrDefault(TAB_IDS[i], false)) index++;
        }
        return 0;
    }

    public DecompData[] getTabsData() { return tabsData; }

    private static native String[] nativeDecompile(String soPath);

    static {
        System.loadLibrary("decomp");
    }

    private class FragmentAdapter extends FragmentStateAdapter {
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

        public static DecompFragment newInstance(int position) {
            DecompFragment fragment = new DecompFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_dual_recycler, container, false);
            RecyclerView recyclerTop = view.findViewById(R.id.recycler_top);
            RecyclerView recyclerBottom = view.findViewById(R.id.recycler_bottom);
            
            recyclerTop.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerBottom.setLayoutManager(new LinearLayoutManager(requireContext()));
            
            int position = getArguments().getInt(ARG_POSITION);
            DecompiledActivity activity = (DecompiledActivity) requireActivity();
            if (activity.getTabsData() == null || position >= activity.getTabsData().length) {
                recyclerTop.setAdapter(new com.vcx.decomp.adapter.OverviewAdapter("[]"));
                recyclerBottom.setAdapter(new com.vcx.decomp.adapter.OverviewAdapter("[]"));
                return view;
            }
            DecompData data = activity.getTabsData()[position];
            
            recyclerTop.setAdapter(getAdapterForType(data.topType, data.topData));
            recyclerBottom.setAdapter(getAdapterForType(data.bottomType, data.bottomData));
            
            return view;
        }

        private RecyclerView.Adapter getAdapterForType(AdapterType type, String data) {
            switch (type) {
                case FUNCTIONS: return new com.vcx.decomp.adapter.FunctionsAdapter(data != null ? data : "[]");
                case PSEUDOC: return new com.vcx.decomp.adapter.PseudoCAdapter(data != null ? data : "");
                case XREFS: return new com.vcx.decomp.adapter.XRefsAdapter(data != null ? data : "[]");
                case NAMES: return new com.vcx.decomp.adapter.NamesAdapter(data != null ? data : "[]");
                case STRINGS: return new com.vcx.decomp.adapter.StringsAdapter(data != null ? data : "[]");
                case IMPORTS: return new com.vcx.decomp.adapter.ImportsAdapter(data != null ? data : "[]");
                case EXPORTS: return new com.vcx.decomp.adapter.ExportsAdapter(data != null ? data : "[]");
                case OUTPUT: return new com.vcx.decomp.adapter.OutputAdapter(data != null ? data : "");
                case OVERVIEW: return new com.vcx.decomp.adapter.OverviewAdapter(data != null ? data : "[]");
                default: return new com.vcx.decomp.adapter.OverviewAdapter("[]");
            }
        }
    }
}