package com.vt.disposisibandung.services;

import android.content.Intent;

/**
 * Created by irvan on 7/4/15.
 */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        startService(new Intent(this, RegistrationIntentService.class));
    }


}
