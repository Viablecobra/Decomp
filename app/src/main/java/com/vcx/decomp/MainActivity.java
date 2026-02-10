package com.vcx.decomp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_PICK_SO = 1001;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSelectSo = (Button) findViewById(R.id.btnSelectSo);
        btnSelectSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSoFilePicker();
            }
        });
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
                Toast.makeText(this, "Selected: " + getFileName(uri), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Please select a .so file", Toast.LENGTH_SHORT).show();
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