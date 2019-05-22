package com.piyush.todoapp;

import android.net.Uri;

public class Statics {
    public static final String DB_NAME = "NotesDB";
    public static final String TABLE_NAME = "Notes";
    public static final int DB_VERSION = 1;
    public static final String COL_NOTE_ID = "note_id";
    public static final String COL_NOTE_TITLE = "note_title";
    public static final String COL_NOTE_TEXT = "note_text";
    public static final String AUTHORITY = "com.piyush.todoapp";
    public static final String BASE_URI = "Notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_URI);
}
