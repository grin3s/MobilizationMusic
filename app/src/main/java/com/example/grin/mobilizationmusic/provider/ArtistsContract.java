package com.example.grin.mobilizationmusic.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by grin on 4/19/16.
 */
public class ArtistsContract {
    private ArtistsContract() {}

    // Content provider authority.
    public static final String CONTENT_AUTHORITY = "com.example.grin.mobilizationmusic.provider";

    // base uri
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // path components for resources
    public static final String PATH_ARTISTS = "artists";
    private static final String PATH_GENRES = "genres";
    private static final String PATH_GENRES_TO_ARTISTS = "genres_to_artists";

    // Artists table
    public static class Artist implements BaseColumns {
        // resource uri
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTISTS).build();

        public static final String TABLE_NAME = "artists";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTISTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTISTS;

        // columns
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SMALL_COVER = "small_cover";
        public static final String COLUMN_NAME_LARGE_COVER = "large_cover";
        public static final String COLUMN_NAME_TRACKS = "tracks";
        public static final String COLUMN_NAME_ALBUMS = "albums";
        public static final String COLUMN_NAME_GENRES = "genres";
        public static final String COLUMN_NAME_DESCRITION = "description";

        public static Uri buildArtistById(int artist_id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(artist_id)).build();
        }
    }
}
