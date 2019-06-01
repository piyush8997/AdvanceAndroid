package com.piyush.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class AddNoteActivity extends AppCompatActivity {

    FloatingActionButton btnSaveNote, btnDeleteNote;
    EditText edNoteTitle, edNoteText;
    Bundle mBundle;
    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        getSupportActionBar().hide();

        btnSaveNote = findViewById(R.id.btn_save_note);
        edNoteTitle = findViewById(R.id.ed_note_title);
        edNoteText = findViewById(R.id.ed_note_text);
        btnDeleteNote = findViewById(R.id.btn_delete_note);

        Intent i = getIntent();
        mBundle = i.getExtras();
    }

    @Override
    protected void onResume() {
        super.onResume();

        helper = DBHelper.getDBInstance(AddNoteActivity.this);

        if (mBundle != null) { // if activity launched from recycler-view adapter
            edNoteTitle.setText(mBundle.getString("note_title"));
            edNoteText.setText(mBundle.getString("note_text"));

            btnDeleteNote.show();
            btnDeleteNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String id = String.valueOf(mBundle.getInt("note_id"));

                    //Alert Dialog for final confirmation.
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to delete the note?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    long n = helper.deleteNote(id);
                                    if (n > 0) {
                                        Toast.makeText(AddNoteActivity.this,
                                                "Note deleted successfully",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AddNoteActivity.this,
                                                "Something went wrong",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setCancelable(false);
                    builder.show();
                }
            });
        } else {
            btnDeleteNote.hide();
        }

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edNoteTitle.getText().toString();
                String text = edNoteText.getText().toString();

                if (text.isEmpty()) {
                    Toast.makeText(AddNoteActivity.this,
                            "Please enter something in the note",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                long n;
                if (mBundle != null) {
                    n = helper.updateNote(String.valueOf(mBundle.getInt("note_id")), title, text);
                } else {
                    n = helper.addNote(title, text);
                }

                if (n > 0) {
                    Toast.makeText(AddNoteActivity.this,
                            "Note saved",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddNoteActivity.this,
                            "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}
