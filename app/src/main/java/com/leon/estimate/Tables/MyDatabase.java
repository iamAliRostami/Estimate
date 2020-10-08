package com.leon.estimate.Tables;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CalculationInformation.class, Calculation.class, CalculationUserInput.class,
        TaxfifDictionary.class, ServiceDictionary.class, KarbariDictionary.class, ExaminerDuties.class,
        QotrSifoonDictionary.class, QotrEnsheabDictionary.class, NoeVagozariDictionary.class,
        RequestDictionary.class, Images.class, MapScreen.class, ResultDictionary.class,
        Tejariha.class, Zarib.class, Formula.class, Block.class},
        version = 39, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public static final Migration MIGRATION_38_39 = new Migration(38, 39) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("Alter TABLE \"CalculationUserInput\" Add column  x1 Real;");
            database.execSQL("Alter TABLE \"CalculationUserInput\" Add column  x2 Real;");
            database.execSQL("Alter TABLE \"CalculationUserInput\" Add column  y1 Real;");
            database.execSQL("Alter TABLE \"CalculationUserInput\" Add column  y2 Real;");
//            database.execSQL("CREATE TABLE \"FormulaTemp\" (\n" +
//                    "\t\"id\"\tINTEGER,\n" +
//                    "\t\"zoneId\"\tINTEGER,\n" +
//                    "\t\"gozarFrom\"\tREAL,\n" +
//                    "\t\"gozarTo\"\tREAL,\n" +
//                    "\t\"gozarTitle\"\tTEXT,\n" +
//                    "\t\"maskooniZ\"\tREAL,\n" +
//                    "\t\"TejraiZ\"\tREAL,\n" +
//                    "\t\"edariDolatiZ\"\tREAL,\n" +
//                    "\t\"khadamatiZ\"\tREAL,\n" +
//                    "\t\"sanatiZ\"\tREAL,\n" +
//                    "\t\"sayerZ\"\tREAL,\n" +
//                    "\tPRIMARY KEY(\"id\")\n" +
//                    ");");
//            database.execSQL("DROP TABLE Formula;");
//            database.execSQL("ALTER TABLE FormulaTemp RENAME TO Formula;");

//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column pelak INTEGER;");
//            database.execSQL("CREATE TABLE \"ImagesTemp\" (\n" +
//                    "\t\"imageId\"\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
//                    "\t\"address\"\tTEXT,\n" +
//                    "\t\"billId\"\tTEXT,\n" +
//                    "\t\"trackingNumber\"\tTEXT,\n" +
//                    "\t\"peygiri\"\tTEXT,\n" +
//                    "\t\"docId\"\tINTEGER,\n" +
//                    "\t\"Sent\"\tINTEGER\n" +
//                    ");");
        }
    };

    public static final Migration MIGRATION_31_32 = new Migration(27, 28) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column estelamShahrdari INTEGER;");
            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column parvane INTEGER;");
            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column motaqazi INTEGER;");
//            database.execSQL("Alter TABLE \"chahDescription\" Add column parvane TEXT;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Rename column olqFazelab to omqFazelab ;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column shenasname TEXT;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column faseleKhakiA INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column faseleKhakiF INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column faseleAsphaultA INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column faseleAsphaultF INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column faseleSangA INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column faseleSangF INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column faseleOtherA INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column faseleOtherF INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column ezhaNazarA INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column ezhaNazarF INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column  qotrLooleI INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column jensLooleI INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column qotrLooleS TEXT;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column  jensLooleS TEXT;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column looleA INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column  looleF INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column noeMasrafI INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column noeMasrafS TEXT;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column  vaziatNasbPompI INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column omqeZirzamin INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column  etesalZirzamin INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column olqFazelab INTEGER;");
//            database.execSQL("Alter TABLE \"ExaminerDuties\" Add column chahAbBaran INTEGER;");
        }
    };

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
                    "\t\"trackNumber\"\tNUMERIC PRIMARY KEY ,\n" +
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


    public abstract DaoCalculation daoCalculateCalculation();

    public abstract DaoCalculateInfo daoCalculateInfo();

    public abstract DaoCalculationUserInput daoCalculationUserInput();

    public abstract DaoImages daoImages();

    public abstract DaoTejariha daoTejariha();

    public abstract DaoMapScreen daoMapScreen();

    public abstract DaoExaminerDuties daoExaminerDuties();

    public abstract DaoKarbariDictionary daoKarbariDictionary();

    public abstract DaoRequestDictionary daoRequestDictionary();

    public abstract DaoNoeVagozariDictionary daoNoeVagozariDictionary();

    public abstract DaoQotrEnsheabDictionary daoQotrEnsheabDictionary();

    public abstract DaoQotrSifoonDictionary daoQotrSifoonDictionary();

    public abstract DaoTaxfifDictionary daoTaxfifDictionary();

    public abstract DaoServiceDictionary daoServiceDictionary();

    public abstract DaoResultDictionary daoResultDictionary();

    public abstract DaoZarib daoZarib();

    public abstract DaoBlock daoBlock();

    public abstract DaoFormula daoFormula();

}
