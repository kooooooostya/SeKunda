package com.example.sekunda.Data;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BusinessSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Business_db";
    private static int DB_VERSION = 1;
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SECONDS = "seconds";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_SHORT_DATE = "short_date";
    private static final String COLUMN_IS_COMPLETE = "is_complete";
    static final String FULL_PATTERN = "ss-mm-HH-dd-MM-yyyy";
    static final String SHORT_PATTERN = "dd-MM-yyyy";

    private Context mContext;

    BusinessSQLiteOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ DB_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, " + COLUMN_SECONDS + " INTEGER, " + COLUMN_SHORT_DATE + " TEXT, "
                + COLUMN_START_DATE + " TEXT, " + COLUMN_IS_COMPLETE + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        DB_VERSION = newVersion;
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        DB_VERSION = newVersion;
        onCreate(db);
    }

    void insertBusinessAsync(Business business){
        AddBusinessAsync task = new AddBusinessAsync(getWritableDatabase());
        task.doInBackground(business);
    }

    void changeAsync(Business newBusiness, Business oldBusiness){
        ChangeTimeBusinessAsync task = new ChangeTimeBusinessAsync(getWritableDatabase());
        task.doInBackground(newBusiness, oldBusiness);
    }
    void deleteBusinessAsync(Business business){
        DeleteBusinessAsync task = new DeleteBusinessAsync(getWritableDatabase());
        task.doInBackground(business);
    }

    ArrayList<Business> getFulledList(Calendar calendar){

        ArrayList<Business> businessArrayList = new ArrayList<>();
        SimpleDateFormat shortDate = new SimpleDateFormat(SHORT_PATTERN, Locale.ENGLISH);
        SimpleDateFormat fullDate = new SimpleDateFormat(FULL_PATTERN, Locale.ENGLISH);
        String shortDateString = shortDate.format(calendar.getTime());
        try {

            Cursor cursor = this.getReadableDatabase().query(DB_NAME,
                    new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_SECONDS, COLUMN_SHORT_DATE, COLUMN_START_DATE, COLUMN_IS_COMPLETE},
                    COLUMN_SHORT_DATE + " = ?", new String[]{shortDateString},
                    null, null, COLUMN_SECONDS + " DESC");
// TODO данные теряются дата становиться на 112 часов больше
            if(cursor.moveToFirst()){
                do {
                    if(cursor.getInt(5) == 1){
                        Business business = new Business(cursor.getString(1),
                                cursor.getInt(2), calendar, true);
                        businessArrayList.add(business);
                    }else {
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(fullDate.parse(cursor.getString(4)));
                        String s = fullDate.format(calendar1.getTime());
                        Business business = new Business(cursor.getString(1),
                                cursor.getInt(2), calendar1, false);
                        businessArrayList.add(business);
                    }
                }while (cursor.moveToNext());
                cursor.close();
            }
        }catch (SQLException e) {
            Toast.makeText(mContext, "db error", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
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
                contentValues.put(COLUMN_SHORT_DATE, businesses[0].getShortCalendarTime());
                contentValues.put(COLUMN_START_DATE, businesses[0].getFullCalendarTime());
                contentValues.put(COLUMN_IS_COMPLETE, businesses[0].isComplete());

                mDatabase.insert(DB_NAME, null, contentValues);
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    private static class ChangeTimeBusinessAsync extends AsyncTask<Business, Void, Integer> {

        SQLiteDatabase mDatabase;

        ChangeTimeBusinessAsync(SQLiteDatabase db) {
            mDatabase = db;
        }

        @Override
        protected Integer doInBackground(Business... businesses) {
            try {
                //businesses[0] - new, businesses[1] - old
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_NAME, businesses[0].getName());
                contentValues.put(COLUMN_SECONDS, businesses[0].getSeconds());
                contentValues.put(COLUMN_START_DATE, businesses[0].getFullCalendarTime());
                contentValues.put(COLUMN_SHORT_DATE, businesses[0].getShortCalendarTime());
                contentValues.put(COLUMN_IS_COMPLETE, businesses[0].isComplete()?1:0);

                return mDatabase.update(DB_NAME, contentValues,
                        COLUMN_NAME + " = ?", new String[]{businesses[1].getName()});
            } catch (SQLException e) {
                return 0;
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

