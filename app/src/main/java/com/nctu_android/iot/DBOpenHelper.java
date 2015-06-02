package com.nctu_android.iot;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    String PARKTABLE = "parktable";

    public DBOpenHelper(Context context) {
        super(context, "note.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //BAGTABLE裡包括使用者所擁有的monster的id
        db.execSQL("create table parktable (Park_id,status,book);");

        //初始化BAGTABLE
        ContentValues cv = new ContentValues();
        cv.put("Park_id", "1");
        cv.put("status", 0);
        cv.put("book", 0);
        db.insert(PARKTABLE, null, cv);

        cv.put("Park_id", "2");
        cv.put("status", 0);
        cv.put("book", 0);
        db.insert(PARKTABLE, null, cv);

        cv.put("Park_id", "3");
        cv.put("status", 0);
        cv.put("book", 0);
        db.insert(PARKTABLE, null, cv);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
    }

}
