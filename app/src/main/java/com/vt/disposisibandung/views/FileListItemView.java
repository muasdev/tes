package com.vt.disposisibandung.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vt.disposisibandung.ImageViewerActivity_;
import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.File;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Irvan on 6/28/2015.
 */
@EViewGroup(R.layout.layout_list_item_file)
public class FileListItemView extends RelativeLayout {

    @ViewById(R.id.fileDescription)
    protected TextView textViewFileDescription;

    private List<File> files;
    private int position;

    public FileListItemView(Context context) {
        super(context);
    }

    public FileListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FileListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Click({R.id.imageFile, R.id.fileDescription})
    protected void clicked() {
        ArrayList<String> filenames = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        for (File file : files) {
            filenames.add(file.getFilename());
            descriptions.add(file.getDescription());
        }
        ImageViewerActivity_.intent(getContext()).imageList(filenames).titleList(descriptions).position(position).start();
    }

    public void setFile(List<File> files, int position) {
        this.files = files;
        this.position = position;

        if (files.get(position).getDescription() != null)
            textViewFileDescription.setText(files.get(position).getDescription());
    }
}
