package com.vcx.decomp.adapter;

import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {
    private JSONArray overview;

    public OverviewAdapter(String jsonData) {
        try {
            overview = new JSONArray(jsonData);
        } catch (Exception e) {
            overview = new JSONArray();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(32, 24, 32, 24);
        tv.setTextSize(13);
        tv.setTextColor(0xFFFFFFFF);
        tv.setBackgroundColor(0xFF1A1A1A);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject item = overview.getJSONObject(position);
            String key = item.keys().next();
            String value = item.getString(key);
            holder.textView.setText(String.format("%s: %s", key, value));
        } catch (Exception e) {
            holder.textView.setText("Error at position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return overview.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(android.view.View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}