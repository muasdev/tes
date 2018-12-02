package com.vt.disposisibandung.services;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.vt.disposisibandung.R;
import com.vt.disposisibandung.models.WebServiceResponse;
import com.vt.disposisibandung.utils.Callback;
import com.vt.disposisibandung.utils.WebServiceHelper;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by irvan on 7/6/15.
 */
public class RegistrationIntentService extends IntentService {

    public static final String PREF_GCM_TOKEN_UPDATED = "gcm_token_updated";

    public RegistrationIntentService() {
        super(RegistrationIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String token = "";
        try {
            token = InstanceID.getInstance(this).getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("GCM token", token);
        performUpdateGcmToken(token);
    }

    private void performUpdateGcmToken(String gcmToken) {
        WebServiceHelper.getInstance().getServices().updateGcmToken(gcmToken, new Callback() {
            @Override
            public void success(Object o) {
            }

            @Override
            public void failure(WebServiceResponse error) {
                Log.e("API error", error.getStatusCode() + ": " + error.getMessage());

                PreferenceManager.getDefaultSharedPreferences(RegistrationIntentService.this).edit()
                        .putBoolean(PREF_GCM_TOKEN_UPDATED, false)
                        .apply();
            }

            @Override
            public void success(Object o, Response response) {
                PreferenceManager.getDefaultSharedPreferences(RegistrationIntentService.this).edit()
                        .putBoolean(PREF_GCM_TOKEN_UPDATED, true)
                        .apply();
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("Retrofit error", error.getMessage());

                PreferenceManager.getDefaultSharedPreferences(RegistrationIntentService.this).edit()
                        .putBoolean(PREF_GCM_TOKEN_UPDATED, false)
                        .apply();
            }
        });
    }
}
