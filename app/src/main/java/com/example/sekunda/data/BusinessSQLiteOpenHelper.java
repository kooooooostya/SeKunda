//package com.example.sekunda.data;
//
//import android.content.ContentValues;
//import android.content.Context;
//
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.os.AsyncTask;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Locale;
//import java.util.Objects;
//
//public class BusinessSQLiteOpenHelper extends SQLiteOpenHelper {
//
//    private static final String DB_NAME = "Business_db";
//    private static int DB_VERSION = 1;
//    private static final String COLUMN_ID = "_id";
//    private static final String COLUMN_NAME = "name";
//    private static final String COLUMN_SECONDS = "seconds";
//    private static final String COLUMN_START_DATE = "start_date";
//    private static final String COLUMN_SHORT_DATE = "short_date";
//    private static final String COLUMN_IS_COMPLETE = "is_complete";
//    static final String FULL_PATTERN = "ss-mm-HH-dd-MM-yyyy";
//    static final String SHORT_PATTERN = "dd-MM-yyyy";
//
//    private final Context mContext;
//    private static boolean isDeletedToday = false;
//
//    public BusinessSQLiteOpenHelper(Context context) {
//        super(context, DB_NAME, null, DB_VERSION);
//        mContext = context;
//        if (!isDeletedToday && Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
//            deleteOutdatedBusinessAsync(Calendar.getInstance());
//        }
//        isDeletedToday = true;
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE "+ DB_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + COLUMN_NAME + " TEXT, " + COLUMN_SECONDS + " INTEGER, " + COLUMN_SHORT_DATE + " TEXT, "
//                + COLUMN_START_DATE + " TEXT, " + COLUMN_IS_COMPLETE + " INTEGER)");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
//        DB_VERSION = newVersion;
//        onCreate(db);
//    }
//
//    @Override
//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
//        DB_VERSION = newVersion;
//        onCreate(db);
//    }
//
//    //
//    public void insertBusinessAsync(Business business){
//        AddBusinessAsync task = new AddBusinessAsync(getWritableDatabase());
//        task.doInBackground(business);
//    }
//
////
//    public void changeAsync(Business newBusiness, Business oldBusiness){
//        ChangeTimeBusinessAsync task = new ChangeTimeBusinessAsync(getWritableDatabase());
//        task.doInBackground(newBusiness, oldBusiness);
//    }
//
//    private void deleteOutdatedBusinessAsync(Calendar calendar){
//        DeleteOutdatedBusinessAsync task =
//                new DeleteOutdatedBusinessAsync(getWritableDatabase(), calendar);
//        task.execute();
//    }
//
//
//    public ArrayList<Business> getFulledList(Calendar calendar){
//
//        ArrayList<Business> businessArrayList = new ArrayList<>();
//        SimpleDateFormat shortDate = new SimpleDateFormat(SHORT_PATTERN, Locale.ENGLISH);
//        SimpleDateFormat fullDate = new SimpleDateFormat(FULL_PATTERN, Locale.ENGLISH);
//        String shortDateString = shortDate.format(calendar.getTime());
//        try {
//
//            Cursor cursor = this.getReadableDatabase().query(DB_NAME,
//                    new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_SECONDS, COLUMN_SHORT_DATE, COLUMN_START_DATE, COLUMN_IS_COMPLETE},
//                    COLUMN_SHORT_DATE + " = ?", new String[]{shortDateString},
//                    null, null, COLUMN_SECONDS + " DESC");
//            if(cursor.moveToFirst()){
//                do {
//                    if(cursor.getInt(5) == 1){
//                        Business business = new Business(cursor.getString(1),
//                                cursor.getInt(2), calendar, true);
//                        businessArrayList.add(business);
//                    }else {
//                        Calendar calendar1 = Calendar.getInstance();
//                        calendar1.setTime(Objects.requireNonNull(fullDate.parse(cursor.getString(4))));
//                        Business business = new Business(cursor.getString(1),
//                                cursor.getInt(2), calendar1, false);
//                        businessArrayList.add(business);
//                    }
//                }while (cursor.moveToNext());
//                cursor.close();
//            }
//        }catch (SQLException e) {
//            Toast.makeText(mContext, "db error", Toast.LENGTH_SHORT).show();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return businessArrayList;
//    }
//
//
//    private static class AddBusinessAsync extends AsyncTask<Business, Void, Boolean> {
//
//        SQLiteDatabase mDatabase;
//
//        AddBusinessAsync(SQLiteDatabase db) {
//            mDatabase = db;
//        }
//        @Override
//        protected Boolean doInBackground(Business... businesses) {
//            try {
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(COLUMN_NAME, businesses[0].getName());
//                contentValues.put(COLUMN_SECONDS, businesses[0].getSeconds());
//                contentValues.put(COLUMN_SHORT_DATE, businesses[0].getShortCalendarTime());
//                contentValues.put(COLUMN_START_DATE, businesses[0].getFullCalendarTime());
//                contentValues.put(COLUMN_IS_COMPLETE, businesses[0].isComplete());
//
//                mDatabase.insert(DB_NAME, null, contentValues);
//                return true;
//            } catch (SQLException e) {
//                return false;
//            }
//        }
//    }
//
//    private static class ChangeTimeBusinessAsync extends AsyncTask<Business, Void, Integer> {
//
//        SQLiteDatabase mDatabase;
//
//        ChangeTimeBusinessAsync(SQLiteDatabase db) {
//            mDatabase = db;
//        }
//
//        @Override
//        protected Integer doInBackground(Business... businesses) {
//            try {
//                //businesses[0] - new, businesses[1] - old
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(COLUMN_NAME, businesses[0].getName());
//                contentValues.put(COLUMN_SECONDS, businesses[0].getSeconds());
//                contentValues.put(COLUMN_START_DATE, businesses[0].getFullCalendarTime());
//                contentValues.put(COLUMN_SHORT_DATE, businesses[0].getShortCalendarTime());
//                contentValues.put(COLUMN_IS_COMPLETE, businesses[0].isComplete()?1:0);
//
//                return mDatabase.update(DB_NAME, contentValues,
//                        COLUMN_NAME + " = ?", new String[]{businesses[1].getName()});
//            } catch (SQLException e) {
//                return 0;
//            }
//        }
//    }
//
//    private static class DeleteOutdatedBusinessAsync extends AsyncTask<Business, Void, Boolean> {
//
//        SQLiteDatabase mDatabase;
//        Calendar mDeleteBeforeThis;
//
//        DeleteOutdatedBusinessAsync(SQLiteDatabase db, Calendar calendar) {
//            mDatabase = db;
//            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
//            mDeleteBeforeThis = calendar;
//        }
//
//        @Override
//        protected Boolean doInBackground(@Nullable Business... businesses) {
//            try {
//                SimpleDateFormat shortDate = new SimpleDateFormat(SHORT_PATTERN, Locale.ENGLISH);
//                int i;
//                // this is necessary to stop the cycle, if there are no tasks in 7 days, then the cycle will stop
//                int stepToStop = 7;
//                do{
//                   i = mDatabase.delete(DB_NAME,
//                            COLUMN_SHORT_DATE + " = ? ",
//                            new String[]{shortDate.format(mDeleteBeforeThis.getTime())});
//                   if(i > 0) stepToStop = 7;
//                   else stepToStop--;
//                }while (stepToStop > 0);
//
//                return true;
//            } catch (SQLException e) {
//                return false;
//            }
//        }
//    }
//}
//
