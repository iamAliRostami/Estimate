package com.leon.estimate.Tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Images", indices = @Index(value = {"imageId"}, unique = true))
public class Images {
    @PrimaryKey(autoGenerate = true)
    int imageId;
    String address;
    String billId;
    String eshterak;
    String imageCode;

    public Images(String address, String billId, String eshterak, String imageCode) {
        this.address = address;
        this.billId = billId;
        this.eshterak = eshterak;
        this.imageCode = imageCode;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
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

    public String getEshterak() {
        return eshterak;
    }

    public void setEshterak(String eshterak) {
        this.eshterak = eshterak;
    }
}
