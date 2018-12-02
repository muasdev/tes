package com.vt.disposisibandung.fragments.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vt.disposisibandung.fragments.DetailSuratDisposisiFragment_;
import com.vt.disposisibandung.fragments.DetailSuratFragment_;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Irvan on 6/28/2015.
 */
public class DetailSuratPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<Fragment> fragments;

    public DetailSuratPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

        fragments = new ArrayList<>();
        fragments.add(DetailSuratFragment_.builder().build());
        fragments.add(DetailSuratDisposisiFragment_.builder().build());

        boolean arsip = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("arsip", true);
        if (arsip == true) fragments.remove(1);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SURAT";
            case 1:
                return "DISPOSISI";
            default:
                return super.getPageTitle(position);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
