package com.vcx.decomp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.vcx.decomp.R;
import org.json.JSONArray;
import org.json.JSONObject;

public class NamesAdapter extends RecyclerView.Adapter<NamesAdapter.ViewHolder> {
    private JSONArray names;

    public NamesAdapter(String jsonData) {
        try {
            if (jsonData == null || jsonData.trim().isEmpty()) {
                names = new JSONArray();
            } else {
                names = new JSONArray(jsonData);
            }
        } catch (Exception e) {
            names = new JSONArray();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TextView tv = new TextView(context);
        tv.setPadding(32, 20, 32, 20);
        tv.setTextSize(12);
        tv.setTextColor(0xFFFF00FF);
        tv.setBackgroundColor(0xFF1A1A1A);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject nameObj = names.getJSONObject(position);
            String symbol = nameObj.optString("name", "");
            String addr = nameObj.optString("vaddr", "");
            Context context = holder.itemView.getContext();
            holder.textView.setText(context.getString(R.string.name_format, symbol, addr));
        } catch (Exception e) {
            holder.textView.setText("Error at position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return names.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}