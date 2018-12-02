package com.vt.disposisibandung;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.vt.disposisibandung.fragments.DetailSuratDisposisiFragment;
import com.vt.disposisibandung.fragments.DetailSuratFragment;
import com.vt.disposisibandung.fragments.adapters.DetailSuratPagerAdapter;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.services.GcmListenerService;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.DatabaseHelper;
import com.vt.disposisibandung.utils.WebServiceHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import retrofit.RetrofitError;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Irvan on 6/28/2015.
 */

@EActivity(R.layout.activity_detail_surat)
public class DetailSuratActivity extends AppCompatActivity implements DetailSuratFragment.OnDetailChangeListener, DetailSuratDisposisiFragment.OnDisposisiChangeListener {

    private static final String EXTRA_SURAT_ID = "surat_id";
    private static final String EXTRA_SURAT_TYPE = "surat_type";
    private static final String EXTRA_IS_KOMENTAR = "is_komentar";

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;

    @ViewById(R.id.tabStrip)
    protected PagerSlidingTabStrip tabStrip;

    @ViewById(R.id.viewPager)
    protected ViewPager viewPager;

    @ViewById(R.id.buttonRetry)
    protected Button buttonRetry;

    @Extra(EXTRA_SURAT_ID)
    protected long suratId = -1;

    @Extra(EXTRA_SURAT_TYPE)
    protected int suratType = -1;

    @Extra(EXTRA_IS_KOMENTAR)
    protected boolean isKomentar = false;

    private DetailSuratPagerAdapter pagerAdapter;
    private Boolean arsip;

    @AfterViews
    protected void initViews() {
        NotificationManagerCompat.from(this).cancel(GcmListenerService.NOTIF_ID);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_detail_surat);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pagerAdapter = new DetailSuratPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        if (suratType == Surat.TYPE_DISPOSISI_MASUK || suratType == Surat.TYPE_DISPOSISI_KELUAR)
            viewPager.setCurrentItem(1);

        tabStrip.setViewPager(viewPager);

        initData();
    }

    @Click(R.id.buttonRetry)
    protected void initData() {
        buttonRetry.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);

        arsip = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("arsip", false);
        if (suratId != -1 && suratType != -1) performGetDetailSurat();
    }

    @Override
    public void onDetailChanged() {
        initData();
    }

    @Override
    public void onDisposisiChanged() {
        initData();
    }

    private void performGetDetailSurat() {
        switch (suratType) {
            case Surat.TYPE_KELUAR: {
                performGetDetailSuratKeluar();
                break;
            }
            case Surat.TYPE_DISPOSISI_KELUAR: {
                performGetDetailSuratKeluar();
                break;
            }
            default: {
                performGetDetailSuratMasuk();
                break;
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        suratId = intent.getIntExtra(EXTRA_SURAT_ID, -1);
        suratType = intent.getIntExtra(EXTRA_SURAT_TYPE, -1);
        isKomentar = intent.getBooleanExtra(EXTRA_IS_KOMENTAR, false);
        initData();
    }

    private void performGetDetailSuratMasuk() {
        WebServiceHelper.getInstance().getServices().getDetailSuratMasuk(suratId, new Callback<Surat>() {
            @Override
            public void success(Surat surat) {
                DatabaseHelper.getInstance().getRealm().beginTransaction();
                Surat oldSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", suratId).findFirst();
                if (oldSurat != null) {
                    oldSurat.setIsRead(true);
                } else {
                    if (surat.getIsKegiatan() == 0) {
                        surat.setWaktuMulaiKegiatan(null);
                        surat.setWaktuSelesaiKegiatan(null);
                    }
                    surat.setIsRead(true);
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(surat);
                }
                DatabaseHelper.getInstance().getRealm().commitTransaction();

                ((DetailSuratFragment) pagerAdapter.getItem(0)).setSurat(surat, isKomentar);
                ((DetailSuratFragment) pagerAdapter.getItem(0)).setOnDetailChangeListener(DetailSuratActivity.this);

                if (arsip == false){
                    ((DetailSuratDisposisiFragment) pagerAdapter.getItem(1)).setOnDisposisiChangeListener(DetailSuratActivity.this);
                    ((DetailSuratDisposisiFragment) pagerAdapter.getItem(1)).setSurat(surat);
                    tabStrip.setVisibility(View.VISIBLE);
                } else {
                    tabStrip.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                viewPager.setVisibility(View.GONE);
                buttonRetry.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(DetailSuratActivity.this, "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                viewPager.setVisibility(View.GONE);
                buttonRetry.setVisibility(View.VISIBLE);
            }
        });
    }

    private void performGetDetailSuratKeluar() {
        WebServiceHelper.getInstance().getServices().getDetailSuratKeluar(suratId, new Callback<Surat>() {
            @Override
            public void success(Surat surat) {
                DatabaseHelper.getInstance().getRealm().beginTransaction();
                Surat oldSurat = DatabaseHelper.getInstance().getRealm().where(Surat.class).equalTo("id", suratId).findFirst();
                if (oldSurat != null) {
                    oldSurat.setIsRead(true);
                } else {
                    if (surat.getIsKegiatan() == 0) {
                        surat.setWaktuMulaiKegiatan(null);
                        surat.setWaktuSelesaiKegiatan(null);
                    }

                    surat.setIsRead(true);
                    DatabaseHelper.getInstance().getRealm().copyToRealmOrUpdate(surat);
                }
                DatabaseHelper.getInstance().getRealm().commitTransaction();

                ((DetailSuratFragment) pagerAdapter.getItem(0)).setSurat(surat, isKomentar);
                ((DetailSuratFragment) pagerAdapter.getItem(0)).setOnDetailChangeListener(DetailSuratActivity.this);

                if (arsip == false){
                    ((DetailSuratDisposisiFragment) pagerAdapter.getItem(1)).setOnDisposisiChangeListener(DetailSuratActivity.this);
                    ((DetailSuratDisposisiFragment) pagerAdapter.getItem(1)).setSurat(surat);
                    tabStrip.setVisibility(View.VISIBLE);
                } else {
                    tabStrip.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                viewPager.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                Toast.makeText(DetailSuratActivity.this, "Gagal menghubungi server. Silakan coba kembali sesaat lagi.", Toast.LENGTH_SHORT).show();

                viewPager.setVisibility(View.GONE);
                buttonRetry.setVisibility(View.VISIBLE);
            }
        });
    }

    public void refreshDataSurat(){
        buttonRetry.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);

        arsip = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("arsip", false);
        if (suratId != -1 && suratType != -1) performGetDetailSurat();
    }
}
