package com.vcx.decomp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class DecompiledActivity extends Activity {

    public static final String EXTRA_SO_URI = "extra_so_uri";

    static {
        System.loadLibrary("decomp");
    }

    private native String nativeDecompile(String soPath);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decompiled);

        TextView title = findViewById(R.id.tvTitle);
        TextView code  = findViewById(R.id.tvCode);

        String uriString = getIntent().getStringExtra(EXTRA_SO_URI);
        if (uriString != null) {
            Uri soUri = Uri.parse(uriString);
            title.setText("Decompiled: " + soUri.getLastPathSegment());
            
            try {
                String decompiled = nativeDecompile(uriString);
                code.setText(decompiled);
            } catch (Exception e) {
                code.setText("Native error: " + e.getMessage());
            }
        } else {
            title.setText("No file");
            code.setText("// Select a .so file first");
        }
    }
}