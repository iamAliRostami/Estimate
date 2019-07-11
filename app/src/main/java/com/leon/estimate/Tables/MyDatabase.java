package com.leon.estimate.Tables;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CalculationInfo.class, Calculation.class}, version = 2, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract DaoCalculateInfo daoCalcuateInfo();

    public abstract DaoCalculation daoCalculateCalculation();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            alter table `users` modify column id INT NOT NULL AUTO_INCREMENT;
//            database.execSQL("ALTER TABLE Calculation "
//                    + "  Add COLUMN id AUTO_INCREMENT ");
            database.execSQL("CREATE TABLE backup_table (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "address TEXT," +
                    "billId TEXT, " +
                    "examinationDay TEXT, " +
                    "examinationId TEXT, " +
                    "isPeymayesh INTEGER, " +
                    "moshtarakMobile TEXT, " +
                    "nameAndFamily TEXT, " +
                    "neighbourBillId TEXT, " +
                    "notificationMobile TEXT, " +
                    "radif TEXT, " +
                    "serviceGroup TEXT, " +
                    "trackNumber TEXT" +
                    ")");
            database.execSQL("DROP TABLE Calculation");
            database.execSQL("ALTER TABLE backup_table RENAME TO Calculation");
        }
    };
}
