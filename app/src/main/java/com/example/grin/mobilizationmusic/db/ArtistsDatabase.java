package com.example.grin.mobilizationmusic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;


/**
* Created by grin3s on 22.07.16.
*/



public class ArtistsDatabase extends SQLiteOpenHelper {
    private static ArtistsDatabase instance = null;

    private SQLiteQueryBuilder mQueryBuilder;

    public static synchronized ArtistsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new ArtistsDatabase(context);
        }
        return instance;
    }

    // Artists table
    public static class Artist implements BaseColumns {

        public static final String TABLE_NAME = "artists";


        // columns
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SMALL_COVER = "small_cover";
        public static final String COLUMN_NAME_LARGE_COVER = "large_cover";
        public static final String COLUMN_NAME_TRACKS = "tracks";
        public static final String COLUMN_NAME_ALBUMS = "albums";
        public static final String COLUMN_NAME_GENRES = "genres";
        public static final String COLUMN_NAME_DESCRITION = "description";

    }

    /** Schema version. */
    public static final int DATABASE_VERSION = 1;
    /** Filename for SQLite file. */
    public static final String DATABASE_NAME = "artists.db";

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String UNIQUE_KEY = " UNIQUE";
    private static final String COMMA_SEP = ",";
    /** SQL statement to create "entry" table. */
    private static final String SQL_CREATE_ARTISTS =
            "CREATE TABLE " + Artist.TABLE_NAME + " (" +
                    Artist._ID + " INTEGER PRIMARY KEY," +
                    Artist.COLUMN_NAME_NAME + TYPE_TEXT + UNIQUE_KEY + COMMA_SEP +
                    Artist.COLUMN_NAME_SMALL_COVER + TYPE_TEXT + COMMA_SEP +
                    Artist.COLUMN_NAME_LARGE_COVER + TYPE_TEXT + COMMA_SEP +
                    Artist.COLUMN_NAME_TRACKS + TYPE_INTEGER + COMMA_SEP +
                    Artist.COLUMN_NAME_ALBUMS + TYPE_INTEGER + COMMA_SEP +
                    Artist.COLUMN_NAME_GENRES + TYPE_TEXT + COMMA_SEP +
                    Artist.COLUMN_NAME_DESCRITION + TYPE_TEXT + ")";

    /** SQL statement to drop "entry" table. */
    private static final String SQL_DELETE_ARTISTS =
            "DROP TABLE IF EXISTS " + Artist.TABLE_NAME;

    public ArtistsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mQueryBuilder = new SQLiteQueryBuilder();
        mQueryBuilder.setTables(Artist.TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ARTISTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ARTISTS);
        onCreate(db);
    }

    // costructs the query that returns all artists
    public Cursor getAllArtists() {
        SQLiteDatabase db = getReadableDatabase();
        return mQueryBuilder.query(
                db,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getArtistById(int artist_id) {
        SQLiteDatabase db = getReadableDatabase();
        return mQueryBuilder.query(
                db,
                null,
                Artist._ID + " = ? ",
                new String[]{Integer.toString(artist_id)},
                null,
                null,
                null
        );
    }

    public void insertArtists(ContentValues[] values) {
        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                // if we already have the artist with the same name, we ovewrite this entry
                long _id = db.insertWithOnConflict(ArtistsDatabase.Artist.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}

