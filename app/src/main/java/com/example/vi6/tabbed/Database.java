package com.example.vi6.tabbed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by vi6 on 28-Feb-17.
 */

public class Database extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "nemojis";

    // Contacts table name
    private static final String TABLE_catIDpair = "catIDpair";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE "+TABLE_catIDpair+"("+KEY_ID+" TEXT, "+KEY_NAME+" TEXT)";
        db.execSQL(query);
        Log.e("DATABASE ","TABLE CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_catIDpair);
        // Create tables again
        onCreate(db);
    }

    void add_cat_id_pair(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID,id);
        values.put(KEY_NAME,name);

        db.insert(TABLE_catIDpair,null,values);
        Log.e("Database", "added "+name+"   "+id);
        db.close();
    }

    ArrayList<String> get_all_categoreies() {
        ArrayList<String> catList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_catIDpair,null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            catList.add(res.getString(res.getColumnIndex(KEY_NAME)));
            Log.e("added to category list ",res.getString(res.getColumnIndex(KEY_NAME)));
            res.moveToNext();
        }
        return catList;
    }

    String getCat(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_catIDpair, new String[] { KEY_ID,
                        KEY_NAME}, KEY_ID + "=?",
                    new String[] { String.valueOf(id) }, null, null, null, null);
            if (cursor.moveToFirst()) {
               // allEmojisPOJO pojo = new allEmojisPOJO(cursor.getString(0),
                     String name=cursor.getString(1);
               // cursor.close();
              //  return pojo;
                return name;
            }
        return "demo";

       // return contact

    }

    String get_cat_name_from_id(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res=null;
        String q1="SELECT "+KEY_NAME+" FROM "+TABLE_catIDpair+" WHERE "+KEY_ID+"=?";
        Log.e("Database ", q1);
        String name="";
        try {
            res = db.rawQuery(q1, new String[]{id+""});
            if (res.getCount() > 0) {
                res.moveToFirst();
                name = res.getString(res.getColumnIndex(KEY_NAME));
            //    Log.e("Database returns ", name);

            }
            return name;

        } finally{
            Log.e("Database returns ", name);
            res.close();
        }

        /*if (res != null && res.moveToFirst()) {
            String name = res.getString(1);
            Log.e("DATABASE ", "RETURN : "+name);
            res.close();
            return name;
        } else {
            res.close();
            return null;
        }*/
    }
}
