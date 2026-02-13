package com.vcx.decomp.adapter;

import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class OutputAdapter extends RecyclerView.Adapter<OutputAdapter.ViewHolder> {
    private String[] logLines;

    public OutputAdapter(String logData) {
        if (logData == null || logData.isEmpty()) {
            logLines = new String[]{"No output"};
        } else {
            logLines = logData.split("
");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(32, 16, 32, 16);
        tv.setTextSize(11);
        tv.setTextColor(0xFFAAAAAA);
        tv.setBackgroundColor(0xFF0A0A0A);
        tv.setTypeface(android.graphics.Typeface.MONOSPACE);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String line = logLines[position];
        holder.textView.setText(line);
        
        if (line.toLowerCase().contains("error")) {
            holder.textView.setTextColor(0xFFFF0000);
        } else if (line.toLowerCase().contains("warning")) {
            holder.textView.setTextColor(0xFFFFAA00);
        } else if (line.toLowerCase().contains("success") || line.toLowerCase().contains("complete")) {
            holder.textView.setTextColor(0xFF00FF00);
        } else {
            holder.textView.setTextColor(0xFFAAAAAA);
        }
    }

    @Override
    public int getItemCount() {
        return logLines.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(android.view.View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}