package com.vcx.decomp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class DecompFragment extends Fragment {
    private static final String ARG_CONTENT = "content";

    public static DecompFragment newInstance(String content) {
        DecompFragment fragment = new DecompFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(requireContext());
        TextView textView = new TextView(requireContext());
        textView.setId(android.R.id.text1);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(12);
        textView.setTypeface(null, android.graphics.Typeface.MONOSPACE);
        textView.setPadding(32, 32, 32, 32);
        textView.setLineSpacing(8, 1);
        if (getArguments() != null) {
            textView.setText(getArguments().getString(ARG_CONTENT));
        }
        scrollView.addView(textView);
        return scrollView;
    }
}