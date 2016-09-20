package com.example.maxi.redditclient.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "redditclient.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";


    private static final String CREATE_ENTRIES = "CREATE TABLE "
            + DBEntries.TABLE_NAME + " ("
            + DBEntries.ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
            + DBEntries.COLUMN_ENTRY_TITLE + TEXT_TYPE + COMMA_SEP
            + DBEntries.COLUMN_ENTRY_AUTHOR + TEXT_TYPE + COMMA_SEP
            + DBEntries.COLUMN_ENTRY_DATE + TEXT_TYPE + COMMA_SEP
            + DBEntries.COLUMN_ENTRY_THUMBNAIL_URL + TEXT_TYPE + COMMA_SEP
            + DBEntries.COLUMN_ENTRY_IMAGE_URL + TEXT_TYPE + COMMA_SEP
            + DBEntries.COLUMN_ENTRY_COMMENTS + INT_TYPE + COMMA_SEP
            + DBEntries.COLUMN_ENTRY_URL + INT_TYPE
            +")";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public static abstract class DBEntries implements BaseColumns {
        public static final String TABLE_NAME = "entries";
        public static final String ID = "_id";
        public static final String COLUMN_ENTRY_TITLE = "title";
        public static final String COLUMN_ENTRY_AUTHOR = "author";
        public static final String COLUMN_ENTRY_DATE = "date";
        public static final String COLUMN_ENTRY_THUMBNAIL_URL = "thumbnail";
        public static final String COLUMN_ENTRY_IMAGE_URL = "image";
        public static final String COLUMN_ENTRY_COMMENTS = "comments";
        public static final String COLUMN_ENTRY_URL = "url";
    }
}
