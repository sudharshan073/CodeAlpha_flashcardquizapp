
package com.example.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FlashcardQuiz.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE = "questions";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable =
                "CREATE TABLE " + TABLE + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "subject TEXT NOT NULL, " +
                        "question TEXT NOT NULL, " +
                        "option1 TEXT NOT NULL, " +
                        "option2 TEXT NOT NULL, " +
                        "option3 TEXT NOT NULL, " +
                        "option4 TEXT NOT NULL, " +
                        "correctAnswer INTEGER NOT NULL, " +
                        "explanation TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    private ContentValues toValues(Question q) {
        ContentValues values = new ContentValues();

        values.put("subject", q.subject);
        values.put("question", q.question);
        values.put("option1", q.option1);
        values.put("option2", q.option2);
        values.put("option3", q.option3);
        values.put("option4", q.option4);
        values.put("correctAnswer", q.correctAnswer);
        values.put("explanation", q.explanation);

        return values;
    }

    public long insertQuestion(Question q) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE, null, toValues(q));
    }

    public long addQuestion(Question q) {
        return insertQuestion(q);
    }

    public int updateQuestion(Question q) {
        SQLiteDatabase db = getWritableDatabase();

        return db.update(
                TABLE,
                toValues(q),
                "id = ?",
                new String[]{String.valueOf(q.id)}
        );
    }

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questions = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE,
                null,
                null,
                null,
                null,
                null,
                "id DESC")) {

            while (cursor.moveToNext()) {
                questions.add(readQuestion(cursor));
            }
        }

        return questions;
    }

    public Question getQuestionById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null)) {

            if (cursor.moveToFirst()) {
                return readQuestion(cursor);
            }
        }

        return null;
    }

    public Question getQuestion(int id) {
        return getQuestionById(id);
    }

    public int deleteQuestion(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    private Question readQuestion(Cursor cursor) {
        return new Question(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("subject")),
                cursor.getString(cursor.getColumnIndexOrThrow("question")),
                cursor.getString(cursor.getColumnIndexOrThrow("option1")),
                cursor.getString(cursor.getColumnIndexOrThrow("option2")),
                cursor.getString(cursor.getColumnIndexOrThrow("option3")),
                cursor.getString(cursor.getColumnIndexOrThrow("option4")),
                cursor.getInt(cursor.getColumnIndexOrThrow("correctAnswer")),
                cursor.getString(cursor.getColumnIndexOrThrow("explanation"))
        );
    }
}