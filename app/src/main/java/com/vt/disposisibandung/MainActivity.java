package com.vt.disposisibandung;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;
import com.vt.disposisibandung.fragments.ArsipSuratFragment_;
import com.vt.disposisibandung.fragments.DrawerFragment;
import com.vt.disposisibandung.fragments.DrawerFragment_;
import com.vt.disposisibandung.fragments.JadwalFragment_;
import com.vt.disposisibandung.fragments.ListSuratFragment_;
import com.vt.disposisibandung.models.Surat;
import com.vt.disposisibandung.models.Token;
import com.vt.disposisibandung.models.User;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.services.RegistrationIntentService;
import com.vt.disposisibandung.services.UploadService_;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.DatabaseHelper;
import com.vt.disposisibandung.utils.WebServiceHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by irvan on 6/24/15.
 */
@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends AppCompatActivity implements DrawerFragment.OnDrawerItemClickListener, SearchView.OnQueryTextListener {

    public static final String EXTRA_INIT_SURAT_TYPE = "init_surat_type";

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;

    @ViewById(R.id.drawerLayout)
    protected DrawerLayout drawerLayout;

    @ViewById(R.id.addMenu)
    protected FloatingActionMenu floatingActionMenu;

    @OptionsMenuItem(R.id.action_search)
    protected MenuItem menuItemSearch;

    @Extra(EXTRA_INIT_SURAT_TYPE)
    protected int initSuratType = Surat.TYPE_MASUK;

    private ActionBarDrawerToggle drawerToggle;
    private SearchView searchView;
    private DrawerFragment drawerFragment;

    @AfterViews
    protected void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        TypedValue typedValueColorPrimaryDark = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValueColorPrimaryDark, true);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        floatingActionMenu.setClosedOnTouchOutside(true);

        switch (initSuratType) {
            case Surat.TYPE_MASUK: {
                drawerFragment = DrawerFragment_.builder().initItemPosition(0).build();
                break;
            }
            case Surat.TYPE_UNDANGAN: {
                drawerFragment = DrawerFragment_.builder().initItemPosition(1).build();
                break;
            }
            case Surat.TYPE_AUDIENSI: {
                drawerFragment = DrawerFragment_.builder().initItemPosition(2).build();
                break;
            }
            case Surat.TYPE_UMUM: {
                drawerFragment = DrawerFragment_.builder().initItemPosition(3).build();
                break;
            }
            case Surat.TYPE_BELUM_SELESAI: {
                drawerFragment = DrawerFragment_.builder().initItemPosition(4).build();
                break;
            }
            case Surat.TYPE_SUDAH_SELESAI: {
                drawerFragment = DrawerFragment_.builder().initItemPosition(5).build();
                break;
            }
            case Surat.TYPE_KELUAR: {
                drawerFragment = DrawerFragment_.builder().initItemPosition(6).build();
                break;
            }
            case Surat.TYPE_DISPOSISI_MASUK: {
                drawerFragment = DrawerFragment_.builder().initItemPosition(7).build();
                break;
            }
        }

        drawerFragment.setOnDrawerItemClickListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.drawerFrame, drawerFragment)
                .commit();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.setDrawerListener(drawerToggle);

        drawerToggle.syncState();

        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(RegistrationIntentService.PREF_GCM_TOKEN_UPDATED, false)) {
            startService(new Intent(this, RegistrationIntentService.class));
        }

        UploadService_.intent(this).start();

        if (PreferenceManager.getDefaultSharedPreferences(this).getString("role_id", "").equals(""))
            floatingActionMenu.setVisibility(View.GONE);
    }

    @OptionsItem(R.id.action_logout)
    protected void logout() {
        performDeauth();
    }


    @Click(R.id.buttonAddSuratMasuk)
    protected void addSuratMasuk() {
        floatingActionMenu.close(true);
        AddSuratMasukActivity_.intent(this).start();
    }

    @Click(R.id.buttonAddSuratKeluar)
    protected void addSuratKeluar() {
        floatingActionMenu.close(true);
        AddSuratKeluarActivity_.intent(this).start();
    }

    @Override
    public void onDrawerItemClick(int position) {
        Fragment fragment = new Fragment();
        String role_id = PreferenceManager.getDefaultSharedPreferences(this).getString("role_id", "");

        switch (position) {
            case 0: {
                getSupportActionBar().setTitle(R.string.label_kotak_masuk);
                if (role_id.equals("staf_entri")){
                    floatingActionMenu.setVisibility(View.VISIBLE);
                }
                fragment = ListSuratFragment_.builder().suratType(Surat.TYPE_MASUK).build();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("arsip", false).commit();
                break;
            }
            case 1: {
                getSupportActionBar().setTitle(R.string.label_surat_undangan);
                if (role_id.equals("staf_entri")){
                    floatingActionMenu.setVisibility(View.VISIBLE);
                }
                fragment = ListSuratFragment_.builder().suratType(Surat.TYPE_UNDANGAN).build();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("arsip", false).commit();
                break;
            }
            case 2: {
                getSupportActionBar().setTitle(R.string.label_surat_audiensi);
                if (role_id.equals("staf_entri")){
                    floatingActionMenu.setVisibility(View.VISIBLE);
                }
                fragment = ListSuratFragment_.builder().suratType(Surat.TYPE_AUDIENSI).build();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("arsip", false).commit();
                break;
            }
            case 3: {
                getSupportActionBar().setTitle(R.string.label_surat_umum);
                if (role_id.equals("staf_entri")){
                    floatingActionMenu.setVisibility(View.VISIBLE);
                }
                fragment = ListSuratFragment_.builder().suratType(Surat.TYPE_UMUM).build();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("arsip", false).commit();
                break;
            }
            case 4: {
                getSupportActionBar().setTitle(R.string.label_surat_keluar);
                if (role_id.equals("staf_entri")){
                    floatingActionMenu.setVisibility(View.VISIBLE);
                }
                fragment = ListSuratFragment_.builder().suratType(Surat.TYPE_KELUAR).build();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("arsip", false).commit();
                break;
            }
            case 5: {
                getSupportActionBar().setTitle("BELUM SELESAI");
                floatingActionMenu.setVisibility(View.GONE);
                fragment = ListSuratFragment_.builder().suratType(Surat.TYPE_BELUM_SELESAI).build();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("arsip", false).commit();
                break;
            }
            case 6: {
                getSupportActionBar().setTitle("SUDAH SELESAI");
                floatingActionMenu.setVisibility(View.GONE);
                fragment = ListSuratFragment_.builder().suratType(Surat.TYPE_SUDAH_SELESAI).build();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("arsip", false).commit();
                break;
            }
            case 7: {
                getSupportActionBar().setTitle(R.string.label_disposisi_masuk);
                floatingActionMenu.setVisibility(View.GONE);
                fragment = ListSuratFragment_.builder().suratType(Surat.TYPE_DISPOSISI_MASUK).build();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("arsip", false).commit();
                break;
            }
            case 8: {
                getSupportActionBar().setTitle(R.string.label_disposisi_keluar);
                floatingActionMenu.setVisibility(View.GONE);
                fragment = ListSuratFragment_.builder().suratType(Surat.TYPE_DISPOSISI_KELUAR).build();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("arsip", false).commit();
                break;
            }
            case 9: {
                getSupportActionBar().setTitle(R.string.label_arsip);
                floatingActionMenu.setVisibility(View.GONE);
                fragment = ArsipSuratFragment_.builder().build();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("arsip", true).commit();
                break;
            }
            case 10: {
                getSupportActionBar().setTitle(R.string.label_jadwal);
                floatingActionMenu.setVisibility(View.GONE);
                fragment = JadwalFragment_.builder().build();
                break;
            }
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrame, fragment)
                .commit();

        drawerLayout.closeDrawers();
        floatingActionMenu.close(false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        MenuItemCompat.collapseActionView(menuItemSearch);
        SearchResultActivity_.intent(this).query(query).start();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initSuratType = intent.getIntExtra(EXTRA_INIT_SURAT_TYPE, Surat.TYPE_MASUK);
        initViews();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchView = (SearchView) MenuItemCompat.getActionView(menuItemSearch);
        searchView.setQueryHint(getResources().getString(R.string.action_search));
        searchView.setOnQueryTextListener(this);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (floatingActionMenu.isOpened()) {
            floatingActionMenu.close(true);
        } else {
            super.onBackPressed();
        }
    }

    private void performDeauth() {
        WebServiceHelper.getInstance().getServices().deauthenticate(new Callback() {
            @Override
            public void success(Object o) {
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());
            }

            @Override
            public void success(Object o, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());
            }
        });

        WebServiceHelper.getInstance().setAccessToken(null);

        DatabaseHelper.getInstance().getRealm().beginTransaction();
        DatabaseHelper.getInstance().getRealm().delete(Token.class);
        DatabaseHelper.getInstance().getRealm().delete(User.class);
        DatabaseHelper.getInstance().getRealm().delete(Surat.class);
        DatabaseHelper.getInstance().getRealm().commitTransaction();

        finish();
        LoginActivity_.intent(this).start();
    }
}
