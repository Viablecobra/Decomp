package com.vcx.decomp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DecompPagerAdapter extends FragmentStateAdapter {
    private final String[] tabs;

    public DecompPagerAdapter(AppCompatActivity activity, String[] tabs) {
        super(activity.getSupportFragmentManager(), activity.getLifecycle());
        this.tabs = tabs;
    }

    @Override
    public Fragment createFragment(int position) {
        return DecompFragment.newInstance(tabs[position * 2 + 1]);
    }

    @Override
    public int getItemCount() {
        return tabs.length / 2;
    }
}