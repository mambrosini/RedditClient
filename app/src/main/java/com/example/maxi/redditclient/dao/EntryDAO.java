package com.example.maxi.redditclient.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.maxi.redditclient.helpers.SQLiteHelper;
import com.example.maxi.redditclient.model.Entry;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mambrosini on 5/15/15.
 */
public class EntryDAO {

        private SQLiteDatabase database;
        private SQLiteHelper dbHelper;
        private String[] allColumns = { SQLiteHelper.DBEntries.ID,
                SQLiteHelper.DBEntries.COLUMN_ENTRY_TITLE,
                SQLiteHelper.DBEntries.COLUMN_ENTRY_AUTHOR,
                SQLiteHelper.DBEntries.COLUMN_ENTRY_DATE,
                SQLiteHelper.DBEntries.COLUMN_ENTRY_THUMBNAIL_URL,
                SQLiteHelper.DBEntries.COLUMN_ENTRY_IMAGE_URL,
                SQLiteHelper.DBEntries.COLUMN_ENTRY_COMMENTS,
                SQLiteHelper.DBEntries.COLUMN_ENTRY_URL};

        public EntryDAO(Context context) {
            dbHelper = new SQLiteHelper(context);
        }

        public void open() throws SQLException {
            database = dbHelper.getWritableDatabase();
        }

        public void close() {
            dbHelper.close();
        }

        public Entry createEntry(Entry entry) {
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.DBEntries.COLUMN_ENTRY_TITLE, entry.getTitle());
            values.put(SQLiteHelper.DBEntries.COLUMN_ENTRY_AUTHOR, entry.getAuthor());
            values.put(SQLiteHelper.DBEntries.COLUMN_ENTRY_DATE, String.valueOf(entry.getDate().getTime()));
            values.put(SQLiteHelper.DBEntries.COLUMN_ENTRY_THUMBNAIL_URL, entry.getThumbUrl());
            values.put(SQLiteHelper.DBEntries.COLUMN_ENTRY_IMAGE_URL, entry.getImageUrl());
            values.put(SQLiteHelper.DBEntries.COLUMN_ENTRY_COMMENTS, entry.getNumberOfComments());
            values.put(SQLiteHelper.DBEntries.COLUMN_ENTRY_URL, entry.getUrl());

            long insertId = database.insert(SQLiteHelper.DBEntries.TABLE_NAME, null,
                    values);
            Cursor cursor = database.query(SQLiteHelper.DBEntries.TABLE_NAME,
                    allColumns, SQLiteHelper.DBEntries.ID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            Entry newEntry = cursorToEntry(cursor);
            cursor.close();

            System.out.println("Entry created with id: " + newEntry.getId());

            return newEntry;
        }

        public void deleteAllEntries(ArrayList<Entry> entries) {
            for (Entry entry : entries) {
                long id = entry.getId();
                System.out.println("Entry deleted with id: " + id);
                database.delete(SQLiteHelper.DBEntries.TABLE_NAME, SQLiteHelper.DBEntries.ID
                        + " = " + id, null);
            }
        }

        private Entry cursorToEntry(Cursor cursor) {
            Entry entry = new Entry();
            entry.setId(cursor.getLong(0));
            entry.setTitle(cursor.getString(1));
            entry.setAuthor(cursor.getString(2));
            Date time = new java.util.Date(Double.valueOf(cursor.getString(3)).longValue());
            entry.setDate(time);
            entry.setThumbUrl(cursor.getString(4));
            entry.setImageUrl(cursor.getString(5));
            entry.setNumberOfComments(cursor.getInt(6));
            entry.setUrl(cursor.getString(7));
            return entry;
        }

        public ArrayList<Entry> getAllEntries() {
            ArrayList<Entry> entries = new ArrayList<>();

            Cursor cursor = database.query(SQLiteHelper.DBEntries.TABLE_NAME,
                    allColumns, null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Entry entry = cursorToEntry(cursor);
                entries.add(entry);
                cursor.moveToNext();
            }
            cursor.close();
            return entries;
        }


        public Cursor getEntriesCursor() {
            Cursor cursor = database.rawQuery("SELECT  * FROM " + SQLiteHelper.DBEntries.TABLE_NAME, null);
            cursor.moveToFirst();
            return cursor;
        }
}
