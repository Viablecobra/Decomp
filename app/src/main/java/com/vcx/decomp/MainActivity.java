package com.vcx.decomp;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.vcx.decomp.R;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_PICK_SO = 1001;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            String crashLog = android.util.Log.getStackTraceString(throwable);
            
            new AlertDialog.Builder(this)
                .setTitle(R.string.app_crashed)
                .setMessage(crashLog.substring(0, Math.min(500, crashLog.length())) + "...")
                .setPositiveButton(R.string.copy_log, (dialog, which) -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("JNI Crash Log", crashLog);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, R.string.log_copied, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.back_to_main, (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finishAffinity();
                })
                .setCancelable(false)
                .show();
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSelectSo = (Button) findViewById(R.id.btnSelectSo);
        btnSelectSo.setOnClickListener(v -> openSoFilePicker());
    }

    private void openSoFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_SO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_PICK_SO && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null && isSoFile(uri)) {
                getContentResolver().takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Intent viewIntent = new Intent(this, DecompiledActivity.class);
                viewIntent.putExtra(DecompiledActivity.EXTRA_SO_URI, uri.toString());
                startActivity(viewIntent);
            } else {
                Toast.makeText(this, R.string.select_so_file, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isSoFile(Uri uri) {
        return getFileName(uri).toLowerCase().endsWith(".so");
    }

    private String getFileName(Uri uri) {
        try {
            String[] projection = {DocumentsContract.Document.COLUMN_DISPLAY_NAME};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME);
                String name = cursor.getString(nameIndex);
                cursor.close();
                return name;
            }
        } catch (Exception ignored) {}
        
        String pathSegment = uri.getLastPathSegment();
        return pathSegment != null ? pathSegment : "unknown.so";
    }
}