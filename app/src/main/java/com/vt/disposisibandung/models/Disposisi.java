package com.vt.disposisibandung.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Irvan on 6/28/2015.
 */
public class Disposisi extends RealmObject {

    @PrimaryKey
    @SerializedName("surat_action_id")
    private long id;

    @SerializedName("time")
    private Date tanggal;

    @SerializedName("jabatan")
    private String jabatan;

    @SerializedName("kepada")
    private String kepada;

    @SerializedName("keterangan")
    private String keterangan;

    @SerializedName("can_edit")
    private boolean canEdit;

    @SerializedName("tipe_edit")
    private int editType;

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

    public String getKepada() {
        return kepada;
    }

    public void setKepada(String kepada) {
        this.kepada = kepada;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public int getEditType() {
        return editType;
    }

    public void setEditType(int editType) {
        this.editType = editType;
    }
}
