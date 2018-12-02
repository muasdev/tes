package com.vt.disposisibandung.views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vt.disposisibandung.R;
import com.vt.disposisibandung.fragments.DetailSuratDisposisiFragment;
import com.vt.disposisibandung.models.Disposisi;
import com.vt.disposisibandung.models.User;
import com.vt.disposisibandung.models.UserGroup;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.WebServiceHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Irvan on 6/28/2015.
 */
@EViewGroup(R.layout.layout_list_item_disposisi)
public class DisposisiListItemView extends RelativeLayout implements TargetListItemView.OnTargetCheckedChangeListener {

    @ViewById(R.id.textTanggalDisposisi)
    protected TextView textViewTanggalDisposisi;

    @ViewById(R.id.textJabatan)
    protected TextView textViewJabatan;

    @ViewById(R.id.textKeterangan)
    protected TextView textViewKeterangan;

    @ViewById(R.id.textKepada)
    protected TextView textViewKepada;

    @ViewById(R.id.buttonEditDisposisi)
    protected ImageButton buttonEditDisposisi;

    @ViewById(R.id.progressLoading)
    protected ProgressBar progressBarLoading;

    private Disposisi disposisi;
    private List<UserGroup> disposisiTargetGroupList;
    private Map<Long, Boolean> selectedTargets;
    private DetailSuratDisposisiFragment.OnDisposisiChangeListener onDisposisiChangeListener;

    public DisposisiListItemView(Context context) {
        super(context);
    }

    public DisposisiListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisposisiListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterViews
    protected void initViews() {
        selectedTargets = new HashMap<>();
    }

    @Click(R.id.buttonEditDisposisi)
    protected void editDisposisi() {
        View dialogView = View.inflate(getContext(), R.layout.dialog_edit_disposisi, null);
        LinearLayout layoutDisposisiTarget = (LinearLayout) dialogView.findViewById(R.id.layoutDisposisiTarget);
        LinearLayout layoutDisposisiTargetList = (LinearLayout) dialogView.findViewById(R.id.disposisiTargetGroupList);
        final EditText editTextKeterangan = (EditText) dialogView.findViewById(R.id.editKeterangan);

        if (disposisi.getEditType() == 1 && disposisiTargetGroupList != null && !disposisiTargetGroupList.isEmpty()) {
            layoutDisposisiTarget.setVisibility(VISIBLE);

            List<String> kepadaList = Arrays.asList(disposisi.getKepada().split(", "));

            for (UserGroup userGroup : disposisiTargetGroupList) {
                TargetGroupItemView view = TargetGroupItemView_.build(getContext());
                view.setOnTargetCheckedChangeListener(this);
                view.setKepadaList(kepadaList);
                view.setUserGroup(userGroup);

                for (User user : userGroup.getUsers()) {
                    if (kepadaList.contains(user.getJabatan())) {
                        selectedTargets.put(user.getId(), true);
                    }
                }

                layoutDisposisiTargetList.addView(view);
            }
        } else {
            layoutDisposisiTarget.setVisibility(GONE);
        }
        editTextKeterangan.setText(disposisi.getKeterangan());

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.title_edit_disposisi)
                .setView(dialogView)
                .setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disposisi.setKeterangan(editTextKeterangan.getText().toString());
                        performEditDisposisi();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public void onTargetCheckedChanged(User user, boolean isChecked) {
        selectedTargets.put(user.getId(), isChecked);
    }

    public void setDisposisi(Disposisi disposisi) {
        this.disposisi = disposisi;

        textViewTanggalDisposisi.setText(disposisi.getTanggal() == null ? "-" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(disposisi.getTanggal().getTime()));
        textViewJabatan.setText("Dari: " + (disposisi.getJabatan() == null ? "-" : disposisi.getJabatan()));
        textViewKeterangan.setText(disposisi.getKeterangan() == null ? "-" : disposisi.getKeterangan());

        String[] targets = disposisi.getKepada().split(", ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String target : targets) {
            stringBuilder.append("- " + target + "\n");
        }
        textViewKepada.setText(disposisi.getKepada() == null ? "-" : stringBuilder.toString());

        buttonEditDisposisi.setVisibility(disposisi.getCanEdit() ? VISIBLE : GONE);
    }

    public void setDisposisiTargetGroupList(List<UserGroup> disposisiTargetGroupList) {
        this.disposisiTargetGroupList = disposisiTargetGroupList;
    }

    public void setOnDisposisiChangeListener(DetailSuratDisposisiFragment.OnDisposisiChangeListener onDisposisiChangeListener) {
        this.onDisposisiChangeListener = onDisposisiChangeListener;
    }

    private void performEditDisposisi() {
        textViewKeterangan.setVisibility(GONE);
        textViewKepada.setVisibility(GONE);
        progressBarLoading.setVisibility(VISIBLE);

        WebServiceHelper.getInstance().getServices().editDisposisi(disposisi.getId(), disposisi.getKeterangan(), selectedTargets, disposisi.getEditType(), new Callback() {
            @Override
            public void success(Object o) {
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                textViewKeterangan.setVisibility(GONE);
                textViewKepada.setVisibility(GONE);
                progressBarLoading.setVisibility(VISIBLE);
            }

            @Override
            public void success(Object o, Response response) {
                if (onDisposisiChangeListener != null)
                    onDisposisiChangeListener.onDisposisiChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                textViewKeterangan.setVisibility(GONE);
                textViewKepada.setVisibility(GONE);
                progressBarLoading.setVisibility(VISIBLE);
            }
        });
    }
}
