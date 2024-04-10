package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASKS = "tasks";
    private static final String STATUS = "status";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TASKS + " TEXT, " +
            STATUS + " INTEGER)"; // Add the TEXT_COLOR field to the table creation statement


    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);

    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTasks(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TASKS, task.getTask());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);

    }

    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                do {
                    ToDoModel task = new ToDoModel();
                    int idIndex = cur.getColumnIndex(ID);
                    if (idIndex >= 0) {
                        task.setId(cur.getInt(idIndex));
                    }

                    int taskIndex = cur.getColumnIndex(TASKS);
                    if (taskIndex >= 0) {
                        task.setTask(cur.getString(taskIndex));
                    }

                    int statusIndex = cur.getColumnIndex(STATUS);
                    if (statusIndex >= 0) {
                        task.setStatus(cur.getInt(statusIndex));
                    }


                    taskList.add(task);
                } while (cur.moveToNext());
            }
        } finally {
            if (cur != null) {
                cur.close();
            }
            db.endTransaction();
        }
        return taskList;
    }

    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateTasks(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASKS, task);
        db.update(TODO_TABLE, cv, ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteTasks(int id) {
        db.delete(TODO_TABLE, ID + "=?", new String[]{String.valueOf(id)});
    }


}
