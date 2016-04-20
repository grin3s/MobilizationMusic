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
    private static final String PATH_ARTISTS = "artists";

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
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.example.mobilizationmusic.provider.artists";

        /**
         * Name of the artist
         */
        public static final String COLUMN_NAME_NAME = "name";
        public static final int COLUMN_ID_NAME = 0;
    }
}
