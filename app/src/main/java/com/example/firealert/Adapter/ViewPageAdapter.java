package com.example.firealert.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments= new ArrayList<>();
    private List<String> stringList = new ArrayList<>();
    public ViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }
    public void clear()
    {
        fragments.clear();
        stringList.clear();
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return stringList.get(position);
    }

    public void addFragment(Fragment fragment, String title)
    {
        fragments.add(fragment);
        stringList.add(title);
    }
}
