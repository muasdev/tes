package com.vt.disposisibandung.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * For VELO TEKNOLOGI
 * Created by Ridwan Ismail on 11 Agustus 2016
 * You can contact me at : ismail.ridwan98@gmail.com
 * -------------------------------------------------
 * SURAT ONLINE BANDUNG
 * com.vt.sipbandung.fragments
 */

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private View mTargetView;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTargetView(View targetView) {
        mTargetView = targetView;
    }

    @Override
    public boolean canChildScrollUp() {
        return this.mTargetView.canScrollVertically(-1);
    }
}