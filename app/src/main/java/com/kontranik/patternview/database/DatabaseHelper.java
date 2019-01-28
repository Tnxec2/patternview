package com.kontranik.patternview.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "patternstore.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "pattern"; // название таблицы в бд

    // названия столбцов
    static final String COLUMN_ID = "_id";
    static final String COLUMN_URI = "uri";
    static final String COLUMN_ROWHEIGHT = "rowheight";
    static final String COLUMN_SCROLLX = "scrollx";
    static final String COLUMN_SCROLLY = "scrolly";
    static final String COLUMN_SCALE = "scale";
    static final String COLUMN_LASTOPENED = "lastopened";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_URI + " TEXT, "
                + COLUMN_ROWHEIGHT + " INTEGER, "
                + COLUMN_SCROLLX + " INTEGER, "
                + COLUMN_SCROLLY + " INTEGER, "
                + COLUMN_SCALE + " REAL, "
                + COLUMN_LASTOPENED + " INTEGER "
                + ");"
        );
        // добавление начальных данных
        // db.execSQL("INSERT INTO "+ TABLE +" (" + COLUMN_NAME
        //         + ", " + COLUMN_YEAR  + ") VALUES ('Том Смит', 1981);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
