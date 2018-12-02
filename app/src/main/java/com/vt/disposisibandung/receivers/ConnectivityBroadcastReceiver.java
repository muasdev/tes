package com.vt.disposisibandung.receivers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.net.ConnectivityManagerCompat;

import com.vt.disposisibandung.services.UploadService_;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.ReceiverAction;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.api.support.content.AbstractBroadcastReceiver;

/**
 * Created by irvan on 7/7/15.
 */
@EReceiver
public class ConnectivityBroadcastReceiver extends AbstractBroadcastReceiver {

    @SystemService
    protected ConnectivityManager connectivityManager;

    @ReceiverAction(ConnectivityManager.CONNECTIVITY_ACTION)
    protected void startUploadService(Context context, Intent intent) {
        NetworkInfo networkInfo = ConnectivityManagerCompat.getNetworkInfoFromBroadcast(connectivityManager, intent);
        if (networkInfo != null & networkInfo.isConnected()) {
            UploadService_.intent(context).start();
        }
    }

}
