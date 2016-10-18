package com.developers.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by avispa on 17.10.2016.
 */

public class NoteAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    int count;

    public NoteAdapter(Context context) {
        //TODO constructor
        ctx = context;
        DBHelper dbHelper = new DBHelper(this);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("NOTES", null, null, null, null, null, null);
        this.count = c.getCount();
    }

    public int getCount() {
        return count;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }
        
        return view;
    }


}
