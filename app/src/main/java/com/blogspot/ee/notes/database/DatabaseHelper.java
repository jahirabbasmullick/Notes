package com.blogspot.ee.notes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import com.blogspot.ee.notes.database.model.Note;

import static com.blogspot.ee.notes.database.model.Note.TABLE_NAME;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;

    private static final String DATABASE_NAME="notes_db";

    public DatabaseHelper(Context context){

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST"+ Note.TABLE_NAME);

        onCreate(db);


    }
    public long insertNote(String note){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NOTE,note);
        long id= db.insert(Note.TABLE_NAME,null,values);
        db.close();
        return id;
    }
    public Note getNote(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Note.TABLE_NAME,new String[]{Note.COLUMN_ID,Note.COLUMN_NOTE,Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID +"=?",new String[]{String.valueOf(id)},null,null,null);
        if (cursor!=null)
            cursor.moveToFirst();
        Note note=new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));
        cursor.close();
        return note;

    }
    public List<Note>getAllNotes(){
        List<Note>notes= new ArrayList<>();
        String selectQuery="SELECT *FROM"+ TABLE_NAME+"ORDER BY"+Note.COLUMN_TIMESTAMP+"DESC";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                Note note=new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));
                notes.add(note);
            }while (cursor.moveToNext());
            }
        db.close();
        return notes;

    }
    public int getNotesCount(){
        String countQuery= "SELECT * FROM"+ TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int updateNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(Note.COLUMN_ID,note.getNote());
        return db.update(Note.TABLE_NAME,contentValues,Note.COLUMN_ID+"=?",new String[]{String.valueOf(note.getId())});

    }
    public void deleteNote(Note note){
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(Note.TABLE_NAME,Note.COLUMN_ID+"=?",new String[]{String.valueOf(note.getId())});
    db.close();
    }
}
