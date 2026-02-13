package com.vcx.decomp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.vcx.decomp.R;
import org.json.JSONArray;
import org.json.JSONObject;

public class ExportsAdapter extends RecyclerView.Adapter<ExportsAdapter.ViewHolder> {
    private JSONArray exports;

    public ExportsAdapter(String jsonData) {
        try {
            if (jsonData == null || jsonData.trim().isEmpty()) {
                exports = new JSONArray();
            } else {
                exports = new JSONArray(jsonData);
            }
        } catch (Exception e) {
            exports = new JSONArray();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TextView tv = new TextView(context);
        tv.setPadding(32, 20, 32, 20);
        tv.setTextSize(12);
        tv.setTextColor(0xFF88FF00);
        tv.setBackgroundColor(0xFF1A1A1A);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject exp = exports.getJSONObject(position);
            String name = exp.optString("name", "");
            String addr = exp.optString("vaddr", "");
            Context context = holder.itemView.getContext();
            holder.textView.setText(context.getString(R.string.export_format, name, addr));
        } catch (Exception e) {
            holder.textView.setText("Error at position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return exports.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}