package com.vcx.decomp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.vcx.decomp.R;
import org.json.JSONArray;
import org.json.JSONObject;

public class ImportsAdapter extends RecyclerView.Adapter<ImportsAdapter.ViewHolder> {
    private JSONArray imports;

    public ImportsAdapter(String jsonData) {
        try {
            if (jsonData == null || jsonData.trim().isEmpty()) {
                imports = new JSONArray();
            } else {
                imports = new JSONArray(jsonData);
            }
        } catch (Exception e) {
            imports = new JSONArray();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TextView tv = new TextView(context);
        tv.setPadding(32, 20, 32, 20);
        tv.setTextSize(12);
        tv.setTextColor(0xFFFF8800);
        tv.setBackgroundColor(0xFF1A1A1A);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject imp = imports.getJSONObject(position);
            String name = imp.optString("name", "");
            String lib = imp.optString("libname", "");
            Context context = holder.itemView.getContext();
            holder.textView.setText(context.getString(R.string.import_format, name, lib));
        } catch (Exception e) {
            holder.textView.setText("Error at position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return imports.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}