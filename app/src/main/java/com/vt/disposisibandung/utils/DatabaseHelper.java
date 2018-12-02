package com.vt.disposisibandung.utils;

import io.realm.Realm;

/**
 * Created by irvan on 6/20/15.
 */
public class DatabaseHelper {
    private static DatabaseHelper instance;
    private Realm realm;

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    public Realm getRealm() {
        realm = Realm.getDefaultInstance();
        return realm;
    }
}
