package com.vcx.decomp.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

public class FunctionsAdapter extends RecyclerView.Adapter<FunctionsAdapter.ViewHolder> {
    private JSONArray functions;

    public FunctionsAdapter(String jsonData) {
        try {
            functions = new JSONArray(jsonData);
        } catch (Exception e) {
            functions = new JSONArray();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TextView tv = new TextView(context);
        tv.setPadding(32, 24, 32, 24);
        tv.setTextSize(13);
        tv.setTextColor(0xFFFFFFFF);
        tv.setBackgroundColor(0xFF1A1A1A);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject func = functions.getJSONObject(position);
            String name = func.optString("name", "unknown");
            String addr = func.optString("addr", "");
            String size = func.optString("size", "");
            Context context = holder.itemView.getContext();
            holder.textView.setText(context.getString(R.string.function_format, name, addr, size));
        } catch (Exception e) {
            holder.textView.setText("Error at position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return functions.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}