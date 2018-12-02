package com.vt.disposisibandung.fragments;

import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.User;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.DatabaseHelper;
import com.vt.disposisibandung.utils.WebServiceHelper;
import com.vt.disposisibandung.utils.transformations.RoundedTransformation;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.ViewById;

import retrofit.RetrofitError;

/**
 * Created by irvan on 6/25/15.
 */
@EFragment(R.layout.fragment_drawer)
public class DrawerFragment extends Fragment {

    private static final String FRAGMENT_ARG_INIT_ITEM_POSITION = "init_item_position";

    @ViewById(R.id.profileImageView)
    protected ImageView imageViewProfile;

    @ViewById(R.id.profileName)
    protected TextView textViewNama;

    @ViewById(R.id.profileJabatan)
    protected TextView textViewJabatan;

    @ViewById(R.id.newKotakMasuk)
    protected TextView textViewNewKotakMasuk;

    @ViewById(R.id.newSuratUndangan)
    protected TextView textViewNewSuratUndangan;

    @ViewById(R.id.newAudiensi)
    protected TextView textViewNewAudiensi;

    @ViewById(R.id.newSuratUmum)
    protected TextView textViewNewSuratUmum;

    @ViewById(R.id.newSuratKeluar)
    protected TextView textViewNewSuratKeluar;

    @ViewById(R.id.newSuratBelum)
    protected TextView textViewNewSuratBelum;

    @ViewById(R.id.newSuratSelesai)
    protected TextView textViewNewSuratSelesai;

    @ViewById(R.id.newDisposisiMasuk)
    protected TextView textViewNewDisposisiMasuk;

    @ViewById(R.id.newDisposisiKeluar)
    protected TextView textViewNewDisposisiKeluar;

    @ViewById(R.id.buttonKotakMasuk)
    protected RelativeLayout buttonKotakMasuk;

    @ViewById(R.id.buttonSuratUndangan)
    protected RelativeLayout buttonSuratUndangan;

    @ViewById(R.id.buttonAudiensi)
    protected RelativeLayout buttonAudiensi;

    @ViewById(R.id.buttonSuratUmum)
    protected RelativeLayout buttonSuratUmum;

    @ViewById(R.id.buttonSuratKeluar)
    protected RelativeLayout buttonSuratKeluar;

    @ViewById(R.id.buttonSuratBelum)
    protected RelativeLayout buttonSuratBelum;

    @ViewById(R.id.buttonSuratSelesai)
    protected RelativeLayout buttonSuratSelesai;

    @ViewById(R.id.buttonDisposisiMasuk)
    protected RelativeLayout buttonDisposisiMasuk;

    @ViewById(R.id.buttonDisposisiKeluar)
    protected RelativeLayout buttonDisposisiKeluar;

    @ViewById(R.id.buttonArsip)
    protected RelativeLayout buttonArsip;

    @ViewById(R.id.buttonJadwal)
    protected RelativeLayout buttonJadwal;

    @FragmentArg
    protected int initItemPosition = 0;

    private OnDrawerItemClickListener onDrawerItemClickListener;

    @AfterViews
    protected void initViews() {
        Picasso.with(getActivity())
                .load(R.drawable.ic_profile_placeholder)
                .resize(80, 80)
                .centerInside()
                .transform(new RoundedTransformation())
                .into(imageViewProfile);

        User user = DatabaseHelper.getInstance().getRealm().where(User.class).findFirst();
        if (user != null) {
            Picasso.with(getActivity())
                    .load(user.getProfilePictureUrl())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .resize(80, 80)
                    .centerInside()
                    .transform(new RoundedTransformation())
                    .into(imageViewProfile);

            textViewNama.setText(user.getName());
            textViewJabatan.setText(user.getJabatan());

            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .edit().putString("role_id", user.role_id).commit();
        }

        switch (initItemPosition) {
            case 0: {
                kotakMasukClicked();
                break;
            }
            case 1: {
                suratUndanganClicked();
                break;
            }
            case 2: {
                suratAudiensiClicked();
                break;
            }
            case 3: {
                suratUmumClicked();
                break;
            }
            case 4: {
                suratKeluarClicked();
                break;
            }
            case 5: {
                suratBelumClicked();
                break;
            }
            case 6: {
                suratSelesaiClicked();
                break;
            }
            case 7: {
                disposisiMasukClicked();
                break;
            }
            case 8: {
                disposisiKeluarClicked();
                break;
            }
            case 9: {
                arsipClicked();
                break;
            }
            case 10: {
                jadwalClicked();
                break;
            }
        }

        performGetUserProfile();
    }

    @Click(R.id.buttonKotakMasuk)
    protected void kotakMasukClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.parseColor("#dddddd"));
        buttonSuratUndangan.setBackgroundColor(Color.TRANSPARENT);
        buttonAudiensi.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUmum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratBelum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratSelesai.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonArsip.setBackgroundColor(Color.TRANSPARENT);
        buttonJadwal.setBackgroundColor(Color.TRANSPARENT);

        onDrawerItemClickListener.onDrawerItemClick(0);
    }

    @Click(R.id.buttonSuratUndangan)
    protected void suratUndanganClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUndangan.setBackgroundColor(Color.parseColor("#dddddd"));
        buttonAudiensi.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUmum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratBelum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratSelesai.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonArsip.setBackgroundColor(Color.TRANSPARENT);
        buttonJadwal.setBackgroundColor(Color.TRANSPARENT);

        onDrawerItemClickListener.onDrawerItemClick(1);
    }

    @Click(R.id.buttonAudiensi)
    protected void suratAudiensiClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUndangan.setBackgroundColor(Color.TRANSPARENT);
        buttonAudiensi.setBackgroundColor(Color.parseColor("#dddddd"));
        buttonSuratUmum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratBelum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratSelesai.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonArsip.setBackgroundColor(Color.TRANSPARENT);
        buttonJadwal.setBackgroundColor(Color.TRANSPARENT);

        onDrawerItemClickListener.onDrawerItemClick(2);
    }

    @Click(R.id.buttonSuratUmum)
    protected void suratUmumClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUndangan.setBackgroundColor(Color.TRANSPARENT);
        buttonAudiensi.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUmum.setBackgroundColor(Color.parseColor("#dddddd"));
        buttonSuratKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratBelum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratSelesai.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonArsip.setBackgroundColor(Color.TRANSPARENT);
        buttonJadwal.setBackgroundColor(Color.TRANSPARENT);

        onDrawerItemClickListener.onDrawerItemClick(3);
    }

    @Click(R.id.buttonSuratKeluar)
    protected void suratKeluarClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUndangan.setBackgroundColor(Color.TRANSPARENT);
        buttonAudiensi.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUmum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratKeluar.setBackgroundColor(Color.parseColor("#dddddd"));
        buttonSuratBelum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratSelesai.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonArsip.setBackgroundColor(Color.TRANSPARENT);
        buttonJadwal.setBackgroundColor(Color.TRANSPARENT);

        onDrawerItemClickListener.onDrawerItemClick(4);
    }

    @Click(R.id.buttonSuratBelum)
    protected void suratBelumClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUndangan.setBackgroundColor(Color.TRANSPARENT);
        buttonAudiensi.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUmum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratBelum.setBackgroundColor(Color.parseColor("#dddddd"));
        buttonSuratSelesai.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonArsip.setBackgroundColor(Color.TRANSPARENT);
        buttonJadwal.setBackgroundColor(Color.TRANSPARENT);

        onDrawerItemClickListener.onDrawerItemClick(5);
    }

    @Click(R.id.buttonSuratSelesai)
    protected void suratSelesaiClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUndangan.setBackgroundColor(Color.TRANSPARENT);
        buttonAudiensi.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUmum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratBelum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratSelesai.setBackgroundColor(Color.parseColor("#dddddd"));
        buttonDisposisiMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonArsip.setBackgroundColor(Color.TRANSPARENT);
        buttonJadwal.setBackgroundColor(Color.TRANSPARENT);

        onDrawerItemClickListener.onDrawerItemClick(6);
    }

    @Click(R.id.buttonDisposisiMasuk)
    protected void disposisiMasukClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUndangan.setBackgroundColor(Color.TRANSPARENT);
        buttonAudiensi.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUmum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratBelum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratSelesai.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiMasuk.setBackgroundColor(Color.parseColor("#dddddd"));
        buttonDisposisiKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonArsip.setBackgroundColor(Color.TRANSPARENT);
        buttonJadwal.setBackgroundColor(Color.TRANSPARENT);

        onDrawerItemClickListener.onDrawerItemClick(7);
    }

    @Click(R.id.buttonDisposisiKeluar)
    protected void disposisiKeluarClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUndangan.setBackgroundColor(Color.TRANSPARENT);
        buttonAudiensi.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUmum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratBelum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratSelesai.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiKeluar.setBackgroundColor(Color.parseColor("#dddddd"));
        buttonArsip.setBackgroundColor(Color.TRANSPARENT);
        buttonJadwal.setBackgroundColor(Color.TRANSPARENT);

        onDrawerItemClickListener.onDrawerItemClick(8);
    }

    @Click(R.id.buttonArsip)
    protected void arsipClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUndangan.setBackgroundColor(Color.TRANSPARENT);
        buttonAudiensi.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUmum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratBelum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratSelesai.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonArsip.setBackgroundColor(Color.parseColor("#dddddd"));
        buttonJadwal.setBackgroundColor(Color.TRANSPARENT);

        onDrawerItemClickListener.onDrawerItemClick(9);
    }

    @Click(R.id.buttonJadwal)
    protected void jadwalClicked() {
        buttonKotakMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUndangan.setBackgroundColor(Color.TRANSPARENT);
        buttonAudiensi.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratUmum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratBelum.setBackgroundColor(Color.TRANSPARENT);
        buttonSuratSelesai.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiMasuk.setBackgroundColor(Color.TRANSPARENT);
        buttonDisposisiKeluar.setBackgroundColor(Color.TRANSPARENT);
        buttonArsip.setBackgroundColor(Color.TRANSPARENT);
        buttonJadwal.setBackgroundColor(Color.parseColor("#dddddd"));

        onDrawerItemClickListener.onDrawerItemClick(10);
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshData();
    }

    public void refreshData() {
        long count = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("suratMasuk", true).equalTo("isRead", false).count();

        if (count == 0) {
            textViewNewKotakMasuk.setVisibility(View.GONE);
        } else {
            textViewNewKotakMasuk.setVisibility(View.VISIBLE);
            textViewNewKotakMasuk.setText("" + count);
        }

        count = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("suratUndangan", true).equalTo("isRead", false).count();
        if (count == 0) {
            textViewNewSuratUndangan.setVisibility(View.GONE);
        } else {
            textViewNewSuratUndangan.setVisibility(View.VISIBLE);
            textViewNewSuratUndangan.setText("" + count);
        }

        count = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("suratAudiensi", true).equalTo("isRead", false).count();
        if (count == 0) {
            textViewNewAudiensi.setVisibility(View.GONE);
        } else {
            textViewNewAudiensi.setVisibility(View.VISIBLE);
            textViewNewAudiensi.setText("" + count);
        }

        count = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("suratUmum", true).equalTo("isRead", false).count();
        if (count == 0) {
            textViewNewSuratUmum.setVisibility(View.GONE);
        } else {
            textViewNewSuratUmum.setVisibility(View.VISIBLE);
            textViewNewSuratUmum.setText("" + count);
        }

        count = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("suratKeluar", true).equalTo("isRead", false).count();
        if (count == 0) {
            textViewNewSuratKeluar.setVisibility(View.GONE);
        } else {
            textViewNewSuratKeluar.setVisibility(View.VISIBLE);
            textViewNewSuratKeluar.setText("" + count);
        }

        count = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("suratBelum", true).equalTo("isRead", false).count();
        if (count == 0) {
            textViewNewSuratBelum.setVisibility(View.GONE);
        } else {
            textViewNewSuratBelum.setVisibility(View.VISIBLE);
            textViewNewSuratBelum.setText("" + count);
        }

        count = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("suratSelesai", true).equalTo("isRead", false).count();
        if (count == 0) {
            textViewNewSuratSelesai.setVisibility(View.GONE);
        } else {
            textViewNewSuratSelesai.setVisibility(View.VISIBLE);
            textViewNewSuratSelesai.setText("" + count);
        }

        count = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("disposisiMasuk", true).equalTo("isRead", false).count();
        if (count == 0) {
            textViewNewDisposisiMasuk.setVisibility(View.GONE);
        } else {
            textViewNewDisposisiMasuk.setVisibility(View.VISIBLE);
            textViewNewDisposisiMasuk.setText("" + count);
        }

        count = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("disposisiKeluar", true).equalTo("isRead", false).count();
        if (count == 0) {
            textViewNewDisposisiKeluar.setVisibility(View.GONE);
        } else {
            textViewNewDisposisiKeluar.setVisibility(View.VISIBLE);
            textViewNewDisposisiKeluar.setText("" + count);
        }
    }

    public void setOnDrawerItemClickListener(OnDrawerItemClickListener onDrawerItemClickListener) {
        this.onDrawerItemClickListener = onDrawerItemClickListener;
    }

    private void performGetUserProfile() {
        WebServiceHelper.getInstance().getServices().getUserProfile(new Callback<User>() {
            @Override
            public void success(User user) {
                DatabaseHelper.getInstance().getRealm().beginTransaction();
                DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(user);
                DatabaseHelper.getInstance().getRealm().commitTransaction();

                Picasso.with(getActivity())
                        .load(user.getProfilePictureUrl())
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .resize(80, 80)
                        .centerInside()
                        .transform(new RoundedTransformation())
                        .into(imageViewProfile);

                textViewNama.setText(user.getName());
                textViewJabatan.setText(user.getJabatan());
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(getActivity(), "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnDrawerItemClickListener {
        void onDrawerItemClick(int position);
    }
}
