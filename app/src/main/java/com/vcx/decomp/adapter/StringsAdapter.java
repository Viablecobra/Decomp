package com.vcx.decomp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.vcx.decomp.R;
import org.json.JSONArray;
import org.json.JSONObject;

public class StringsAdapter extends RecyclerView.Adapter<StringsAdapter.ViewHolder> {
    private JSONArray strings;

    public StringsAdapter(String jsonData) {
        try {
            if (jsonData == null || jsonData.trim().isEmpty()) {
                strings = new JSONArray();
            } else {
                strings = new JSONArray(jsonData);
            }
        } catch (Exception e) {
            strings = new JSONArray();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TextView tv = new TextView(context);
        tv.setPadding(32, 20, 32, 20);
        tv.setTextSize(12);
        tv.setTextColor(0xFFFFFF00);
        tv.setBackgroundColor(0xFF1A1A1A);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject strObj = strings.getJSONObject(position);
            String value = strObj.optString("string", "");
            String addr = strObj.optString("vaddr", "");
            Context context = holder.itemView.getContext();
            holder.textView.setText(context.getString(R.string.string_format, addr, value));
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