package com.example.grin.mobilizationmusic.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by grin on 4/19/16.
 */
/*
 * Define an implementation of ContentProvider that stubs out
 * all methods
 */
public class ArtistsProvider extends ContentProvider {
    public final static String TAG = "ArtistsProvider";
    ArtistsDatabase mDatabaseHelper;
    private static final SQLiteQueryBuilder sQueryBuilder;

    static {
        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(ArtistsContract.Artist.TABLE_NAME);
    }
    /**
     * URI ID for route: /artists
     */
    public static final int ROUTE_ARTISTS = 1;
    public static final int ROUTE_ARTIST_BY_ID = 2;

    private static final String sArtistByIdSelection = ArtistsContract.Artist._ID + " = ? ";

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ArtistsContract.CONTENT_AUTHORITY, ArtistsContract.PATH_ARTISTS, ROUTE_ARTISTS);
        sUriMatcher.addURI(ArtistsContract.CONTENT_AUTHORITY, ArtistsContract.PATH_ARTISTS + "/*", ROUTE_ARTIST_BY_ID);
    }

    /*
     * Always return true, indicating that the
     * provider loaded correctly.
     */
    @Override
    public boolean onCreate() {
        mDatabaseHelper = new ArtistsDatabase(getContext());
        return true;
    }
    /*
     * Return no type for MIME type
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ARTISTS:
                return ArtistsContract.Artist.CONTENT_TYPE;
            case ROUTE_ARTIST_BY_ID:
                return ArtistsContract.Artist.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Cursor getAllArtists(String[] projection, String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        return sQueryBuilder.query(
                db,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getArtistById(int artist_id, String[] projection, String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        return sQueryBuilder.query(
                db,
                projection,
                sArtistByIdSelection,
                new String[]{Integer.toString(artist_id)},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        Log.i(TAG, "performing query for " + uri.toString());
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        int uriMatch = sUriMatcher.match(uri);
        Context ctx = getContext();
        assert ctx != null;
        Cursor c;
        switch (uriMatch) {
            case ROUTE_ARTISTS:
                Log.i(TAG, "all artists");
                // Return all known artists.
                c = getAllArtists(projection, sortOrder);
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            case ROUTE_ARTIST_BY_ID:
                int artist_id = Integer.valueOf(uri.getPathSegments().get(1));
                Log.i(TAG, "artist by id " + Integer.toString(artist_id));
                c = getArtistById(artist_id, projection, sortOrder);
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    /*
     * insert() always returns null (no URI)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }
    /*
     * delete() always returns "no rows affected" (0)
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
    /*
     * update() always returns "no rows affected" (0)
     */
    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Log.i(TAG, "performing bulk insert");
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ARTISTS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(ArtistsContract.Artist.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    static class ArtistsDatabase extends SQLiteOpenHelper {
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
                "CREATE TABLE " + ArtistsContract.Artist.TABLE_NAME + " (" +
                        ArtistsContract.Artist._ID + " INTEGER PRIMARY KEY," +
                        ArtistsContract.Artist.COLUMN_NAME_NAME + TYPE_TEXT + UNIQUE_KEY + COMMA_SEP +
                        ArtistsContract.Artist.COLUMN_NAME_SMALL_COVER + TYPE_TEXT + COMMA_SEP +
                        ArtistsContract.Artist.COLUMN_NAME_LARGE_COVER + TYPE_TEXT + COMMA_SEP +
                        ArtistsContract.Artist.COLUMN_NAME_TRACKS + TYPE_INTEGER + COMMA_SEP +
                        ArtistsContract.Artist.COLUMN_NAME_ALBUMS + TYPE_INTEGER + COMMA_SEP +
                        ArtistsContract.Artist.COLUMN_NAME_GENRES + TYPE_TEXT + ")";

        private static final String SQL_CREATE_GENRES =
                "CREATE TABLE" + ArtistsContract.Genre.TABLE_NAME + " (" +
                        ArtistsContract.Genre._ID + " INTEGER PRIMARY KEY," +
                        ArtistsContract.Genre.COLUMN_NAME_NAME + UNIQUE_KEY + TYPE_TEXT + ")";

        private static final String SQL_CREATE_GENRES_TO_ARTISTS =
                "CREATE TABLE" + ArtistsContract.GenresToArtists.TABLE_NAME + " (" +
                        ArtistsContract.GenresToArtists._ID + " INTEGER PRIMARY KEY," +
                        ArtistsContract.GenresToArtists.COLUMN_NAME_GENRE_ID + TYPE_INTEGER + COMMA_SEP +
                        ArtistsContract.GenresToArtists.COLUMN_NAME_ARTIST_ID + TYPE_INTEGER + COMMA_SEP +
                        "UNIQUE (" + ArtistsContract.GenresToArtists.COLUMN_NAME_GENRE_ID + "," + ArtistsContract.GenresToArtists.COLUMN_NAME_ARTIST_ID + ")" + ")";


        /** SQL statement to drop "entry" table. */
        private static final String SQL_DELETE_ARTISTS =
                "DROP TABLE IF EXISTS " + ArtistsContract.Artist.TABLE_NAME;

        private static final String SQL_DELETE_GENRES =
                "DROP TABLE IF EXISTS " + ArtistsContract.Genre.TABLE_NAME;

        private static final String SQL_DELETE_GENRES_TO_ARTISTS =
                "DROP TABLE IF EXISTS " + ArtistsContract.GenresToArtists.TABLE_NAME;

        public ArtistsDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ARTISTS);
//            db.execSQL(SQL_CREATE_GENRES);
//            db.execSQL(SQL_CREATE_GENRES_TO_ARTISTS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ARTISTS);
//            db.execSQL(SQL_DELETE_GENRES);
//            db.execSQL(SQL_DELETE_GENRES_TO_ARTISTS);
            onCreate(db);
        }
    }
}
