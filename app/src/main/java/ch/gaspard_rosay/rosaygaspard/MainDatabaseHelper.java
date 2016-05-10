package ch.gaspard_rosay.rosaygaspard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rosay on 09/05/16.
 */

public class MainDatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "RosayGaspard.db";

    public MainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EXPERIENCE);
        db.execSQL(SQL_CREATE_STUDIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Puisque les données sont reprises d'Internet en cas de connexion disponible,
        // On remet à 0 à chaque connexion.
        db.execSQL(SQL_DELETE_EXPERIENCE);
        db.execSQL(SQL_DELETE_STUDIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_EXPERIENCE =
            "CREATE TABLE " + MainDatabase.ExperienceEntry.TABLE_NAME + " (" +
                    MainDatabase.ExperienceEntry._ID + " " + INT_TYPE + "PRIMARY KEY," +
                    MainDatabase.ExperienceEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    MainDatabase.ExperienceEntry.COLUMN_NAME_DATES + TEXT_TYPE + COMMA_SEP +
                    MainDatabase.ExperienceEntry.COLUMN_NAME_SOCIETY + TEXT_TYPE + COMMA_SEP +
                    MainDatabase.ExperienceEntry.COLUMN_NAME_DESCR + TEXT_TYPE  +
                    " )";

    private static final String SQL_DELETE_EXPERIENCE =
            "DROP TABLE IF EXISTS " + MainDatabase.ExperienceEntry.TABLE_NAME;


    private static final String SQL_CREATE_STUDIES =
            "CREATE TABLE " + MainDatabase.StudiesEntry.TABLE_NAME + " (" +
                    MainDatabase.StudiesEntry._ID + " " + INT_TYPE + "PRIMARY KEY," +
                    MainDatabase.StudiesEntry.COLUMN_NAME_DIPLOMA + TEXT_TYPE + COMMA_SEP +
                    MainDatabase.StudiesEntry.COLUMN_NAME_DATES + TEXT_TYPE + COMMA_SEP +
                    MainDatabase.StudiesEntry.COLUMN_NAME_SCHOOL + TEXT_TYPE + COMMA_SEP +
                    MainDatabase.StudiesEntry.COLUMN_NAME_DESCR + TEXT_TYPE  +
                    " )";

    private static final String SQL_DELETE_STUDIES =
            "DROP TABLE IF EXISTS " + MainDatabase.StudiesEntry.TABLE_NAME;

}
