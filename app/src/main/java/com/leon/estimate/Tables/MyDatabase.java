package com.leon.estimate.Tables;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CalculationInformation.class, Calculation.class}, version = 10, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE backup_table (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "address TEXT," +
                    "billId TEXT, " +
                    "examinationDay TEXT, " +
                    "examinationId TEXT UNIQUE, " +
                    "isPeymayesh INTEGER, " +
                    "moshtarakMobile TEXT, " +
                    "nameAndFamily TEXT, " +
                    "neighbourBillId TEXT, " +
                    "notificationMobile TEXT, " +
                    "radif TEXT, " +
                    "serviceGroup TEXT, " +
                    "trackNumber TEXT" +
                    ")");
        }
    };

    public abstract DaoCalculation daoCalculateCalculation();

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE Calculation");
        }
    };
    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Calculation ADD COLUMN read INTEGER ");
        }
    };
    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Calculation RENAME TO CalculationInformation");
        }
    };

    public abstract DaoCalculateInfo daoCalculateInfo();
}
