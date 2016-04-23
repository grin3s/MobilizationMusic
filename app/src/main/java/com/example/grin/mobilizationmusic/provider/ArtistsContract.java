package com.example.grin.mobilizationmusic.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by grin on 4/19/16.
 */
public class ArtistsContract {
    private ArtistsContract() {}
    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "com.example.grin.mobilizationmusic.provider";

    /**
     * Base URI.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    public static final String PATH_ARTISTS = "artists";
    private static final String PATH_GENRES = "genres";
    private static final String PATH_GENRES_TO_ARTISTS = "genres_to_artists";

    /**
     * Columns supported by "entries" records.
     */
    public static class Artist implements BaseColumns {
        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTISTS).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "artists";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTISTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTISTS;

        /**
         * Name of the artist
         */
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SMALL_COVER = "small_cover";
        public static final String COLUMN_NAME_LARGE_COVER = "large_cover";
        public static final String COLUMN_NAME_TRACKS = "tracks";
        public static final String COLUMN_NAME_ALBUMS = "albums";
        public static final String COLUMN_NAME_GENRES = "genres";

        public static Uri buildArtistById(int artist_id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(artist_id)).build();
        }
    }

    public static class Genre implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRES).build();

        public static final String TABLE_NAME = "genres";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.example.mobilizationmusic.provider.genres";

        public static final String COLUMN_NAME_NAME = "name";
    }

    public static class GenresToArtists implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRES_TO_ARTISTS).build();

        public static final String TABLE_NAME = "genres_to_artists";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.example.mobilizationmusic.provider.genres_to_artists";

        public static final String COLUMN_NAME_GENRE_ID = "genre_id";
        public static final String COLUMN_NAME_ARTIST_ID = "artist_id";
    }
}
