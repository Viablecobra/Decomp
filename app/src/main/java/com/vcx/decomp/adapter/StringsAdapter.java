package com.vcx.decomp.adapter;

import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

public class StringsAdapter extends RecyclerView.Adapter<StringsAdapter.ViewHolder> {
    private JSONArray strings;

    public StringsAdapter(String jsonData) {
        try {
            strings = new JSONArray(jsonData);
        } catch (Exception e) {
            strings = new JSONArray();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(32, 20, 32, 20);
        tv.setTextSize(12);
        tv.setTextColor(0xFFFFFF00);
        tv.setBackgroundColor(0xFF1A1A1A);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject str = strings.getJSONObject(position);
            String value = str.optString("string", "");
            String addr = str.optString("vaddr", "");
            holder.textView.setText(String.format("[%s] "%s"", addr, value));
        } catch (Exception e) {
            holder.textView.setText("Error at position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return strings.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}