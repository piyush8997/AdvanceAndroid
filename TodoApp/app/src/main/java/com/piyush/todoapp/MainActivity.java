package com.piyush.todoapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btnAddNote;
    RecyclerView notesRecyclerView;
    FragmentManager fragmentManager;
    TextView tvNotice;

    NotesAdapter adapter;
    List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddNote = findViewById(R.id.btn_add_note);
        notesRecyclerView = findViewById(R.id.recycler_view);
        tvNotice = findViewById(R.id.tv_notice);

        fragmentManager = getSupportFragmentManager();

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        noteList = new ArrayList<>();
        adapter = new NotesAdapter(noteList);
        notesRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DBHelper helper = DBHelper.getDBInstance(MainActivity.this);
        List<Note> noteList = helper.getNotes();
        adapter.updateDataSet(noteList);

        if(noteList.size()>0){
            tvNotice.setVisibility(View.INVISIBLE);
        }else{
            tvNotice.setVisibility(View.VISIBLE);
        }

        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sync:
                // call method to sync data with cloud.
                break;
            case R.id.menu_exit: finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
