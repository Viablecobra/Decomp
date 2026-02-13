package com.vcx.decomp.adapter;

import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class PseudoCAdapter extends RecyclerView.Adapter<PseudoCAdapter.ViewHolder> {
    private String[] codeLines;

    public PseudoCAdapter(String pseudoC) {
        codeLines = pseudoC.split("
");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(32, 16, 32, 16);
        tv.setTextSize(11);
        tv.setTextColor(0xFF00FF00);
        tv.setBackgroundColor(0xFF0A0A0A);
        tv.setTypeface(android.graphics.Typeface.MONOSPACE);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(codeLines[position]);
    }

    @Override
    public int getItemCount() {
        return codeLines.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View view) {
            super(view);
            textView = (TextView) view;
        }
    }
}