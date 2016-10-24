package com.developers.notes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static com.developers.notes.DBHelper.TABLE_NAME;

public class MainActivity extends AppCompatActivity {

    private static final int DELETE_NOTE_MENU_ITEM = 1;
    FloatingActionButton fab;
    ListView noteList;
    NoteAdapter noteAdapter;
    Cursor cursor;
    DBHelper dbHelper;
    SQLiteDatabase notesDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        noteList = (ListView) findViewById(R.id.noteList);
        dbHelper = new DBHelper(this);
        notesDB = dbHelper.getReadableDatabase();
        cursor = notesDB.query(TABLE_NAME, null, null, null, null, null, null);
        noteAdapter = new NoteAdapter(MainActivity.this, cursor, 0);
        noteList.setAdapter(noteAdapter);
        registerForContextMenu(noteList);
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor = notesDB.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
                TextView itemFileNameText = (TextView) findViewById(R.id.itemFileNameText);
                String fileName = itemFileNameText.getText().toString();
                cursor = notesDB.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE "  + DBHelper.FILE_NAME_COLUMN + " = '" + fileName + "'", null);
                cursor.moveToFirst();
                Log.d("CURSOR", cursor.getColumnIndex(DBHelper.NOTE_NAME_COLUMN) + " #");
                String noteName = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_NAME_COLUMN));
                Intent editNote = new Intent(MainActivity.this, EditNoteActivity.class);
                editNote.putExtra("filename", fileName);
                editNote.putExtra("notename", noteName);
                startActivity(editNote);
            }
        });

        View.OnClickListener fabOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createNote = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivity(createNote);
            }
        };
        fab.setOnClickListener(fabOnClick);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add(0, v.getId(), 0, "Delete");
    }
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete") {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int pos = acmi.position;
            cursor = notesDB.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
            //TODO alertdialog
            cursor.moveToPosition(pos);
            String fileName = cursor.getString(cursor.getColumnIndex(DBHelper.FILE_NAME_COLUMN));
            String path = this.getFilesDir().getPath() + "/" + fileName + "." + CreateNoteActivity.FILE_EXTENSION;
            notesDB.execSQL("DELETE FROM " + DBHelper.TABLE_NAME + " WHERE " + DBHelper.FILE_NAME_COLUMN
                    + " = '" + fileName + "'");
            File myNote = new File(path);
            myNote.delete();
            updateListView();
            Toast.makeText(getApplicationContext(), "Note was deleted", Toast.LENGTH_LONG).show();
        }
        return true;
    }
    private void updateListView() {
        cursor = notesDB.query(TABLE_NAME, null, null, null, null, null, null);
        noteAdapter = new NoteAdapter(MainActivity.this, cursor, 0);
        noteAdapter.notifyDataSetChanged();
        noteList.setAdapter(noteAdapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    protected void onRestart() {
        super.onRestart();
        updateListView();
    }

    protected void onStart() {
        super.onStart();
        updateListView();
    }

    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
            super.onBackPressed();
//        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
////        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
////        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}
