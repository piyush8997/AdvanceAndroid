package com.piyush.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {

    FloatingActionButton btnSaveNote, btnDeleteNote, btnClickImage;
    EditText edNoteTitle, edNoteText;
    Bundle mBundle;
    DBHelper helper;

    private static final int REQ_CODE_IMAGE_CAPTURE = 1111;
    private static final String AUTHORITY = "com.piyush.todoapp.fileprovider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        getSupportActionBar().hide();

        btnSaveNote = findViewById(R.id.btn_save_note);
        edNoteTitle = findViewById(R.id.ed_note_title);
        edNoteText = findViewById(R.id.ed_note_text);
        btnDeleteNote = findViewById(R.id.btn_delete_note);
        btnClickImage = findViewById(R.id.btn_click_img);

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

        btnClickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getPackageManager()) == null){
                    Toast.makeText(AddNoteActivity.this, "No camera app found.", Toast.LENGTH_SHORT).show();
                    return;
                }
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                String fileName = String.format("PIC_%s_",new SimpleDateFormat("yyyyMMDD_HHmmss",Locale.US).format(new Date()));
                try{
                    File file = File.createTempFile(fileName,".jpg",storageDir);
                    Uri uri = FileProvider.getUriForFile(v.getContext(),AUTHORITY,file);
                    i.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                }catch (IOException e){
                    e.printStackTrace();
                }
                startActivityForResult(i, REQ_CODE_IMAGE_CAPTURE);
            }
        });
    }
}
