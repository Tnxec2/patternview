package com.example.thanx2.patternview.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.thanx2.patternview.model.Pattern;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    private final static int MAX_PATTERN_COUNT = 20;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context){
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open(){
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private Cursor getAllEntries(){
        String[] columns = new String[] {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_URI,
                DatabaseHelper.COLUMN_ROWHEIGHT,
                DatabaseHelper.COLUMN_SCROLLX,
                DatabaseHelper.COLUMN_SCROLLY,
                DatabaseHelper.COLUMN_SCALE,
                DatabaseHelper.COLUMN_LASTOPENED
        };
        return  database.query(DatabaseHelper.TABLE, columns, null, null, null, null, DatabaseHelper.COLUMN_LASTOPENED + " DESC");
    }

    public List<Pattern> getPatterns(){
        ArrayList<Pattern> patterns = new ArrayList<>();
        Cursor cursor = getAllEntries();
        if(cursor.moveToFirst()){
            do{
                Pattern pattern = new Pattern(
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_URI)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROWHEIGHT)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCROLLX)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCROLLY)),
                        cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCALE)));
                pattern.setLastOpened(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_LASTOPENED)));
                patterns.add(pattern);

            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return  patterns;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE);
    }

    public Pattern getPattern(long id){
        Pattern pattern = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?", DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            pattern = new Pattern(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_URI)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROWHEIGHT)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCROLLX)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCROLLY)),
                    cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCALE))
            );
            pattern.setLastOpened(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_LASTOPENED)));
        }
        cursor.close();
        return  pattern;
    }

    public Pattern getPatternByUri(String uri){
        Pattern pattern = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?", DatabaseHelper.TABLE, DatabaseHelper.COLUMN_URI);
        Cursor cursor = database.rawQuery(query, new String[]{ uri });
        if(cursor.moveToFirst()){
            pattern = new Pattern(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_URI)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROWHEIGHT)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCROLLX)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCROLLY)),
                    cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCALE))
            );
            pattern.setLastOpened(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_LASTOPENED)));
        }
        cursor.close();
        return  pattern;
    }


    public long insert(Pattern pattern){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_URI, pattern.getUri());
        cv.put(DatabaseHelper.COLUMN_ROWHEIGHT, pattern.getRowHeight());
        cv.put(DatabaseHelper.COLUMN_SCROLLX, pattern.getImageScrollX());
        cv.put(DatabaseHelper.COLUMN_SCROLLY, pattern.getImageScrollY());
        cv.put(DatabaseHelper.COLUMN_SCALE, pattern.getImageScalle());
        cv.put(DatabaseHelper.COLUMN_LASTOPENED, pattern.getLastOpened());

        return  database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public long delete(long patternId){
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(patternId)};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public long deleteByUri(String uri){
        String whereClause = DatabaseHelper.COLUMN_URI + " = ?";
        String[] whereArgs = new String[]{uri};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public long update(Pattern pattern){

        String whereClause = DatabaseHelper.COLUMN_ID + "=" + String.valueOf(pattern.getId());
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_URI, pattern.getUri());
        cv.put(DatabaseHelper.COLUMN_ROWHEIGHT, pattern.getRowHeight());
        cv.put(DatabaseHelper.COLUMN_SCROLLX, pattern.getImageScrollX());
        cv.put(DatabaseHelper.COLUMN_SCROLLY, pattern.getImageScrollY());
        cv.put(DatabaseHelper.COLUMN_SCALE, pattern.getImageScalle());
        cv.put(DatabaseHelper.COLUMN_LASTOPENED, pattern.getLastOpened());

        return database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }

    /*
     * Bereinigen Database auf maximale Anzahl
     */
    public void clean() {
        long count = getCount();
        if ( count > MAX_PATTERN_COUNT ) {
            String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT ?", DatabaseHelper.TABLE, DatabaseHelper.COLUMN_LASTOPENED);
            Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(MAX_PATTERN_COUNT) });
            if(cursor.moveToLast()){
                String whereClause = DatabaseHelper.COLUMN_LASTOPENED + " > ?";
                String[] whereArgs = new String[]{String.valueOf(cursor.getColumnIndex(DatabaseHelper.COLUMN_LASTOPENED))};
                database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
            }
            cursor.close();
        }
    }
}