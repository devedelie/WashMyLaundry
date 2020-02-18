package com.elbaz.eliran.washmylaundry.models;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class Orders {
    private String uid, pid, uniqueOrderId;
    private boolean isProviderConfirmedOrder, isOrderReadyForDelivery, isOrderDelivered;
    private double taxAdded, deliveryPrice, ironingPrice, finalPrice;
    private String reservationDate, reservationDateFormatted, confirmationDate;
//    private Timestamp reservationTimestamp, confirmationTimestamp;

    public Orders() {}

    public Orders(String uid, String pid, String uniqueOrderId, double taxAdded, double deliveryPrice, double ironingPrice, double finalPrice, String reservationDate, String reservationDateFormatted) {
        this.uid = uid;
        this.pid = pid;
        this.uniqueOrderId = uniqueOrderId;
        this.taxAdded = taxAdded;
        this.deliveryPrice = deliveryPrice;
        this.ironingPrice = ironingPrice;
        this.finalPrice = finalPrice;
        this.reservationDate = reservationDate;
        this.reservationDateFormatted = reservationDateFormatted;
//        this.reservationTimestamp = reservationTimestamp;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getPid() { return pid; }

    public void setPid(String pid) { this.pid = pid; }

    public String getUniqueOrderId() { return uniqueOrderId; }

    public void setUniqueOrderId(String uniqueOrderId) { this.uniqueOrderId = uniqueOrderId; }

    public boolean isProviderConfirmedOrder() { return isProviderConfirmedOrder; }

    public void setProviderConfirmedOrder(boolean providerConfirmedOrder) { isProviderConfirmedOrder = providerConfirmedOrder; }

    public boolean isOrderReadyForDelivery() { return isOrderReadyForDelivery; }

    public void setOrderReadyForDelivery(boolean orderReadyForDelivery) { isOrderReadyForDelivery = orderReadyForDelivery; }

    public boolean isOrderDelivered() { return isOrderDelivered; }

    public void setOrderDelivered(boolean orderDelivered) { isOrderDelivered = orderDelivered; }

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

    //    public Timestamp getReservationTimestamp() { return reservationTimestamp; }
//
//    public void setReservationTimestamp(Timestamp reservationTimestamp) { this.reservationTimestamp = reservationTimestamp; }
//
//    public Timestamp getConfirmationTimestamp() { return confirmationTimestamp; }
//
//    public void setConfirmationTimestamp(Timestamp confirmationTimestamp) { this.confirmationTimestamp = confirmationTimestamp; }
}
