package com.example.workout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "workout.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "exercise_records";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_EXERCISE = "exercise";
    private static final String COLUMN_REPS = "reps"; // 거리와 시간을 저장하는데 사용

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_EXERCISE + " TEXT, "
                + COLUMN_REPS + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addExerciseRecord(String date, String exercise, int reps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_EXERCISE, exercise);
        values.put(COLUMN_REPS, reps);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public HashMap<String, ArrayList<SetData>> getExerciseRecords(String date) {
        HashMap<String, ArrayList<SetData>> exerciseDataMap = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_EXERCISE, COLUMN_REPS},
                COLUMN_DATE + "=?", new String[]{date}, null, null, null);

        int exerciseIndex = cursor.getColumnIndex(COLUMN_EXERCISE);
        int repsIndex = cursor.getColumnIndex(COLUMN_REPS);

        if (exerciseIndex == -1 || repsIndex == -1) {
            throw new IllegalStateException("Invalid column name used in query");
        }

        while (cursor.moveToNext()) {
            String exercise = cursor.getString(exerciseIndex);
            int reps = cursor.getInt(repsIndex);

            SetData setData = new SetData(reps, true);
            if (!exerciseDataMap.containsKey(exercise)) {
                exerciseDataMap.put(exercise, new ArrayList<>());
            }
            exerciseDataMap.get(exercise).add(setData);
        }

        cursor.close();
        db.close();
        return exerciseDataMap;
    }
}
