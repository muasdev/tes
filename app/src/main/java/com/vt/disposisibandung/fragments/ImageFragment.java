package com.vt.disposisibandung.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vt.disposisibandung.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by irvan on 6/30/15.
 */
@EFragment(R.layout.fragment_image)
public class ImageFragment extends Fragment implements Target {

    private static final String ARG_IMAGE_FILENAME = "image_filename";
    private static final String ARG_IMAGE_URL = "image_url";

    @ViewById(R.id.progressLoading)
    protected ProgressBar progressBarLoading;

    @ViewById(R.id.image)
    protected ImageView imageView;

    @FragmentArg(ARG_IMAGE_FILENAME)
    protected String imageFilename;

    @FragmentArg(ARG_IMAGE_URL)
    protected String imageUrl;

    @AfterViews
    protected void initViews() {
        if (imageFilename != null) {
            Picasso.with(getActivity())
                    .load(new File(imageFilename))
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .resize(2000, 2000)
                    .onlyScaleDown()
                    .centerInside()
                    .into(this);
        } else if (imageUrl != null) {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .resize(2000, 2000)
                    .onlyScaleDown()
                    .centerInside()
                    .into(this);
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        progressBarLoading.setVisibility(View.GONE);

        imageView.setImageBitmap(bitmap);
        new PhotoViewAttacher(imageView);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        progressBarLoading.setVisibility(View.GONE);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }
}
