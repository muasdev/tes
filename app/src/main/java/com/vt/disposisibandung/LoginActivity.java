package com.vt.disposisibandung;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.vt.disposisibandung.fragments.adapters.LoginPagerAdapter;
import com.vt.disposisibandung.models.Token;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.DatabaseHelper;
import com.vt.disposisibandung.utils.WebServiceHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by irvan on 6/24/15.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000;
    private static final int REQUEST_CODE_PLAY_SERVICES = 102;

    @ViewById(R.id.viewPager)
    protected ViewPager viewPager;

    @AfterViews
    protected void initViews() {
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        viewPager.setAdapter(new LoginPagerAdapter(getSupportFragmentManager(), this));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Token token = DatabaseHelper.getInstance().getRealm().where(Token.class).findFirst();
                if (token != null) {
                    if (token.isRemember()) {
                        WebServiceHelper.getInstance().setAccessToken(token);

                        MainActivity_.intent(LoginActivity.this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                        finish();
                    } else {
                        performDeauth();
                        viewPager.setCurrentItem(1);
                    }
                } else {
                    viewPager.setCurrentItem(1);
                }
            }
        }, SPLASH_DELAY);

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, REQUEST_CODE_PLAY_SERVICES, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(LoginActivity.this, "Google Play Services tidak tersedia.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void performDeauth() {
        WebServiceHelper.getInstance().getServices().deauthenticate(new Callback() {
            @Override
            public void success(Object o) {
            }

            @Override
            public void failure(WebServiceResponse error) {
            }

            @Override
            public void success(Object o, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

        WebServiceHelper.getInstance().setAccessToken(null);

        DatabaseHelper.getInstance().getRealm().beginTransaction();
        DatabaseHelper.getInstance().getRealm().delete(Token.class);
        DatabaseHelper.getInstance().getRealm().commitTransaction();
    }
}
