package com.vt.disposisibandung.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.vt.disposisibandung.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.io.File;

/**
 * Created by irvan on 7/6/15.
 */
@EViewGroup(R.layout.layout_grid_item_preview_photo)
public class PreviewPhotoGridItemView extends RelativeLayout {

    @ViewById(R.id.photo)
    protected ImageView imageViewPhoto;

    private String filename;
    private OnPreviewActionListener onPreviewActionListener;

    public PreviewPhotoGridItemView(Context context) {
        super(context);
    }

    public PreviewPhotoGridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewPhotoGridItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Click(R.id.photo)
    protected void previewClicked() {
        if (onPreviewActionListener != null)
            onPreviewActionListener.onPreviewClicked(this, filename);
    }

    @Click(R.id.buttonDelete)
    protected void deleteClicked() {
        if (onPreviewActionListener != null)
            onPreviewActionListener.onPreviewDeleted(this, filename);
    }

    public void setPhoto(String filename) {
        this.filename = filename;

        Picasso.with(getContext())
                .load(new File(filename))
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .resize(100, 100)
                .centerCrop()
                .into(imageViewPhoto);
    }

    public void setOnPreviewActionListener(OnPreviewActionListener onPreviewActionListener) {
        this.onPreviewActionListener = onPreviewActionListener;
    }

    public interface OnPreviewActionListener {
        void onPreviewClicked(View view, String filename);

        void onPreviewDeleted(View view, String filename);
    }
}
