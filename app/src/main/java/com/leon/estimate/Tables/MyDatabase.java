package com.leon.estimate.Tables;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CalculationInformation.class, Calculation.class, CalculationUserInput.class,
        Images.class, MapScreen.class}, version = 14, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE CalculationUserInput ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "trackingId  TEXT, " +
                    "trackNumber  INTEGER,  " +
                    "requestType  INTEGER,  " +
                    "parNumber  TEXT,  " +
                    "billId  TEXT,  " +
                    "radif  INTEGER,  " +
                    "neighbourBillId  TEXT,  " +
                    "zoneId  INTEGER,  " +
                    "notificationMobile  TEXT,  " +
                    "karbariId  INTEGER,  " +
                    "qotrEnsheabId  INTEGER,  " +
                    "noeVagozariId  INTEGER,  " +
                    "taxfifId  INTEGER,  " +
                    "selectedServices  TEXT,  " +
                    "phoneNumber  TEXT,  " +
                    "mobile  TEXT,  " +
                    "firstName  INTEGER,  " +
                    "sureName  TEXT,  " +
                    "arse  INTEGER,  " +
                    "aianKol  INTEGER,  " +
                    "aianMaskooni  INTEGER,  " +
                    "aianTejari  INTEGER,  " +
                    "sifoon100  INTEGER,  " +
                    "sifoon125  INTEGER,  " +
                    "sifoon150  INTEGER,  " +
                    "  sifoon200  INTEGER,  " +
                    "  zarfiatQarardadi  INTEGER,  " +
                    "  arzeshMelk  INTEGER,  " +
                    "  tedadMaskooni  INTEGER,  " +
                    "  tedadTejari  INTEGER,  " +
                    "  tedadSaier  INTEGER,  " +
                    "  tedadTaxfif  INTEGER,  " +
                    "  nationalId  TEXT,  " +
                    "  identityCode  TEXT,  " +
                    "  fatherName  TEXT,  " +
                    "  postalCode  TEXT,  " +
                    "  ensheabQeireDaem  INTEGER,  " +
                    "  adamTaxfifAb  INTEGER,  " +
                    "  adamTaxfifFazelab  INTEGER,  " +
                    "  address  INTEGER,  " +
                    "  description  INTEGER,  " +
                    "  sent  INTEGER  " +
                    ");");
        }
    };

    public abstract DaoCalculation daoCalculateCalculation();

    public abstract DaoCalculateInfo daoCalculateInfo();

    public abstract DaoCalculationUserInput daoCalculationUserInput();

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE Calculation");
        }
    };
    public static final Migration MIGRATION_13_14 = new Migration(13, 14) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Images ADD COLUMN peygiri TEXT ");
        }
    };
    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Calculation RENAME TO CalculationInformation");
        }
    };
    public static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE MapScreen (" +
                    "id INTEGER," +
                    "bilId TEXT, " +
                    "bitmap BLOB, " +
                    "PRIMARY KEY(id)" +
                    ")");

        }
    };

    public abstract DaoImages daoImages();

    public abstract DaoMapScreen daoMapScreen();

}
