package com.vt.disposisibandung.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vt.disposisibandung.DetailSuratActivity_;
import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.Surat;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;

/**
 * Created by irvan on 6/26/15.
 */
@EViewGroup(R.layout.layout_list_item_surat)
public class SuratListItemView extends RelativeLayout {

    @ViewById(R.id.pengirimSurat)
    protected TextView textViewPengirim;

    @ViewById(R.id.perihalSurat)
    protected TextView textViewPerihal;

    @ViewById(R.id.tanggalSurat)
    protected TextView textViewTanggal;

    @ViewById(R.id.status_surat)
    protected ImageView imgStatus;

    @ViewById(R.id.badgeNew)
    protected TextView textViewBadgeNew;

    @ViewById(R.id.container)
    protected RelativeLayout container;

    private Surat surat;
    private OnListItemClickListener onListItemClickListener;

    public SuratListItemView(Context context) {
        super(context);
    }

    public SuratListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuratListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Click(R.id.container)
    protected void clicked() {
        DetailSuratActivity_.intent(getContext()).suratId(surat.getId()).suratType(surat.getType()).start();
        if (onListItemClickListener != null)
            onListItemClickListener.onListItemClicked(surat.getId());
    }

    public void setSurat(Surat surat) {
        this.surat = surat;

        if (surat.isRead()) {
            container.setBackgroundResource(R.drawable.sel_surat_item);
        } else {
            container.setBackgroundResource(R.drawable.sel_surat_item_new);
        }

        textViewPengirim.setText(surat.getNamaPengirim() == null ? "-" : surat.getNamaPengirim());
        textViewPerihal.setText(surat.getPerihal() == null ? "-" : surat.getPerihal());
        textViewTanggal.setText(surat.getTanggalMasuk() == null ? "-" : new SimpleDateFormat("dd/MM/yyyy").format(surat.getTanggalMasuk()));

        textViewBadgeNew.setVisibility(surat.isNew() ? VISIBLE : GONE);
        imgStatus.setVisibility((surat.getStatusSurat()).equals("sudah_selesai") ? VISIBLE : GONE);
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }

    public interface OnListItemClickListener {
        void onListItemClicked(long id);
    }
}
