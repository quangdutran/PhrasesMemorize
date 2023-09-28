package com.dutq.mywordingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class WordDBHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DB_NAME = "Word.db";
    private static int DB_VERSION = 1;
    public static final String TABLE_NAME = "words";
    private static final String ID = "id";
    private static final String COL_KEY = "_key";
    public static final String COL_PHRASE = "phrase";
    public static final String COL_MEANING = "meaning";
    private static final String COL_DATE_ADDED = "added";

    private static WordDBHelper instance;

    public static synchronized WordDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new WordDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private WordDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_KEY + " TEXT, " +
                COL_PHRASE + " TEXT, " +
                COL_MEANING + " TEXT, " +
                COL_DATE_ADDED + " DATETIME DEFAULT CURRENT_TIMESTAMP);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void addWord(String phrase, String meaning) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PHRASE, phrase);
        contentValues.put(COL_MEANING, meaning);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Toast.makeText(context, "Could not add word", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Added successfully", Toast.LENGTH_LONG).show();
        }
    }

    public static String getRandomPhrase() {
        String query = "SELECT "+
                WordDBHelper.COL_PHRASE +
                "," +
                WordDBHelper.COL_MEANING +
                " FROM " +
                WordDBHelper.TABLE_NAME +
                " ORDER BY RANDOM() LIMIT 1";
        SQLiteDatabase readDB = instance.getReadableDatabase();
        String result = "";
        Cursor cursor = readDB.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            String phrase = cursor.getString(0);
            String meaning = cursor.getString(1);
            result = phrase + " = " + meaning;
        }
        return result;
    }
}
