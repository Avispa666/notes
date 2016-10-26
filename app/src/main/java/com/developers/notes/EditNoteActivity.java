package com.developers.notes;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
    private EditText noteContentText;
    private EditText noteNameText;
    private DBHelper dbHelper;
    private SQLiteDatabase notesDB;
    private File myNote;
    private Crypto crypto;
    private String password;
    private static String fileName;
    private static String isEncryptionActivated;

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
        isEncryptionActivated = editNote.getStringExtra("isEncryptionActivated");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (isEncryptionActivated.equals("true")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter the password");

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);
// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    password = input.getText().toString();
                    openNote(fileName, isEncryptionActivated);
                }
            });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
            builder.show();
        } else {
            openNote(fileName, isEncryptionActivated);
        }


    }

    private void saveNote(String fileName) {
        try {
            if (isEncryptionActivated.equals("true")) {
                String filePath = this.getFilesDir().getPath() + "/" + fileName + "." + FILE_EXTENSION;
                crypto = new Crypto();
                //TODO pick password
                crypto.execute("encrypt", filePath, password, noteContentText.getText().toString());
            } else {
                OutputStream outputStream = openFileOutput(fileName + "." + FILE_EXTENSION, 0);
                OutputStreamWriter osw = new OutputStreamWriter(outputStream);
                osw.write(noteContentText.getText().toString());
                osw.close();
            }
        } catch (Throwable t) {
            Log.d("Exc in create_note", t.toString());
//            Toast.makeText(getApplicationContext(),
//                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void openNote(String fileName, String isEncryptionActivated) {
        myNote = null;
        try {
            if (isEncryptionActivated.equals("true")) {
                String filePath = this.getFilesDir().getPath() + "/" + fileName + "." + FILE_EXTENSION;
                //TODO get password from alert dialog
                crypto = new Crypto();
                crypto.execute("decrypt", filePath, password, null);
                String text = crypto.get();
                noteContentText.setText(text);
            } else {
                myNote = new File(this.getFilesDir().getPath() + "/" + fileName + "." + FILE_EXTENSION);

                InputStream inputStream = openFileInput(myNote.getName()); // replace with myNote
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
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            Log.d("Exc in edit_note", t.toString());
        }
    }

    public void onBackPressed() {
        if (!noteContentText.getText().toString().isEmpty()) {
            saveNote(fileName);
        }
        super.onBackPressed();
    }
}
