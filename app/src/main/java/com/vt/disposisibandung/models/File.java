package com.vt.disposisibandung.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Irvan on 6/28/2015.
 */
public class File extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("filename")
    private String filename;

    @SerializedName("description")
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
