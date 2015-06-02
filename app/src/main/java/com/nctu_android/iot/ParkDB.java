package com.nctu_android.iot;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


//對parktable的操作
public class ParkDB {

    final static String PARKTABLE = "parktable";

    //取出當前status
    static int getStatus(SQLiteDatabase db, int id) {

        Cursor c = db.rawQuery("select status from " + PARKTABLE + " where Park_id='" + id +"';", null);
        c.moveToFirst();
        return c.getInt(c.getColumnIndex("status"));
    }

    //取出是否預約
    static int getBook(SQLiteDatabase db, int id) {

        Cursor c = db.rawQuery("select book from " + PARKTABLE + " where Park_id='" + id +"';", null);
        c.moveToFirst();
        return c.getInt(c.getColumnIndex("book"));
    }


    //修改status
    static boolean editStatus(SQLiteDatabase db, int id, int status){

        ContentValues cv = new ContentValues();
        cv.put("status", status);
        //String str="UPDATE parktable SET status=1 WHERE Park_id=2";
        return db.update(PARKTABLE, cv, "Park_id='"+id+"'", null)>0;

    }



    //修改book
    static boolean editBook(SQLiteDatabase db, int id, int book){

        ContentValues cv = new ContentValues();
        cv.put("book", book);
        return db.update(PARKTABLE, cv, "Park_id='"+id+"'", null)>0;

    }
}
