package com.leon.estimate.Tables;

public class GISInfo {
    String api;
    String token;
    String billId;
    double lat;
    double lng;

    public GISInfo(String api, String token, String billId, double lat, double lng) {
        this.api = api;
        this.token = token;
        this.billId = billId;
        this.lat = lat;
        this.lng = lng;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
