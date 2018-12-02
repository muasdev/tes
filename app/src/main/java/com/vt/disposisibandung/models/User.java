package com.vt.disposisibandung.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Irvan on 6/29/2015.
 */
public class User extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("jabatan")
    private String jabatan;

    @SerializedName("profile_picture")
    private String profilePictureUrl;

    @SerializedName("role_id")
    public String role_id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
