package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoImages {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertImage(Images images);


    @Query("SELECT * FROM Images")
    List<Images> getImages();

    @Query("SELECT * FROM Images WHERE peygiri =:peygiri And docId=:imageCode")
    List<Images> getImagesByPeygiriAndImageCode(String peygiri, String imageCode);

    @Query("SELECT * FROM Images WHERE peygiri =:peygiri")
    List<Images> getImagesByPeygiri(String peygiri);

    @Query("SELECT * FROM Images WHERE docId =:imageCode")
    List<Images> getImagesByImageCode(String imageCode);

    @Query("SELECT * FROM Images WHERE billId =:billId")
    List<Images> getImagesByBillId(String billId);

    @Query("SELECT * FROM Images WHERE trackingNumber =:trackingNumber OR billId =:billId")
    List<Images> getImagesByTrackingNumberOrBillId(String trackingNumber, String billId);

    @Query("DELETE FROM Images WHERE imageId = :imageId")
    void deleteByID(int imageId);
}
