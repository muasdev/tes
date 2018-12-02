package com.vt.disposisibandung.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.vt.disposisibandung.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;

/**
 * Created by irvan on 7/6/15.
 */
@EViewGroup(R.layout.layout_grid_item_add_photo)
public class AddPhotoGridItemView extends RelativeLayout {

    private OnAddPhotoClickListener onAddPhotoClickListener;

    public AddPhotoGridItemView(Context context) {
        super(context);
    }

    public AddPhotoGridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddPhotoGridItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Click(R.id.buttonAddPhoto)
    protected void addPhotoClicked() {
        if (onAddPhotoClickListener != null) onAddPhotoClickListener.onAddPhotoClicked();
    }

    public void setOnAddPhotoClickListener(OnAddPhotoClickListener onAddPhotoClickListener) {
        this.onAddPhotoClickListener = onAddPhotoClickListener;
    }

    public interface OnAddPhotoClickListener {
        void onAddPhotoClicked();
    }
}
