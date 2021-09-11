package com.leon.estimate.Tables;

import okhttp3.MultipartBody;

public class UploadImage {
    boolean success;
    String error;
//    String data;
    String billId;
    String trackingNumber;
    int docId;
    MultipartBody.Part imageFile;

    public UploadImage(String billId, String trackingNumber, int docId, MultipartBody.Part imageFile) {
        this.billId = billId;
        this.trackingNumber = trackingNumber;
        this.docId = docId;
        this.imageFile = imageFile;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public MultipartBody.Part getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartBody.Part imageFile) {
        this.imageFile = imageFile;
    }
}
