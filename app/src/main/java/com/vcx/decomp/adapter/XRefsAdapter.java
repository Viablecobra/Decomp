package com.vcx.decomp.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;

public class XRefsAdapter extends RecyclerView.Adapter<XRefsAdapter.ViewHolder> {
    private JSONArray xrefs;

    public XRefsAdapter(String jsonData) {
        try {
            xrefs = new JSONArray(jsonData);
        } catch (Exception e) {
            xrefs = new JSONArray();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TextView tv = new TextView(context);
        tv.setPadding(32, 20, 32, 20);
        tv.setTextSize(12);
        tv.setTextColor(0xFF00FFFF);
        tv.setBackgroundColor(0xFF1A1A1A);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject xref = xrefs.getJSONObject(position);
            String from = xref.optString("from", "");
            String to = xref.optString("to", "");
            String type = xref.optString("type", "");
            Context context = holder.itemView.getContext();
            holder.textView.setText(context.getString(R.string.xref_format, from, to, type));
        } catch (Exception e) {
            holder.textView.setText("Error at position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return xrefs.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}