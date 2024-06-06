package com.example.workout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "workout.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "exercise_records";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_EXERCISE = "exercise";
    private static final String COLUMN_REPS = "reps"; // 수정: reps를 String으로 저장

    // 마커 테이블 추가
    private static final String TABLE_MARKER = "bar_locations";
    private static final String COLUMN_MARKER_ID = "id";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating tables...");

        String CREATE_EXERCISE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_EXERCISE + " TEXT, "
                + COLUMN_REPS + " TEXT)"; // 수정: reps를 String으로 저장
        db.execSQL(CREATE_EXERCISE_TABLE);

        String CREATE_MARKER_TABLE = "CREATE TABLE " + TABLE_MARKER + " ("
                + COLUMN_MARKER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LATITUDE + " REAL, "
                + COLUMN_LONGITUDE + " REAL, "
                + COLUMN_DESCRIPTION + " TEXT)";
        db.execSQL(CREATE_MARKER_TABLE);

        Log.d(TAG, "Tables created successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database...");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKER);
        onCreate(db);
    }

    // 철봉 위치를 데이터베이스에 추가
    public void addBarLocation(double latitude, double longitude, String description) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_LATITUDE, latitude);
            values.put(COLUMN_LONGITUDE, longitude);
            values.put(COLUMN_DESCRIPTION, description);
            long result = db.insert(TABLE_MARKER, null, values);
            Log.d(TAG, "Location added: " + result);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error adding location: " + e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    // 저장된 철봉 위치를 데이터베이스에서 불러오기
    public ArrayList<MarkerData> getBarLocations() {
        ArrayList<MarkerData> markers = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_MARKER, new String[]{COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_DESCRIPTION},
                    null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int latitudeIndex = cursor.getColumnIndex(COLUMN_LATITUDE);
                int longitudeIndex = cursor.getColumnIndex(COLUMN_LONGITUDE);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                if (latitudeIndex == -1 || longitudeIndex == -1 || descriptionIndex == -1) {
                    throw new IllegalStateException("Invalid column name used in query");
                }

                do {
                    double latitude = cursor.getDouble(latitudeIndex);
                    double longitude = cursor.getDouble(longitudeIndex);
                    String description = cursor.getString(descriptionIndex);
                    markers.add(new MarkerData(latitude, longitude, description));
                    Log.d(TAG, "Loaded marker: " + latitude + ", " + longitude + " - " + description);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading locations: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return markers;
    }

    // 기존 운동 기록 관련 메소드 유지
    public void addExerciseRecord(String date, String exercise, String reps) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_DATE, date);
            values.put(COLUMN_EXERCISE, exercise);
            values.put(COLUMN_REPS, reps);
            db.insert(TABLE_NAME, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public HashMap<String, ArrayList<SetData>> getExerciseRecords(String date) {
        HashMap<String, ArrayList<SetData>> exerciseDataMap = new HashMap<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME, new String[]{COLUMN_EXERCISE, COLUMN_REPS},
                    COLUMN_DATE + "=?", new String[]{date}, null, null, null);

            if (cursor != null) {
                int exerciseIndex = cursor.getColumnIndex(COLUMN_EXERCISE);
                int repsIndex = cursor.getColumnIndex(COLUMN_REPS);

                if (exerciseIndex == -1 || repsIndex == -1) {
                    throw new IllegalStateException("Invalid column name used in query");
                }

                while (cursor.moveToNext()) {
                    String exercise = cursor.getString(exerciseIndex);
                    String reps = cursor.getString(repsIndex);

                    SetData setData = new SetData(reps, true);
                    if (!exerciseDataMap.containsKey(exercise)) {
                        exerciseDataMap.put(exercise, new ArrayList<>());
                    }
                    exerciseDataMap.get(exercise).add(setData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return exerciseDataMap;
    }
}
