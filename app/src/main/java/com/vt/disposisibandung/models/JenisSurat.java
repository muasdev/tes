package com.vt.disposisibandung.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by irvan on 7/4/15.
 */
public class JenisSurat extends RealmObject {

    @PrimaryKey
    private String id;

    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
