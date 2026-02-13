package com.vcx.decomp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import com.vcx.decomp.R;

public class OutputAdapter extends RecyclerView.Adapter<OutputAdapter.ViewHolder> {
    private String[] logLines;

    public OutputAdapter(String logData) {
        if (logData == null || logData.isEmpty()) {
            logLines = new String[]{"No output"};
        } else {
            logLines = logData.split(System.lineSeparator());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TextView tv = new TextView(context);
        tv.setPadding(32, 16, 32, 16);
        tv.setTextSize(11);
        tv.setTextColor(0xFFAAAAAA);
        tv.setBackgroundColor(0xFF0A0A0A);
        tv.setTypeface(Typeface.MONOSPACE);
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
        ViewHolder(View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}