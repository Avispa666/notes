package com.developers.notes;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreatingNoteActivity extends AppCompatActivity {

    public static final String FILE_EXTENSION = "note";
    public static final String TABLE_NAME = "NOTES";
    public static final String DB_NAME = "NOTES";
    public static final String NOTE_NAME_COLUMN = "NOTE_NAME";
    public static final String FILE_NAME_COLUMN = "FILE_NAME";
    Button saveNoteButton;
    Button openNoteButton;
    EditText noteContentText;
    EditText noteNameText;
    FloatingActionButton fab;
    DBHelper dbHelper;
    SQLiteDatabase notesDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        saveNoteButton = (Button) findViewById(R.id.saveNoteButton);
        openNoteButton = (Button) findViewById(R.id.openNoteButton);
        noteContentText = (EditText) findViewById(R.id.noteContentText);
        noteNameText = (EditText) findViewById(R.id.noteNameText);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        dbHelper = new DBHelper(this);
        setSupportActionBar(toolbar);
        View.OnClickListener openSaveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String fileName = "";
                String noteName = "";
                switch (v.getId()) {
                    case R.id.saveNoteButton:
                        fileName = saveNote();
                        noteName = noteNameText.getText().toString();
                        cv.put(FILE_NAME_COLUMN, fileName);
                        cv.put(NOTE_NAME_COLUMN, noteName);
                        long rowID = db.insert(TABLE_NAME, null, cv);
                        Toast.makeText(getApplicationContext(),
                                "added line id " + rowID, Toast.LENGTH_LONG).show();
                        break;
                    case  R.id.openNoteButton:
//                        fileName = getFileName();
//                        openNote(fileName);
                        break;
                    default:
                        //TODO default toast message
                }
                dbHelper.close();
                db.close();
            }
        };
        saveNoteButton.setOnClickListener(openSaveListener);
        openNoteButton.setOnClickListener(openSaveListener);
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
//            values.put(DBHelper.FILE_NAME_COLUMN, fileName);
//            values.put(DBHelper.NOTE_NAME_COLUMN, noteNameText.getText().toString());
//            notesDB.insert(DB_NAME, null, values);
//            notesDB.close();
//            dbHelper.close();
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return fileName;
    }
    private void openNote(String fileName) {
        try {
            InputStream inputStream = openFileInput(fileName + "." + FILE_EXTENSION);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                inputStream.close();
                noteContentText.setText(builder.toString());
                noteNameText.setText(fileName);
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }
    private String getFileName() { // now useless
        return noteNameText.getText().toString();
    }

}
