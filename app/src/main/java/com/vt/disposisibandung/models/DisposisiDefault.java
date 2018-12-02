package com.vt.disposisibandung.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * For VELO TEKNOLOGI
 * Created by Ridwan Ismail on 17 Juni 2016
 * You can contact me at : ismail.ridwan98@gmail.com
 * -------------------------------------------------
 * SIP MOBILE
 * com.vt.disposisibandung.models
 * or see link for more detail https://gitlab.com/velocite/sip-mobile
 */
public class DisposisiDefault extends RealmObject {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("isi_pesan")
    @Expose
    private String isiPesan;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsiPesan() {
        return isiPesan;
    }

    public void setIsiPesan(String isiPesan) {
        this.isiPesan = isiPesan;
    }
}
