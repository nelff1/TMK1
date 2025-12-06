package com.example.tmk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDB {

    private SQLiteDatabase database;
    private final DBHelper dbHelper;

    public UserDB(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (database != null && database.isOpen()) database.close();
    }

    public long addUser(String email, String password) {
        if (isEmailExists(email)) return -1;
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_EMAIL, email);
        values.put(DBHelper.COLUMN_PASSWORD, password);
        return database.insert(DBHelper.TABLE_USER, null, values);
    }

    public Cursor getUserByEmail(String email) {
        String[] columns = {DBHelper.COLUMN_ID, DBHelper.COLUMN_EMAIL, DBHelper.COLUMN_PASSWORD};
        String selection = DBHelper.COLUMN_EMAIL + "=?";
        String[] selectionArgs = {email};

        Cursor cursor = database.query(DBHelper.TABLE_USER, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) return cursor;
        return null;
    }

    public boolean isEmailExists(String email) {
        Cursor cursor = getUserByEmail(email);
        boolean exists = cursor != null;
        if (cursor != null) cursor.close();
        return exists;
    }

    public void updatePassword(String email, String newPassword) {
        if (!isEmailExists(email)) return;
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_PASSWORD, newPassword);
        database.update(DBHelper.TABLE_USER, values, DBHelper.COLUMN_EMAIL + "=?", new String[]{email});
    }

    public Cursor getAllUsers() {
        return database.query(DBHelper.TABLE_USER, new String[]{DBHelper.COLUMN_ID, DBHelper.COLUMN_EMAIL, DBHelper.COLUMN_PASSWORD}, null, null, null, null, null);
    }
}
