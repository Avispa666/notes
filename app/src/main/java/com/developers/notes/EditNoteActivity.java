package com.developers.notes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class EditNoteActivity extends AppCompatActivity {

    public static final String FILE_EXTENSION = "note";
    EditText noteContentText;
    EditText noteNameText;
    DBHelper dbHelper;
    SQLiteDatabase notesDB;
    private static String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        noteContentText = (EditText) findViewById(R.id.noteContentText);
        noteNameText = (EditText) findViewById(R.id.noteNameText);
        dbHelper = new DBHelper(this);
        notesDB = dbHelper.getReadableDatabase();
        Intent editNote = getIntent();
        noteNameText.setText(editNote.getStringExtra("notename"));
        fileName = editNote.getStringExtra("filename");
        openNote(fileName);
        //TODO get filename from database
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void saveNote(String fileName) {
        if (!noteNameText.getText().toString().isEmpty() && !noteContentText.toString().isEmpty()) {
            try {
                notesDB.execSQL("UPDATE " + DBHelper.TABLE_NAME + " SET " + DBHelper.NOTE_NAME_COLUMN +
                        " = '" + noteNameText.getText().toString() + "' WHERE " + DBHelper.FILE_NAME_COLUMN + " = '" + fileName + "'");
                OutputStream outputStream = openFileOutput(fileName + "." + FILE_EXTENSION, 0);
                OutputStreamWriter osw = new OutputStreamWriter(outputStream);
                osw.write(noteContentText.getText().toString());
                osw.close();
            } catch (Throwable t) {
                Log.d("TAG", t.toString());
                Toast.makeText(getApplicationContext(),
                        "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
        else {
            String path = this.getFilesDir().getPath() + "/" + fileName + "." + CreateNoteActivity.FILE_EXTENSION;
            notesDB.execSQL("DELETE FROM " + DBHelper.TABLE_NAME + " WHERE " + DBHelper.FILE_NAME_COLUMN
                    + " = '" + fileName + "'");
            File myNote = new File(path);
            myNote.delete();
        }
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
                    builder.append(line);
                }

                inputStream.close();
                noteContentText.setText(builder.toString());
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {
        if (!noteContentText.getText().toString().isEmpty()) {
            saveNote(fileName);
        }
        super.onBackPressed();
    }
}
