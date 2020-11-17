package com.leon.estimate.Tables;

public class AddDocument {
    public String trackNumber;
    public String firstName;
    public String sureName;
    public String address;
    public String zoneId;
    boolean success;
    String error;
    String data;

    public AddDocument(String trackNumber, String firstName, String sureName, String address, String zoneId) {
        this.trackNumber = trackNumber;
        this.firstName = firstName;
        this.sureName = sureName;
        this.address = address;
        this.zoneId = zoneId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
