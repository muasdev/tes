package com.vt.disposisibandung.fragments;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vt.disposisibandung.DetailSuratActivity;
import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.Disposisi;
import com.vt.disposisibandung.models.DisposisiDefault;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.User;
import com.vt.disposisibandung.models.UserGroup;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.WebServiceHelper;
import com.vt.disposisibandung.views.CustomSwipeRefreshLayout;
import com.vt.disposisibandung.views.DisposisiListItemView;
import com.vt.disposisibandung.views.DisposisiListItemView_;
import com.vt.disposisibandung.views.TargetGroupItemView;
import com.vt.disposisibandung.views.TargetGroupItemView_;
import com.vt.disposisibandung.views.TargetListItemView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.RealmList;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Irvan on 6/28/2015.
 */

@EFragment(R.layout.fragment_detail_surat_disposisi)
public class DetailSuratDisposisiFragment extends Fragment implements TargetListItemView.OnTargetCheckedChangeListener, RadioGroup.OnCheckedChangeListener, SwipeRefreshLayout.OnRefreshListener {

    @ViewById(R.id.scrollView)
    protected ScrollView scrollView;
    @ViewById(R.id.progressLoading)
    protected ProgressBar progressBarLoading;
    @ViewById(R.id.cardDisposisi)
    protected RelativeLayout cardViewDisposisi;
    @ViewById(R.id.cardKirimDisposisi)
    protected RelativeLayout cardViewKirimDisposisi;
    @ViewById(R.id.cardStatusSurat)
    protected RelativeLayout cardViewStatus;
    @ViewById(R.id.messageList)
    protected LinearLayout layoutMessageList;
    @ViewById(R.id.emptyKirimDisposisi)
    protected TextView textViewEmptyKirimDisposisi;
    @ViewById(R.id.emptyKirimDisposisiDone)
    protected TextView textViewEmptyKirimDisposisiDone;
    @ViewById(R.id.sectionContainerKirimDisposisi)
    protected RelativeLayout sectionContainerKirimDisposisi;
    @ViewById(R.id.textTanggalMasukSurat)
    protected TextView textViewTanggalMasukSurat;
    @ViewById(R.id.textPerihalSurat)
    protected TextView textViewPerihalSurat;
    @ViewById(R.id.disposisiTargetGroupList)
    protected LinearLayout layoutDisposisiTargetGroupList;
    @ViewById(R.id.textTanggal)
    protected TextView textViewTanggal;
    @ViewById(R.id.editKeterangan)
    protected EditText editTextKeterangan;
    @ViewById(R.id.chkStatus)
    protected CheckBox checkboxStatus;
    @ViewById(R.id.txtStatus)
    protected TextView textviewStatus;
    @ViewById(R.id.buttonSubmitDisposisi)
    protected Button buttonSubmitDisposisi;
    @ViewById(R.id.buttonSubmitStatus)
    protected Button buttonSubmitStatus;
    @ViewById(R.id.progressSubmit)
    protected ProgressBar progressBarSubmit;
    @ViewById(R.id.progressSubmitStatus)
    protected ProgressBar progressBarSubmitStatus;
    @ViewById(R.id.radiogroup)
    protected RadioGroup rdGroup;
    @ViewById(R.id.swipeRefreshLayout)
    protected CustomSwipeRefreshLayout mSwipeRefreshLayout;

    private RealmList<DisposisiDefault> dpDefaultsList;
    private String template_disposisi = "";
    private Surat surat;
    private Map<Long, Boolean> selectedTargets;
    private OnDisposisiChangeListener onDisposisiChangeListener;


    private CountDownTimer mCountDownTimer;

    @AfterViews
    protected void initViews() {
        selectedTargets = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        textViewTanggal.setText(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
    }

    @Click(R.id.buttonSubmitDisposisi)
    protected void submitClicked() {
        if (selectedTargets.containsValue(true))
            performSubmitDisposisi();
        else
            Toast.makeText(getActivity(), "Maaf, silahkan tentukan terlebih dahulu kepada siapa surat ini akan didisposisikan.", Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.buttonSubmitStatus)
    protected void submitStatusClicked() {
        if (checkboxStatus.isChecked())
            performSubmitStatus("sudah_selesai");
        else
            performSubmitStatus("belum");
    }

    @Override
    public void onTargetCheckedChanged(User user, boolean isChecked) {
        selectedTargets.put(user.getId(), isChecked);
    }

    public void setSurat(Surat surat) {
        if (getActivity() != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setTargetView(scrollView);
            mSwipeRefreshLayout.canChildScrollUp();

            this.surat = surat;

            if (surat.getDisposisiList() != null && !surat.getDisposisiList().isEmpty()) {
                layoutMessageList.removeAllViews();

                for (Disposisi disposisi : surat.getDisposisiList()) {
                    DisposisiListItemView view = DisposisiListItemView_.build(getActivity());
                    view.setDisposisi(disposisi);
                    view.setDisposisiTargetGroupList(surat.getDisposisiTargetList());
                    view.setOnDisposisiChangeListener(onDisposisiChangeListener);
                    layoutMessageList.addView(view);
                }

                cardViewDisposisi.setVisibility(View.VISIBLE);
                cardViewStatus.setVisibility(View.VISIBLE);

                if (surat.getStatusSurat().equals("sudah_selesai")) {
                    checkboxStatus.setVisibility(View.GONE);
                    textviewStatus.setVisibility(View.VISIBLE);
                    buttonSubmitStatus.setVisibility(View.GONE);
                } else {
                    checkboxStatus.setVisibility(View.VISIBLE);
                    textviewStatus.setVisibility(View.GONE);
                    buttonSubmitStatus.setVisibility(View.VISIBLE);
                }

            }

            switch (surat.getStateDisposisi()) {
                case 0: {
                    cardViewStatus.setVisibility(View.VISIBLE);
                    textViewEmptyKirimDisposisi.setVisibility(View.VISIBLE);
                    textViewEmptyKirimDisposisiDone.setVisibility(View.GONE);
                    sectionContainerKirimDisposisi.setVisibility(View.GONE);
                    break;
                }
                case 1: {
                    textViewTanggalMasukSurat.setText(surat.getTanggalMasuk() == null ? "-" : new SimpleDateFormat("yyyy-MM-dd").format(surat.getTanggalMasuk()));
                    textViewPerihalSurat.setText(surat.getPerihal() == null ? "-" : surat.getPerihal());

                    if (surat.getDisposisiTargetList() != null && !surat.getDisposisiTargetList().isEmpty()) {
                        layoutDisposisiTargetGroupList.removeAllViews();

                        for (UserGroup userGroup : surat.getDisposisiTargetList()) {
                            TargetGroupItemView view = TargetGroupItemView_.build(getActivity());
                            view.setOnTargetCheckedChangeListener(this);
                            view.setUserGroup(userGroup);
                            layoutDisposisiTargetGroupList.addView(view);

                            for (User user : userGroup.getUsers()) {
                                selectedTargets.put(user.getId(), false);
                            }
                        }
                    }

                    getDefaultDisposisi();

                    textViewEmptyKirimDisposisi.setVisibility(View.GONE);
                    textViewEmptyKirimDisposisiDone.setVisibility(View.GONE);
                    sectionContainerKirimDisposisi.setVisibility(View.VISIBLE);
                    break;
                }
                case 2: {
                    textViewEmptyKirimDisposisi.setVisibility(View.GONE);
                    textViewEmptyKirimDisposisiDone.setVisibility(View.VISIBLE);
                    sectionContainerKirimDisposisi.setVisibility(View.GONE);
                    break;
                }
            }

            progressBarLoading.setVisibility(View.GONE);
            cardViewKirimDisposisi.setVisibility(View.VISIBLE);
        }
    }

    public void setOnDisposisiChangeListener(OnDisposisiChangeListener onDisposisiChangeListener) {
        this.onDisposisiChangeListener = onDisposisiChangeListener;
    }

    private void getDefaultDisposisi() {
        WebServiceHelper.getInstance().getServices().getDefaultDisposisi(new Callback<RealmList<DisposisiDefault>>() {
            @Override
            public void success(RealmList<DisposisiDefault> disposisiDefaultsList) {
                dpDefaultsList = disposisiDefaultsList;
                addRadioButtons(disposisiDefaultsList);
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                buttonSubmitDisposisi.setEnabled(true);
                progressBarSubmit.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(getActivity(), "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSubmitDisposisi() {
        buttonSubmitDisposisi.setEnabled(false);
        progressBarSubmit.setVisibility(View.VISIBLE);


        WebServiceHelper.getInstance().getServices().submitDisposisi(surat.getId(), editTextKeterangan.getText().toString(), selectedTargets, new Callback() {
            @Override
            public void success(Object o) {
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                buttonSubmitDisposisi.setEnabled(true);
                progressBarSubmit.setVisibility(View.GONE);
            }

            @Override
            public void success(Object o, Response response) {
                Toast.makeText(getActivity(), "Disposisi berhasil dikirim", Toast.LENGTH_SHORT).show();

                if (onDisposisiChangeListener != null) {
                    cardViewDisposisi.setVisibility(View.GONE);
                    cardViewKirimDisposisi.setVisibility(View.GONE);
                    progressBarLoading.setVisibility(View.VISIBLE);

                    onDisposisiChangeListener.onDisposisiChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(getActivity(), "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                buttonSubmitDisposisi.setEnabled(true);
                progressBarSubmit.setVisibility(View.GONE);
            }
        });
    }

    private void performSubmitStatus(String status) {
        buttonSubmitStatus.setEnabled(false);
        progressBarSubmitStatus.setVisibility(View.VISIBLE);

        WebServiceHelper.getInstance().getServices().submitStatusSurat(surat.getId(), status, new Callback() {
            @Override
            public void success(Object o) {
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                buttonSubmitStatus.setEnabled(true);
                progressBarSubmitStatus.setVisibility(View.GONE);
            }

            @Override
            public void success(Object o, Response response) {
                Toast.makeText(getActivity(), "Status berhasil dikirim", Toast.LENGTH_SHORT).show();

                checkboxStatus.setVisibility(View.GONE);
                textviewStatus.setVisibility(View.VISIBLE);
                buttonSubmitStatus.setVisibility(View.GONE);
                progressBarSubmitStatus.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(getActivity(), "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                buttonSubmitStatus.setEnabled(true);
                progressBarSubmitStatus.setVisibility(View.GONE);
            }
        });
    }

    private void addRadioButtons(RealmList<DisposisiDefault> disposisiDefaultsList) {
        rdGroup.removeAllViews();

        RadioGroup ll = new RadioGroup(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(0, 0, 5, 5);
        ll.setOnCheckedChangeListener(this);
        ll.removeAllViews();

        int i = 0;
        for (DisposisiDefault dd : disposisiDefaultsList) {
            AppCompatRadioButton rdbtn = new AppCompatRadioButton(getActivity());
            rdbtn.setId(i);
            rdbtn.setText(dd.getName());

            if (i == 0)
                rdbtn.setChecked(true);

            ll.addView(rdbtn);

            i = i + 1;
        }

        rdGroup.addView(ll);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        template_disposisi = dpDefaultsList.get(checkedId).getIsiPesan() + " ";
        Log.i("id rdbutton", String.valueOf(checkedId));
        Log.i("isi pesan", template_disposisi);

        editTextKeterangan.setText(template_disposisi);
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

    public interface OnDisposisiChangeListener {
        void onDisposisiChanged();
    }
}
