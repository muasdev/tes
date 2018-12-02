package com.vt.disposisibandung.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.Komentar;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;

/**
 * Created by Irvan on 6/28/2015.
 */
@EViewGroup(R.layout.layout_list_item_komentar)
public class KomentarListItemView extends RelativeLayout {

    @ViewById(R.id.textTanggalKomentar)
    protected TextView textViewTanggalKomentar;

    @ViewById(R.id.textJabatan)
    protected TextView textViewJabatan;

    @ViewById(R.id.textKomentar)
    protected TextView textViewKomentar;

    public KomentarListItemView(Context context) {
        super(context);
    }

    public KomentarListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KomentarListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setKomentar(Komentar komentar) {
        textViewTanggalKomentar.setText(komentar.getTanggal() == null ? "-" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(komentar.getTanggal().getTime()));
        textViewJabatan.setText("Dari: " + (komentar.getJabatan() == null ? "-" : komentar.getJabatan()));
        textViewKomentar.setText(komentar.getKomentar() == null ? "-" : komentar.getKomentar());
    }
}
