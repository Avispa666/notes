package com.developers.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by avispa on 16.10.2016.
 */

public class DBHelper extends SQLiteOpenHelper implements BaseColumns{

    public static final String TABLE_NAME = "NOTES";
    public static final String DB_NAME = "NOTES";
    public static final String NOTE_NAME_COLUMN = "NOTE_NAME";
    public static final String FILE_NAME_COLUMN = "FILE_NAME";
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу с полями
        db.execSQL("create table " + TABLE_NAME + " ("
                + "_id integer primary key autoincrement,"
                + FILE_NAME_COLUMN + " text,"
                + NOTE_NAME_COLUMN + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO make table upgrade method
    }
}
