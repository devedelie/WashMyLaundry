package com.elbaz.eliran.washmylaundry.models;

import com.google.type.Date;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class Orders {
    private String uid, pid, uniqueOrderId;
    private boolean isProviderConfirmedOrder;
    private double taxAdded, deliveryPrice, ironingPrice, finalPrice;
    private Date reservationDate, confirmationDate;

    public Orders() {}

    public Orders(String uid) {
        this.uid = uid;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }
}
