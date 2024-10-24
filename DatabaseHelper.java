package com.zybooks.project2cs_360;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WeightData.db";
    private static final int DATABASE_VERSION = 1;

    // Weight data table and columns
    public static final String TABLE_WEIGHT_DATA = "weight_data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WEIGHT_VALUE = "weight_value";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_USER_ID = "user_id"; // Add this if you're associating data with users

    // User data table and columns
    public static final String TABLE_USER_DATA = "user_data"; // Define your user data table
    public static final String COLUMN_USERNAME = "username"; // Define username column
    public static final String COLUMN_PASSWORD = "password"; // Define password column

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create weight data table
        String createWeightTable = "CREATE TABLE " + TABLE_WEIGHT_DATA + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WEIGHT_VALUE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_USER_ID + " INTEGER)"; // Include user ID
        db.execSQL(createWeightTable);

        // Create user data table
        String createUserTable = "CREATE TABLE " + TABLE_USER_DATA + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHT_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DATA); // Add user data table drop
        onCreate(db);
    }

    // Method to register a new user
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USER_DATA, null, contentValues);
        db.close();
        return result != -1; // If the result is -1, insertion failed
    }

    // Method to validate user login
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USERNAME, COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USER_DATA, columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0; // Check if a user with the provided username and password exists
        cursor.close();
        db.close();
        return exists;
    }

    public void addWeightData(String weight, String date, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEIGHT_VALUE, weight);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_USER_ID, userId); // Store the user ID
        db.insert(TABLE_WEIGHT_DATA, null, values);
    }

    public void deleteWeightData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHT_DATA, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getWeightData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_WEIGHT_DATA,
                null, // Get all columns
                COLUMN_USER_ID + " = ?", // Filter by user ID
                new String[]{String.valueOf(userId)},
                null, null, null);
    }

    public int getWeightDataId(String formattedData) {
        // Assuming formattedData is like "70 kg (2024-10-17)"
        String[] parts = formattedData.split(" \\(");
        String weight = parts[0].trim(); // Extract weight
        String date = parts[1].replace(")", "").trim(); // Extract date

        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_WEIGHT_VALUE + " = ? AND " + COLUMN_DATE + " = ?";
        String[] selectionArgs = {weight, date};

        Cursor cursor = db.query(TABLE_WEIGHT_DATA, new String[]{COLUMN_ID}, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
            return id;
        }
        if (cursor != null) {
            cursor.close();
        }
        return -1; // Return -1 if not found
    }
}
