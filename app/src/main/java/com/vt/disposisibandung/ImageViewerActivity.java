package com.vt.disposisibandung;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.vt.disposisibandung.fragments.adapters.ImageViewerPagerAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Irvan on 6/29/2015.
 */
@EActivity(R.layout.activity_image_viewer)
public class ImageViewerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final String EXTRA_IMAGE_PREVIEW = "image_filename";
    private static final String EXTRA_IMAGE_LIST = "image_list";
    private static final String EXTRA_TITLE_LIST = "title_list";
    private static final String EXTRA_IMAGE_POSITION = "image_position";

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;

    @ViewById(R.id.viewPager)
    protected ViewPager viewPager;

    @Extra(EXTRA_IMAGE_PREVIEW)
    protected String previewFilename;

    @Extra(EXTRA_IMAGE_LIST)
    protected ArrayList<String> imageList;

    @Extra(EXTRA_TITLE_LIST)
    protected ArrayList<String> titleList;

    @Extra(EXTRA_IMAGE_POSITION)
    protected int position;

    ImageViewerPagerAdapter pagerAdapter;

    @AfterViews
    protected void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pagerAdapter = new ImageViewerPagerAdapter(getSupportFragmentManager(), this);

        if (previewFilename != null) {
            titleList = new ArrayList<>();
            String[] parts = previewFilename.split("/");
            titleList.add(parts[parts.length - 1]);
            pagerAdapter.setFile(previewFilename);
        } else {
            pagerAdapter.setFiles(imageList);
        }

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        viewPager.setCurrentItem(position);
        onPageSelected(position);
    }

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setTitle(titleList.get(position));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
