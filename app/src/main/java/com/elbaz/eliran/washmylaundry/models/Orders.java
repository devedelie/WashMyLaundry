package com.elbaz.eliran.washmylaundry.models;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class Orders {
    private String uid, pid, uniqueOrderId, providerName, providerImageUrl, clientName, clientImageUrl;
    private int providerPhone, userPhone, orderStatus;
//    private boolean isProviderConfirmedOrder, isOrderReadyForDelivery, isOrderDelivered;
    private double taxAdded, deliveryPrice, ironingPrice, finalPrice;
    private String reservationDate, reservationDateFormatted, confirmationDate;
//    private Timestamp reservationTimestamp, confirmationTimestamp;

    public Orders() {}

    public Orders(String uid, String pid, String uniqueOrderId, String providerName, String providerImageUrl, String clientName, String clientImageUrl, int providerPhone, int userPhone, int orderStatus, double taxAdded, double deliveryPrice, double ironingPrice, double finalPrice, String reservationDate, String reservationDateFormatted) {
        this.uid = uid;
        this.pid = pid;
        this.uniqueOrderId = uniqueOrderId;
        this.providerName = providerName;
        this.providerImageUrl = providerImageUrl;
        this.clientName = clientName;
        this.clientImageUrl = clientImageUrl;
        this.providerPhone = providerPhone;
        this.userPhone = userPhone;
        this.orderStatus = orderStatus;
        this.taxAdded = taxAdded;
        this.deliveryPrice = deliveryPrice;
        this.ironingPrice = ironingPrice;
        this.finalPrice = finalPrice;
        this.reservationDate = reservationDate;
        this.reservationDateFormatted = reservationDateFormatted;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getPid() { return pid; }

    public void setPid(String pid) { this.pid = pid; }

    public String getUniqueOrderId() { return uniqueOrderId; }

    public void setUniqueOrderId(String uniqueOrderId) { this.uniqueOrderId = uniqueOrderId; }

//    public boolean isProviderConfirmedOrder() { return isProviderConfirmedOrder; }
//
//    public void setProviderConfirmedOrder(boolean providerConfirmedOrder) { isProviderConfirmedOrder = providerConfirmedOrder; }
//
//    public boolean isOrderReadyForDelivery() { return isOrderReadyForDelivery; }
//
//    public void setOrderReadyForDelivery(boolean orderReadyForDelivery) { isOrderReadyForDelivery = orderReadyForDelivery; }
//
//    public boolean isOrderDelivered() { return isOrderDelivered; }
//
//    public void setOrderDelivered(boolean orderDelivered) { isOrderDelivered = orderDelivered; }

    public double getTaxAdded() { return taxAdded; }

    public void setTaxAdded(double taxAdded) { this.taxAdded = taxAdded; }

    public double getDeliveryPrice() { return deliveryPrice; }

    public void setDeliveryPrice(double deliveryPrice) { this.deliveryPrice = deliveryPrice; }

    public double getIroningPrice() { return ironingPrice; }

    public void setIroningPrice(double ironingPrice) { this.ironingPrice = ironingPrice; }

    public double getFinalPrice() { return finalPrice; }

    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }

    public String getReservationDate() { return reservationDate; }

    public void setReservationDate(String reservationDate) { this.reservationDate = reservationDate; }

    public String getConfirmationDate() { return confirmationDate; }

    public void setConfirmationDate(String confirmationDate) { this.confirmationDate = confirmationDate; }

    public String getReservationDateFormatted() { return reservationDateFormatted; }

    public void setReservationDateFormatted(String reservationDateFormatted) { this.reservationDateFormatted = reservationDateFormatted; }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderImageUrl() {
        return providerImageUrl;
    }

    public void setProviderImageUrl(String providerImageUrl) {
        this.providerImageUrl = providerImageUrl;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientImageUrl() {
        return clientImageUrl;
    }

    public void setClientImageUrl(String clientImageUrl) {
        this.clientImageUrl = clientImageUrl;
    }

    public int getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(int providerPhone) {
        this.providerPhone = providerPhone;
    }

    public int getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(int userPhone) {
        this.userPhone = userPhone;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    //    public Timestamp getReservationTimestamp() { return reservationTimestamp; }
//
//    public void setReservationTimestamp(Timestamp reservationTimestamp) { this.reservationTimestamp = reservationTimestamp; }
//
//    public Timestamp getConfirmationTimestamp() { return confirmationTimestamp; }
//
//    public void setConfirmationTimestamp(Timestamp confirmationTimestamp) { this.confirmationTimestamp = confirmationTimestamp; }



}
