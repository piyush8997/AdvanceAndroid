package com.piyush.todoapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class AddNoteActivity extends AppCompatActivity {

    FloatingActionButton btnSaveNote;
    EditText edNoteTitle, edNoteText;
    Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        getSupportActionBar().hide();

        btnSaveNote = findViewById(R.id.btn_save_note);
        edNoteTitle = findViewById(R.id.ed_note_title);
        edNoteText = findViewById(R.id.ed_note_text);

        Intent i = getIntent();
        mBundle = i.getExtras();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mBundle == null){ // launched from add note button
            btnSaveNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = edNoteTitle.getText().toString();
                    String text = edNoteText.getText().toString();

                    DBHelper helper = DBHelper.getDBInstance(AddNoteActivity.this);
                    long n = helper.addNote(title,text);
                    if(n > 0) {
                        Toast.makeText(AddNoteActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(AddNoteActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                }
            });
        }else{ // launched from note itself for updating content
            edNoteTitle.setText(mBundle.getString("note_title"));
            edNoteText.setText(mBundle.getString("note_text"));
        }
    }
}
