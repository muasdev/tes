package com.vt.disposisibandung.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by irvan on 7/2/15.
 */
public class JenisSuratSpinnerAdapter extends ArrayAdapter<String> {

    public JenisSuratSpinnerAdapter(Context context, List<String> titles) {
        super(context, android.R.layout.simple_spinner_item, titles);
    }
}
