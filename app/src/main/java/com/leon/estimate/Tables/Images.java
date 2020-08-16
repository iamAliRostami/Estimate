package com.leon.estimate.Tables;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Images", indices = @Index(value = {"imageId"}, unique = true))
public class Images {
    @PrimaryKey(autoGenerate = true)
    int imageId;
    String address;
    String billId;
    String trackingNumber;
    String docId;
    String peygiri;
    @Ignore
    String docTitle;
    @Ignore
    Bitmap bitmap;
    @Ignore
    String uri;
    @Ignore
    boolean needSave;

    public Images(String address, String billId, String trackingNumber, String docId, String peygiri) {
        this.address = address;
        this.billId = billId;
        this.trackingNumber = trackingNumber;
        this.docId = docId;
        this.peygiri = peygiri;
    }

    public Images(String address, String billId, String trackingNumber, String docId,
                  String docTitle, Bitmap bitmap, Boolean needSave) {
        this.address = address;
        this.billId = billId;
        this.trackingNumber = trackingNumber;
        this.docId = docId;
        this.docTitle = docTitle;
        this.bitmap = bitmap;
        this.needSave = needSave;
    }

    public Images(String billId, String trackingNumber, String docTitle, String uri,
                  Bitmap bitmap, Boolean needSave) {
        this.billId = billId;
        this.trackingNumber = trackingNumber;
        this.docTitle = docTitle;
        this.uri = uri;
        this.bitmap = bitmap;
        this.needSave = needSave;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getPeygiri() {
        return peygiri;
    }

    public void setPeygiri(String peygiri) {
        this.peygiri = peygiri;
    }
}
