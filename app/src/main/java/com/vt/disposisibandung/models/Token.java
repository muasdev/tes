package com.vt.disposisibandung.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by irvan on 6/25/15.
 */
public class Token extends RealmObject {

    @PrimaryKey
    @SerializedName("api_key")
    private String accessToken;

    @SerializedName("device_id")
    private String deviceId;

    private boolean remember;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }
}
