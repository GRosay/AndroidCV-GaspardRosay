package ch.gaspard_rosay.rosaygaspard;

import android.provider.BaseColumns;

/**
 * Created by Rosay on 09/05/16.
 */
public final class MainDatabase {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MainDatabase() {}

    /* Inner class that defines the table contents */
    public static abstract class ExperienceEntry implements BaseColumns {
        public static final String TABLE_NAME = "experiences";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATES = "dates";
        public static final String COLUMN_NAME_SOCIETY = "society";
        public static final String COLUMN_NAME_DESCR = "description";
    }
    /* Inner class that defines the table contents */
    public static abstract class StudiesEntry implements BaseColumns {
        public static final String TABLE_NAME = "studies";
        public static final String COLUMN_NAME_DIPLOMA = "diploma";
        public static final String COLUMN_NAME_DATES = "dates";
        public static final String COLUMN_NAME_SCHOOL = "school";
        public static final String COLUMN_NAME_DESCR = "description";
    }

}