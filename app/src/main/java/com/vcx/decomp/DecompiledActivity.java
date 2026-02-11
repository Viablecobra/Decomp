package com.vcx.decomp;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.chaquo.python.android.PythonCallback;
import org.json.JSONObject;

public class DecompiledActivity extends AppCompatActivity {
    public static final String EXTRA_SO_URI = "extra_so_uri";

    private CircularProgressIndicator progressIndicator;
    private TextView tvTitle, tvStatus, tvProgress;
    private WebView webViewResult;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decompiled);
        initViews();
        startAnalysis();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        progressIndicator = findViewById(R.id.progressIndicator);
        tvStatus = findViewById(R.id.tvStatus);
        tvProgress = findViewById(R.id.tvProgress);
        webViewResult = findViewById(R.id.webViewResult);
        
        WebSettings webSettings = webViewResult.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewResult.setWebViewClient(new WebViewClient());
        webViewResult.setBackgroundColor(0xFF000000);
    }

    private void startAnalysis() {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        
        String soPath = getIntent().getStringExtra(EXTRA_SO_URI);
        if (soPath == null) {
            tvTitle.setText(R.string.no_file);
            return;
        }

        tvTitle.setText(getString(R.string.decomp_title) + ": " + Uri.parse(soPath).getLastPathSegment());
        tvStatus.setText(R.string.initializing);
        
        Python.getInstance().getModule("analyzer").callAttrAsync("ida_analyze", soPath,
            new PythonCallback() {
                @Override
                public void onResponse(String response) {
                    mainHandler.post(() -> onProgressUpdate(response));
                }
            });
    }

    private void onProgressUpdate(String jsonStr) {
        try {
            JSONObject data = new JSONObject(jsonStr);
            int progress = data.getInt("progress");
            tvStatus.setText(data.getString("status"));
            tvProgress.setText(progress + "%");
            
            if (data.optBoolean("done", false)) {
                String html = data.getString("html");
                webViewResult.loadData(html, "text/html", "UTF-8");
                progressIndicator.setVisibility(View.GONE);
                webViewResult.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            tvStatus.setText(getString(R.string.error_prefix) + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}