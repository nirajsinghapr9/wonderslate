package com.example.wonderslate.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wonderslate.Data;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "sampleDb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "mydata";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "file";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " BLOB)";
        db.execSQL(query);
    }

    public void addFile(byte[] file) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME_COL, file);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Data> readData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ArrayList<Data> fileList = new ArrayList<>();
        if (cursorCourses.moveToFirst()) {
            do {
                fileList.add(new Data(cursorCourses.getBlob(1),cursorCourses.getInt(0)));
            } while (cursorCourses.moveToNext());
        }
        cursorCourses.close();
        return fileList;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
