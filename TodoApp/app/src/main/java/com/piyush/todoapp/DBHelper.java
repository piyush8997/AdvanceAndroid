package com.piyush.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper db;

    private DBHelper(Context context) {
        super(context, Statics.DB_NAME, null, Statics.DB_VERSION);
    }

    public static DBHelper getDBInstance(Context context){
        if(db == null){
            return new DBHelper(context);
        }else{
            return db;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = String.format("create table %s(%s integer primary key autoincrement, %s text not null, %s text not null)",
                Statics.TABLE_NAME, Statics.COL_NOTE_ID, Statics.COL_NOTE_TITLE, Statics.COL_NOTE_TEXT);
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = String.format("drop table if exists %s",Statics.TABLE_NAME);
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
    }

    public long addNote(String title, String note){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Statics.COL_NOTE_TITLE, title);
        contentValues.put(Statics.COL_NOTE_TEXT, note);
        return db.insert(Statics.TABLE_NAME,null,contentValues);
    }

    public List<Note> getNotes(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from "+Statics.TABLE_NAME,null);
        List<Note> noteList = new ArrayList<>();
        while (c.moveToNext()){
            Note n = new Note();
            n.setId(c.getInt(0));
            n.setTitle(c.getString(1));
            n.setText(c.getString(2));
            noteList.add(n);
        }
        c.close();
        return noteList;
    }

    public long deleteNote(String id){
        SQLiteDatabase database = getWritableDatabase();
        return database.delete(Statics.TABLE_NAME, Statics.COL_NOTE_ID+"=?", new String[]{id});
    }

    public long updateNote(String id, String title, String note){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Statics.COL_NOTE_TITLE, title);
        contentValues.put(Statics.COL_NOTE_TEXT, note);
        return database.update(Statics.TABLE_NAME, contentValues, Statics.COL_NOTE_ID+"=?", new String[]{id});
    }
}
