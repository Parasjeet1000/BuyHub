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
    public static final String DATABASE_NAME = "Appdb.db";
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

    public static final String listing = "CREATE TABLE " + ListingContract.ListingEntry.TABLE_NAME2 + " ("
            + ListingContract.ListingEntry.ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ListingContract.ListingEntry.UID2 + " TEXT,"
            + ListingContract.ListingEntry.TITLE_COL + " TEXT,"
            + ListingContract.ListingEntry.PRICE_COL + " INTEGER,"
            + ListingContract.ListingEntry.CATEGORY_COL + " TEXT,"
            + ListingContract.ListingEntry.CONDITION_COL + " TEXT,"
            + ListingContract.ListingEntry.DESCRIPTION_COL + " TEXT,"
            + ListingContract.ListingEntry.POSTAL_COL + " TEXT,"
            + ListingContract.ListingEntry.DATE_COL + " TEXT,"
            + ListingContract.ListingEntry.IMAGE_COL + " BLOB,"
            + ListingContract.ListingEntry.VIDEO_COL + " TEXT)";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(listing);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sdb, int i, int i1) {
        sdb.rawQuery("DROP TABLE IF EXISTS " + TABLE_NAME, null).close();
        sdb.rawQuery("DROP TABLE IF EXISTS " + ListingContract.ListingEntry.TABLE_NAME2, null).close();
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long rowID = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowID;
    }

    public Boolean addNewListing(String title, int price, String UID, String Category, String Description, String Condition, String Postal_code, String date, byte[] image, String video) {
        // on below line we are creating a variable for our sqlite database and calling writable method as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();
        // on below line we are creating a variable for content values.
        ContentValues values = new ContentValues();
        // on below line we are passing all values along with its key and value pair.
        values.put(ListingContract.ListingEntry.TITLE_COL, title);
        values.put(ListingContract.ListingEntry.PRICE_COL, price);
        values.put(ListingContract.ListingEntry.UID2, UID);
        values.put(ListingContract.ListingEntry.CATEGORY_COL, Category);
        values.put(ListingContract.ListingEntry.DESCRIPTION_COL, Description);
        values.put(ListingContract.ListingEntry.CONDITION_COL, Condition);
        values.put(ListingContract.ListingEntry.POSTAL_COL, Postal_code);
        values.put(ListingContract.ListingEntry.DATE_COL, date);
        values.put(ListingContract.ListingEntry.IMAGE_COL, image);
        values.put(ListingContract.ListingEntry.VIDEO_COL, video);

        // after adding all values we are passing content values to our table.
        long result = db.insert(ListingContract.ListingEntry.TABLE_NAME2, null, values);
        db.close();
        if(result == -1){
            return false;
        }
        else{
            return true;
        }
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

