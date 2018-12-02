package com.vt.disposisibandung.fragments;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vt.disposisibandung.DetailSuratActivity;
import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.Komentar;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.WebServiceHelper;
import com.vt.disposisibandung.views.CustomSwipeRefreshLayout;
import com.vt.disposisibandung.views.FileListItemView;
import com.vt.disposisibandung.views.FileListItemView_;
import com.vt.disposisibandung.views.KomentarListItemView;
import com.vt.disposisibandung.views.KomentarListItemView_;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Irvan on 6/28/2015.
 */
@EFragment(R.layout.fragment_detail_surat)
public class DetailSuratFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    @ViewById(R.id.scrollView)
    protected ScrollView scrollView;

    @ViewById(R.id.progressLoading)
    protected ProgressBar progressBarLoading;

    @ViewById(R.id.cardDataSurat)
    protected RelativeLayout cardViewDataSurat;

    @ViewById(R.id.cardFileSurat)
    protected RelativeLayout cardViewFileSurat;

    @ViewById(R.id.cardKomentar)
    protected RelativeLayout cardViewKomentar;

    @ViewById(R.id.textTanggalMasukSurat)
    protected TextView textViewTanggalMasukSurat;

    @ViewById(R.id.textJenisSurat)
    protected TextView textViewJenisSurat;

    @ViewById(R.id.textTanggalSurat)
    protected TextView textViewTanggalSurat;

    @ViewById(R.id.textNomorSurat)
    protected TextView textViewNomorSurat;

    @ViewById(R.id.textLampiranSurat)
    protected TextView textViewLampiranSurat;

    @ViewById(R.id.textPerihalSurat)
    protected TextView textViewPerihalSurat;

    @ViewById(R.id.textTembusanSurat)
    protected TextView textViewTembusanSurat;

    @ViewById(R.id.textDitujukan)
    protected TextView textViewDitujukan;

    @ViewById(R.id.sectionContainerKegiatan)
    protected RelativeLayout sectionContainerKegiatan;

    @ViewById(R.id.textNamaKegiatan)
    protected TextView textViewNamaKegiatan;

    @ViewById(R.id.textWaktuMulaiKegiatan)
    protected TextView textViewWaktuMulaiKegiatan;

    @ViewById(R.id.textWaktuSelesaiKegiatan)
    protected TextView textViewWaktuSelesaiKegiatan;

    @ViewById(R.id.textTempatKegiatan)
    protected TextView textViewTempatKegiatan;

    @ViewById(R.id.textNamaPengirim)
    protected TextView textViewNamaPengirim;

    @ViewById(R.id.textNoHpPengirim)
    protected TextView textViewNoHpPengirim;

    @ViewById(R.id.textEmailPengirim)
    protected TextView textViewEmailPengirim;

    @ViewById(R.id.fileList)
    protected LinearLayout layoutFileList;

    @ViewById(R.id.emptyFile)
    protected TextView textViewEmptyFile;

    @ViewById(R.id.komentarList)
    protected LinearLayout layoutKomentarList;

    @ViewById(R.id.editKomentar)
    protected EditText editTextKomentar;

    @ViewById(R.id.buttonKirimKomentar)
    protected Button buttonKirimKomentar;

    @ViewById(R.id.progressSubmit)
    protected ProgressBar progressBarSubmit;

    @ViewById(R.id.swipeRefreshLayout)
    protected CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private Surat surat;
    private OnDetailChangeListener onDetailChangeListener;

    private  CountDownTimer mCountDownTimer;

    @Click(R.id.buttonKirimKomentar)
    protected void submitClicked() {
        if (editTextKomentar.getText().toString().trim().length() > 0) {
            performSubmitKomentar();
        }
    }

    public void setSurat(Surat surat, final boolean isKomentar) {
        if (getActivity() != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setTargetView(scrollView);
            mSwipeRefreshLayout.canChildScrollUp();

            this.surat = surat;

            textViewTanggalMasukSurat.setText(surat.getTanggalMasuk() == null ? "-" : new SimpleDateFormat("yyyy-MM-dd").format(surat.getTanggalMasuk()));
            textViewJenisSurat.setText(surat.getJenisSurat() == null ? "-" : surat.getJenisSurat());
            textViewTanggalSurat.setText(surat.getTanggalSurat() == null ? "-" : new SimpleDateFormat("yyyy-MM-dd").format(surat.getTanggalSurat()));
            textViewNomorSurat.setText(surat.getNomorSurat() == null ? "-" : surat.getNomorSurat());
            textViewLampiranSurat.setText(surat.getLampiran() == null ? "-" : surat.getLampiran());
            textViewPerihalSurat.setText(surat.getPerihal() == null ? "-" : surat.getPerihal());
            textViewTembusanSurat.setText(surat.getTembusan() == null ? "-" : surat.getTembusan());

            String[] targets = surat.getDitujukanKepada().split(", ");
            StringBuilder stringBuilder = new StringBuilder();
            for (String target : targets) {
                stringBuilder.append("- " + target + "\n");
            }
            textViewDitujukan.setText(surat.getDitujukanKepada() == null ? "-" : stringBuilder.toString());

            if (surat.getIsKegiatan() == 1) {
                textViewNamaKegiatan.setText(surat.getNamaKegiatan() == null ? "-" : surat.getNamaKegiatan());
                textViewWaktuMulaiKegiatan.setText(surat.getWaktuMulaiKegiatan() == null ? "-" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(surat.getWaktuMulaiKegiatan().getTime()));
                textViewWaktuSelesaiKegiatan.setText(surat.getWaktuSelesaiKegiatan() == null ? "-" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(surat.getWaktuSelesaiKegiatan().getTime()));
                textViewTempatKegiatan.setText(surat.getTempatKegiatan() == null ? "-" : surat.getTempatKegiatan());
                sectionContainerKegiatan.setVisibility(View.VISIBLE);
            }

            textViewNamaPengirim.setText(surat.getNamaPengirim() == null ? "-" : surat.getNamaPengirim());
            textViewNoHpPengirim.setText(surat.getNoHpPengirim() == null ? "-" : surat.getNoHpPengirim());
            textViewEmailPengirim.setText(surat.getEmailPengirim() == null ? "-" : surat.getEmailPengirim());

            layoutFileList.removeAllViews();
            if (surat.getFiles() != null && !surat.getFiles().isEmpty()) {
                for (int i = 0; i < surat.getFiles().size(); i++) {
                    FileListItemView view = FileListItemView_.build(getActivity());
                    view.setFile(surat.getFiles(), i);
                    layoutFileList.addView(view);
                }
            } else {
                textViewEmptyFile.setVisibility(View.VISIBLE);
            }

            layoutKomentarList.removeAllViews();
            if (surat.getCommentList() != null && !surat.getCommentList().isEmpty()) {
                for (Komentar komentar : surat.getCommentList()) {
                    KomentarListItemView view = KomentarListItemView_.build(getActivity());
                    view.setKomentar(komentar);
                    layoutKomentarList.addView(view);
                }
            }

            progressBarLoading.setVisibility(View.GONE);
            cardViewDataSurat.setVisibility(View.VISIBLE);
            cardViewFileSurat.setVisibility(View.VISIBLE);
            cardViewKomentar.setVisibility(View.VISIBLE);

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    if (isKomentar) {
                        scrollView.scrollTo(0, cardViewKomentar.getBottom());
                    } else {
                        scrollView.scrollTo(0, 0);
                    }
                }
            });
        }
    }

    public void setOnDetailChangeListener(OnDetailChangeListener onDetailChangeListener) {
        this.onDetailChangeListener = onDetailChangeListener;
    }

    private void performSubmitKomentar() {
        buttonKirimKomentar.setEnabled(false);
        progressBarSubmit.setVisibility(View.VISIBLE);

        WebServiceHelper.getInstance().getServices().submitKomentar(surat.getId(), editTextKomentar.getText().toString(), new Callback() {
            @Override
            public void success(Object o) {
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                buttonKirimKomentar.setEnabled(true);
                progressBarSubmit.setVisibility(View.GONE);
            }

            @Override
            public void success(Object o, Response response) {
                Toast.makeText(getActivity(), "Komentar berhasil dikirim", Toast.LENGTH_SHORT).show();

                if (onDetailChangeListener != null) {
                    editTextKomentar.setText("");
                    cardViewDataSurat.setVisibility(View.GONE);
                    cardViewFileSurat.setVisibility(View.GONE);
                    cardViewKomentar.setVisibility(View.GONE);
                    progressBarLoading.setVisibility(View.VISIBLE);

                    onDetailChangeListener.onDetailChanged();
                }

                buttonKirimKomentar.setEnabled(true);
                progressBarSubmit.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(getActivity(), "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                buttonKirimKomentar.setEnabled(true);
                progressBarSubmit.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRefresh() {
        ((DetailSuratActivity)getActivity()).refreshDataSurat();
        setRefresh();
    }

    private void setRefresh() {
        mCountDownTimer = new CountDownTimer(3000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                if (second % 3 == 0) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    public interface OnDetailChangeListener {
        void onDetailChanged();
    }
}
