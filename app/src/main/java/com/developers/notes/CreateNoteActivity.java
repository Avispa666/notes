package com.developers.notes;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateNoteActivity extends AppCompatActivity {

    public static final String FILE_EXTENSION = "note";
    EditText noteContentText;
    EditText noteNameText;
    DBHelper dbHelper;
    SQLiteDatabase notesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        noteContentText = (EditText) findViewById(R.id.noteContentText);
        noteNameText = (EditText) findViewById(R.id.noteNameText);
        dbHelper = new DBHelper(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private String saveNote() {
        Date date = new Date();
        SimpleDateFormat namePattern = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String fileName = namePattern.format(date);
        try {
            OutputStream outputStream = openFileOutput(fileName + "." + FILE_EXTENSION, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(noteContentText.getText().toString());
            osw.close();
        } catch (Throwable t) {
            Log.d("TAG", t.toString());
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return fileName;
    }

    public void onBackPressed() {
        if (!noteContentText.getText().toString().isEmpty()) {
            ContentValues cv = new ContentValues();
            notesDB = dbHelper.getWritableDatabase();
            String noteName = noteNameText.getText().toString();
            if (noteName.equals("Avispa666") && noteContentText.getText().toString().equals("Avispa666"))
                Toast.makeText(getApplicationContext(),
                    "You love me a lot :)", Toast.LENGTH_LONG).show();
            String fileName = saveNote();
            cv.put(DBHelper.FILE_NAME_COLUMN, fileName);
            cv.put(DBHelper.NOTE_NAME_COLUMN, noteName);
            notesDB.insert(DBHelper.TABLE_NAME, null, cv);
            dbHelper.close();
            notesDB.close();
        }
        super.onBackPressed();
    }

}
