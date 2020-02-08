package com.elbaz.eliran.washmylaundry.models;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class Orders {
    private String uid;

    public Orders() {
    }

    public Orders(String uid) { // For creating an empty collection for provider's personal-orders
        this.uid = uid;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }
}
