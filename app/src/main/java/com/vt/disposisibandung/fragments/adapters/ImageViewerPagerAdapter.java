package com.vt.disposisibandung.fragments.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vt.disposisibandung.fragments.ImageFragment_;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by irvan on 7/1/15.
 */
public class ImageViewerPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private List<Fragment> fragments;

    public ImageViewerPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public void setFile(String filename) {
        fragments = new ArrayList<>();

        fragments.add(ImageFragment_.builder().imageFilename(filename).build());
    }

    public void setFiles(List<String> filenames) {
        fragments = new ArrayList<>();

        for (String filename : filenames) {
            fragments.add(ImageFragment_.builder().imageUrl(filename).build());
        }
    }
}
