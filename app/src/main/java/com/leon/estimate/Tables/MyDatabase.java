package com.leon.estimate.Tables;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CalculationInformation.class, Calculation.class, CalculationUserInput.class,
        TaxfifDictionary.class, ServiceDictionary.class, KarbariDictionary.class, ExaminerDuties.class,
        QotrSifoonDictionary.class, QotrEnsheabDictionary.class, NoeVagozariDictionary.class, RequestDictionary.class,
        Images.class, MapScreen.class}, version = 17, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE \"RequestDictionary\" (\n" +
                    "\t\"id\"\tINTEGER,\n" +
                    "\t\"title\"\tTEXT,\n" +
                    "\t\"isSelected\"\tINTEGER,\n" +
                    "\t\"isDisabled\"\tINTEGER,\n" +
                    "\t\"hasSms\"\tINTEGER,\n" +
                    "\tPRIMARY KEY(\"id\")\n" +
                    ");");
//            database.execSQL("CREATE TABLE CalculationUserInput ( " +
//                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    "trackingId  TEXT, " +
//                    "trackNumber  INTEGER,  " +
//                    "requestType  INTEGER,  " +
//                    "parNumber  TEXT,  " +
//                    "billId  TEXT,  " +
//                    "radif  INTEGER,  " +
//                    "neighbourBillId  TEXT,  " +
//                    "zoneId  INTEGER,  " +
//                    "notificationMobile  TEXT,  " +
//                    "karbariId  INTEGER,  " +
//                    "qotrEnsheabId  INTEGER,  " +
//                    "noeVagozariId  INTEGER,  " +
//                    "taxfifId  INTEGER,  " +
//                    "selectedServices  TEXT,  " +
//                    "phoneNumber  TEXT,  " +
//                    "mobile  TEXT,  " +
//                    "firstName  INTEGER,  " +
//                    "sureName  TEXT,  " +
//                    "arse  INTEGER,  " +
//                    "aianKol  INTEGER,  " +
//                    "aianMaskooni  INTEGER,  " +
//                    "aianTejari  INTEGER,  " +
//                    "sifoon100  INTEGER,  " +
//                    "sifoon125  INTEGER,  " +
//                    "sifoon150  INTEGER,  " +
//                    "  sifoon200  INTEGER,  " +
//                    "  zarfiatQarardadi  INTEGER,  " +
//                    "  arzeshMelk  INTEGER,  " +
//                    "  tedadMaskooni  INTEGER,  " +
//                    "  tedadTejari  INTEGER,  " +
//                    "  tedadSaier  INTEGER,  " +
//                    "  tedadTaxfif  INTEGER,  " +
//                    "  nationalId  TEXT,  " +
//                    "  identityCode  TEXT,  " +
//                    "  fatherName  TEXT,  " +
//                    "  postalCode  TEXT,  " +
//                    "  ensheabQeireDaem  INTEGER,  " +
//                    "  adamTaxfifAb  INTEGER,  " +
//                    "  adamTaxfifFazelab  INTEGER,  " +
//                    "  address  INTEGER,  " +
//                    "  description  INTEGER,  " +
//                    "  sent  INTEGER  " +
//                    ");");
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
    public static final Migration MIGRATION_14_15 = new Migration(16, 17) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE \"ExaminerDuties\" (\n" +
                    "\t\"id\"\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t\"examinationId\"\tTEXT,\n" +
                    "\t\"karbariId\"\tTEXT,\n" +
                    "\t\"radif\"\tTEXT,\n" +
                    "\t\"trackNumber\"\tNUMERIC,\n" +
                    "\t\"billId\"\tTEXT,\n" +
                    "\t\"examinationDay\"\tTEXT,\n" +
                    "\t\"nameAndFamily\"\tTEXT,\n" +
                    "\t\"moshtarakMobile\"\tTEXT,\n" +
                    "\t\"notificationMobile\"\tTEXT,\n" +
                    "\t\"serviceGroup\"\tTEXT,\n" +
                    "\t\"address\"\tTEXT,\n" +
                    "\t\"neighbourBillId\"\tTEXT,\n" +
                    "\t\"isPeymayesh\"\tINTEGER,\n" +
                    "\t\"trackingId\"\tTEXT,\n" +
                    "\t\"requestType\"\tTEXT,\n" +
                    "\t\"parNumber\"\tTEXT,\n" +
                    "\t\"zoneId\"\tTEXT,\n" +
                    "\t\"callerId\"\tTEXT,\n" +
                    "\t\"zoneTitle\"\tTEXT,\n" +
                    "\t\"isNewEnsheab\"\tINTEGER,\n" +
                    "\t\"phoneNumber\"\tTEXT,\n" +
                    "\t\"mobile\"\tTEXT,\n" +
                    "\t\"firstName\"\tTEXT,\n" +
                    "\t\"sureName\"\tTEXT,\n" +
                    "\t\"hasFazelab\"\tINTEGER,\n" +
                    "\t\"fazelabInstallDate\"\tTEXT,\n" +
                    "\t\"isFinished\"\tINTEGER,\n" +
                    "\t\"eshterak\"\tTEXT,\n" +
                    "\t\"arse\"\tINTEGER,\n" +
                    "\t\"aianKol\"\tINTEGER,\n" +
                    "\t\"aianMaskooni\"\tINTEGER,\n" +
                    "\t\"aianNonMaskooni\"\tINTEGER,\n" +
                    "\t\"qotrEnsheabId\"\tINTEGER,\n" +
                    "\t\"sifoon100\"\tINTEGER,\n" +
                    "\t\"sifoon125\"\tINTEGER,\n" +
                    "\t\"sifoon150\"\tINTEGER,\n" +
                    "\t\"sifoon200\"\tINTEGER,\n" +
                    "\t\"zarfiatQarardadi\"\tINTEGER,\n" +
                    "\t\"arzeshMelk\"\tINTEGER,\n" +
                    "\t\"tedadMaskooni\"\tINTEGER,\n" +
                    "\t\"tedadTejari\"\tINTEGER,\n" +
                    "\t\"tedadSaier\"\tINTEGER,\n" +
                    "\t\"taxfifId\"\tINTEGER,\n" +
                    "\t\"tedadTaxfif\"\tINTEGER,\n" +
                    "\t\"nationalId\"\tTEXT,\n" +
                    "\t\"identityCode\"\tTEXT,\n" +
                    "\t\"fatherName\"\tTEXT,\n" +
                    "\t\"postalCode\"\tTEXT,\n" +
                    "\t\"description\"\tTEXT,\n" +
                    "\t\"adamTaxfifAb\"\tINTEGER,\n" +
                    "\t\"adamTaxfifFazelab\"\tINTEGER,\n" +
                    "\t\"isEnsheabQeirDaem\"\tINTEGER,\n" +
                    "\t\"hasRadif\"\tINTEGER,\n" +
                    "\t\"requestDictionaryString\"\tINTEGER\n" +
                    ");");


        }
    };

    public abstract DaoImages daoImages();

    public abstract DaoMapScreen daoMapScreen();

    public abstract DaoExaminerDuties daoExaminerDuties();

    public abstract DaoKarbariDictionary daoKarbariDictionary();

    public abstract DaoRequestDictionary daoRequestDictionary();

    public abstract DaoNoeVagozariDictionary daoNoeVagozariDictionary();

    public abstract DaoQotrEnsheabDictionary daoQotrEnsheabDictionary();

    public abstract DaoQotrSifoonDictionary daoQotrSifoonDictionary();

    public abstract DaoTaxfifDictionary daoTaxfifDictionary();

    public abstract DaoServiceDictionary daoServiceDictionary();

}
