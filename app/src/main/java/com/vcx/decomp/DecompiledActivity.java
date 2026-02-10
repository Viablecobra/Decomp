package com.vcx.decomp;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;

public class DecompiledActivity extends Activity {

    public static final String EXTRA_SO_URI = "extra_so_uri";

    static {
        System.loadLibrary("decomp");
    }

    private native String[] nativeDecompile(String soPath);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decompiled);

        TextView title = findViewById(R.id.tvTitle);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        Button btnCopy = findViewById(R.id.btnCopy);

        String uriString = getIntent().getStringExtra(EXTRA_SO_URI);
        if (uriString != null) {
            Uri soUri = Uri.parse(uriString);
            title.setText("Decompiled: " + soUri.getLastPathSegment());
            
            try {
                String[] tabs = nativeDecompile(uriString);
                
                DecompPagerAdapter adapter = new DecompPagerAdapter(this, tabs);
                viewPager.setAdapter(adapter);
                
                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                    tab.setText(tabs[position * 2]);
                }).attach();

                btnCopy.setOnClickListener(v -> {
                    int pos = viewPager.getCurrentItem();
                    String currentText = tabs[(pos * 2) + 1];
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Decomp", currentText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Copied tab!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                title.setText("Error: " + e.getMessage());
            }
        } else {
            title.setText("No file");
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}