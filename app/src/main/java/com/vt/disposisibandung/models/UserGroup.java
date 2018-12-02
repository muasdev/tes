package com.vt.disposisibandung.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by irvan on 12/3/15.
 */
public class UserGroup extends RealmObject {

    @PrimaryKey
    @SerializedName("group_name")
    private String groupName;

    @SerializedName("target")
    private RealmList<User> users;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public RealmList<User> getUsers() {
        return users;
    }

    public void setUsers(RealmList<User> users) {
        this.users = users;
    }
}
