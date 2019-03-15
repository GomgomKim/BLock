package com.example.block.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper {
    private static final String DATABASE_NAME = "BLOCK.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBases.CreateDB.CREATEMEMBER);
            db.execSQL(DataBases.CreateDB.CREATEDOOR);
            db.execSQL(DataBases.CreateDB.CREATEHOST);
            db.execSQL(DataBases.CreateDB.CREATEGUEST);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB.MEMBER);
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB.DOOR);
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB.HOST);
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB.GUEST);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    // Insert DB
    public long insertColumn(int userid, String name, String intro , String home_door_id){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.USERID, userid);
        values.put(DataBases.CreateDB.USERNAME, name);
        values.put(DataBases.CreateDB.USERINTRO, intro);
        values.put(DataBases.CreateDB.HOMEDOORID, home_door_id);
        return mDB.insert(DataBases.CreateDB.MEMBER, null, values);
    }

    // Update DB
    public boolean updateColumn(int userid, String name, String intro , String home_door_id){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.USERID, userid);
        values.put(DataBases.CreateDB.USERNAME, name);
        values.put(DataBases.CreateDB.USERINTRO, intro);
        values.put(DataBases.CreateDB.HOMEDOORID, home_door_id);
        return mDB.update(DataBases.CreateDB.MEMBER, values, "_id=" + userid, null) > 0;
    }

    // Delete All
    public void deleteAllColumns() {
        mDB.delete(DataBases.CreateDB.MEMBER, null, null);
    }

    // Delete DB
    public boolean deleteColumn(long id){
        return mDB.delete(DataBases.CreateDB.MEMBER, "_id="+id, null) > 0;
    }
    // Select DB
    public Cursor selectColumns(){
        return mDB.query(DataBases.CreateDB.MEMBER, null, null, null, null, null, null);
    }

    // sort by column
    public Cursor sortColumn(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM usertable ORDER BY " + sort + ";", null);
        return c;
    }
}
