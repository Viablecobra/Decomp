package com.vcx.decomp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

public class DecompiledActivity extends AppCompatActivity {
    public static final String EXTRA_SO_URI = "extra_so_uri";
    
    private ProgressBar progressIndicator;
    private TextView tvTitle, tvStatus;
    private BottomNavigationView bottomNav;
    private ViewPager2 viewPager;
    private DecompData[] tabsData;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    
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
        tabsData = new DecompData[]{
            new DecompData("Overview", nativeResult[1]),
            new DecompData("Functions", nativeResult[3]),
            new DecompData("XRefs", nativeResult[5]),
            new DecompData("Strings", nativeResult[7]),
            new DecompData("Names", nativeResult[9]),
            new DecompData("Pseudo C", nativeResult[11]),
            new DecompData("Imports", nativeResult[13]),
            new DecompData("Exports", nativeResult[15])
        };
        
        viewPager.setAdapter(new FragmentAdapter(this));
        bottomNav.setOnItemSelectedListener(item -> {
            viewPager.setCurrentItem(item.getOrder());
            return true;
        });
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
            return 8;
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