package com.example.inginerie_software.models;

public class CheckoutPayment {
    // the product name
    private long id_bike;
    private String userid;

    //  currency like usd, eur ...
    private String currency;
    // our success and cancel url stripe will redirect to this links
    private String successUrl;
    private String cancelUrl;



    // simple getters and setters
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }



    public long getId_bike() {
        return id_bike;
    }

    public void setId_bike(long id_bike) {
        this.id_bike = id_bike;
    }
}