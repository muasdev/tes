package com.vt.disposisibandung.fragments.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vt.disposisibandung.fragments.LoginFragment_;
import com.vt.disposisibandung.fragments.SplashFragment_;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by irvan on 6/24/15.
 */
public class LoginPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<Fragment> fragments;

    public LoginPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

        fragments = new ArrayList<>();
        fragments.add(SplashFragment_.builder().build());
        fragments.add(LoginFragment_.builder().build());
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
