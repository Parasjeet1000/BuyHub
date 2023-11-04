package com.example.marketplaceproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.EnumMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Login.db";
    public static final String TABLE_NAME = "Login";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRST_NAME = "FirstName";
    public static final String COLUMN_LAST_NAME = "LastName";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_PASS = "Password";
    public static final String UID = "uid";

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_FIRST_NAME + " TEXT," + COLUMN_LAST_NAME +
            " TEXT," + COLUMN_EMAIL + " TEXT," + UID + " TEXT," + COLUMN_PASS + " TEXT)";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sdb, int i, int i1) {
        sdb.rawQuery("DROP TABLE IF EXISTS " + TABLE_NAME, null).close();
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long rowID = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowID;
    }

    public boolean userExists(String email) {
        SQLiteDatabase sdb = this.getReadableDatabase();
        String[] cols = {COLUMN_EMAIL};
        String whereClause = COLUMN_EMAIL + " LIKE ?";
        String[] whereArgs = new String[]{email};
        Cursor user = sdb.query(TABLE_NAME, cols, whereClause, whereArgs, null, null, null);

        if (user.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }
    public Cursor getUser(String email) {
        SQLiteDatabase sdb = this.getReadableDatabase();
        String whereClause = COLUMN_EMAIL + " LIKE ?";
        String[] whereArgs = new String[]{email};
        return sdb.query(TABLE_NAME, new String[]{COLUMN_EMAIL, COLUMN_PASS}, whereClause, whereArgs, null, null, null);
    }

    @SuppressLint("Range")
    public String getUserFirstNameByUid(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_FIRST_NAME};
        String selection = UID + " = ?";
        String[] selectionArgs = {uid};

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        String firstName = null;
        if (cursor != null && cursor.moveToFirst()) {
            firstName = cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME));
            cursor.close();
        }

        return firstName;
    }

}

