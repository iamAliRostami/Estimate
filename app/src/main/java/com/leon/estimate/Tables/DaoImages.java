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

    @Query("SELECT * FROM Images WHERE peygiri =:peygiri And imageCode=:imageCode")
    List<Images> getImagesByPeygiriAndImageCode(String peygiri, String imageCode);

    @Query("SELECT * FROM Images WHERE peygiri =:peygiri")
    List<Images> getImagesByPeygiri(String peygiri);

    @Query("SELECT * FROM Images WHERE imageCode =:imageCode")
    List<Images> getImagesByImageCode(String imageCode);

    @Query("SELECT * FROM Images WHERE billId =:billId")
    List<Images> getImagesByBillId(String billId);

    @Query("SELECT * FROM Images WHERE imageCode =:imageCode AND billId =:billId")
    List<Images> getImagesByImageCodeAndBillId(String imageCode, String billId);
}
