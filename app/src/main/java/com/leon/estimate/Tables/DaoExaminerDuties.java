package com.leon.estimate.Tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoExaminerDuties {

    @Query("SELECT * FROM ExaminerDuties")
    List<ExaminerDuties> getExaminerDuties();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<ExaminerDuties> values);
}
