package com.vt.disposisibandung.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by irvan on 7/23/15.
 */
public class Komentar extends RealmObject {

    @SerializedName("id")
    private long id;

    @SerializedName("time")
    private Date tanggal;

    @SerializedName("name")
    private String jabatan;

    @SerializedName("comment")
    private String komentar;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }
}
