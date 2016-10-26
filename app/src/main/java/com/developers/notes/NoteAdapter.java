package com.developers.notes;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.developers.notes.CreateNoteActivity.FILE_EXTENSION;

/**
 * Created by avispa on 17.10.2016.
 */

public class NoteAdapter extends CursorAdapter {
    private Context ctx;
    private LayoutInflater lInflater;
    private final String LOG_TAG = "myLogs";

    public NoteAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        Log.d(LOG_TAG, "note adapter created");
        lInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        Log.d(LOG_TAG, "inflater created");
    }

    public void bindView(View view, Context context, Cursor cursor) {
        TextView itemNoteName = (TextView) view.findViewById(R.id.itemNoteName);
        TextView itemNoteContent = (TextView) view.findViewById(R.id.itemNoteContent);
        TextView itemFileNameText = (TextView) view.findViewById(R.id.itemFileNameText);
        String noteName = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_NAME_COLUMN));
        String fileName = cursor.getString(cursor.getColumnIndex(DBHelper.FILE_NAME_COLUMN));
        String isEncryptionActivated = cursor.getString(cursor.getColumnIndex(DBHelper.ENCRYPTION_COLUMN));
        if (!isEncryptionActivated.equals("true")) {
            try {
                openNote(fileName, context, itemNoteContent);
            } catch (Exception e) {}
        } else {
            itemNoteContent.setHint("<<< Encrypted >>> \n");
            itemNoteContent.setTypeface(itemNoteContent.getTypeface(), Typeface.ITALIC);
        }
        itemNoteName.setText(noteName);
        itemFileNameText.setText(fileName);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return lInflater.inflate(R.layout.item, parent, false);
    }


    private void openNote(String fileName,  Context context, TextView itemNoteContent) {
        try {
            InputStream inputStream = context.openFileInput(fileName + "." + FILE_EXTENSION);
            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
//                builder.append("\b");
                inputStream.close();
                itemNoteContent.setText(builder.toString() + "\n");
            }
        }
        catch(Exception e) {}
    }
}
