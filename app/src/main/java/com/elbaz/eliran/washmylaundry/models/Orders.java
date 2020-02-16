package com.elbaz.eliran.washmylaundry.models;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class Orders {
    private String uid, pid, uniqueOrderId;

    public Orders() {}

    public Orders(String uid) {
        this.uid = uid;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }
}
