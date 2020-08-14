package com.example.sekunda.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BusinessSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Business_db";
    private static int DB_VERSION = 1;
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SECONDS = "seconds";
    private static final String COLUMN_DATE = "date";

    private Context mContext;

    public BusinessSQLiteOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ DB_NAME + "( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, " + COLUMN_SECONDS + " INTEGER, " + COLUMN_DATE + " TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        DB_VERSION = newVersion;
        onCreate(db);
    }

    void insertBusinessAsync(Business business){
        AddBusinessAsync task = new AddBusinessAsync(getWritableDatabase());
        task.doInBackground(business);
    }

    public void changeAsync(Business newBusiness, Business oldBusiness){
        ChangeTimeBusinessAsync task = new ChangeTimeBusinessAsync(getWritableDatabase());
        task.doInBackground(newBusiness, oldBusiness);
    }
    public void deleteBusinessAsync(Business business){
        DeleteBusinessAsync task = new DeleteBusinessAsync(getWritableDatabase());
        task.doInBackground(business);
    }

    public ArrayList<Business> getFulledList(Calendar calendar){

        ArrayList<Business> businessArrayList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String currentDate = simpleDateFormat.format(calendar.getTime());


        try {
            Cursor cursor = this.getWritableDatabase().query(DB_NAME,
                    new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_SECONDS, COLUMN_DATE}, COLUMN_DATE + " = ? ", new String[]{currentDate},
                    null, null, null);

            if(cursor.moveToFirst()){
                do {
                    Business business = new Business(cursor.getString(1),
                            cursor.getInt(2), calendar);
                    businessArrayList.add(business);
                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (SQLException e) {
            Toast.makeText(mContext, "db error", Toast.LENGTH_SHORT).show();
        }
        return businessArrayList;
    }


    private static class AddBusinessAsync extends AsyncTask<Business, Void, Boolean> {

        SQLiteDatabase mDatabase;

        AddBusinessAsync(SQLiteDatabase db) {
            mDatabase = db;
        }
        @Override
        protected Boolean doInBackground(Business... businesses) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_NAME, businesses[0].getName());
                contentValues.put(COLUMN_SECONDS, businesses[0].getSeconds());
                contentValues.put(COLUMN_DATE, businesses[0].getCalendarTime());
                mDatabase.insert(DB_NAME, null, contentValues);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    private static class ChangeTimeBusinessAsync extends AsyncTask<Business, Void, Boolean> {

        SQLiteDatabase mDatabase;

        ChangeTimeBusinessAsync(SQLiteDatabase db) {
            mDatabase = db;
        }

        @Override
        protected Boolean doInBackground(Business... businesses) {
            try {
                // businesses[0] - new, businesses[1] - old
                mDatabase.delete(DB_NAME,
                        COLUMN_NAME + " = ? ", new String[]{businesses[1].getName()});
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_NAME, businesses[0].getName());
                contentValues.put(COLUMN_SECONDS, businesses[0].getSeconds());
                contentValues.put(COLUMN_DATE, businesses[0].getCalendarTime());
                mDatabase.insert(DB_NAME, null, contentValues);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    private static class DeleteBusinessAsync extends AsyncTask<Business, Void, Boolean> {

        SQLiteDatabase mDatabase;

        DeleteBusinessAsync(SQLiteDatabase db) {
            mDatabase = db;
        }

        @Override
        protected Boolean doInBackground(Business... businesses) {
            try {
                mDatabase.delete(DB_NAME,
                        COLUMN_NAME + " = ? ", new String[]{businesses[0].getName()});
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }
}

