package com.vcx.decomp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.vcx.decomp.R;

public class PseudoCAdapter extends RecyclerView.Adapter<PseudoCAdapter.ViewHolder> {
    private String[] codeLines;

    public PseudoCAdapter(String pseudoC) {
        codeLines = pseudoC.split(System.lineSeparator());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        TextView tv = new TextView(context);
        tv.setPadding(32, 16, 32, 16);
        tv.setTextSize(11);
        tv.setTextColor(0xFF00FF00);
        tv.setBackgroundColor(0xFF0A0A0A);
        tv.setTypeface(Typeface.MONOSPACE);
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