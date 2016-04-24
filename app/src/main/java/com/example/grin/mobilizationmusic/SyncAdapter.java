package com.example.grin.mobilizationmusic;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.JsonReader;
import android.util.Log;

import com.example.grin.mobilizationmusic.provider.ArtistsContract;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    private static final String SERVER_ENDPOINT_URL = "http://download.cdn.yandex.net/mobilization-2016/artists.json";

    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;

    private class ArtistData {
        String name;
        String small_cover;
        String large_cover;
        int tracks;
        int albums;
        String genres;
        String description;

        public ArtistData(String in_name,
                          String in_small_cover,
                          String in_large_cover,
                          int in_tracks,
                          int in_albums,
                          String in_genres,
                          String in_description) {
            name = in_name;
            small_cover = in_small_cover;
            large_cover = in_large_cover;
            tracks = in_tracks;
            albums = in_albums;
            genres = in_genres;
            description = in_description;
        }
    }


    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    private ArrayList<ArtistData> readJSONFromStream(InputStream stream) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(stream));
        ArrayList<ArtistData> artists = new ArrayList<ArtistData>();
        int n_entries = 0;
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                String name = null;
                String small_cover = null;
                String large_cover = null;
                int tracks = 0;
                int albums = 0;
                String genres = null;
                String description = null;
                reader.beginObject();
                while (reader.hasNext()) {
                    String key = reader.nextName();
                    if (key.equals("name")) {
                        name = reader.nextString();
                        Log.i(TAG + ":name", name);
                    }
                    else if (key.equals("cover")){
                        reader.beginObject();
                        reader.nextName();
                        small_cover = reader.nextString();
                        Log.i(TAG + ":small_cover", small_cover);
                        reader.nextName();
                        large_cover = reader.nextString();
                        Log.i(TAG + ":large_cover", large_cover);
                        reader.endObject();
                    }
                    else if (key.equals("tracks")) {
                        tracks = reader.nextInt();
                    }
                    else if (key.equals("albums")) {
                        albums = reader.nextInt();
                    }
                    else if (key.equals("genres")) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            String genre = reader.nextString();
                            if (genres != null) {
                                genres += ", ";
                            }
                            else {
                                genres = "";
                            }
                            genres += genre;
                        }
                        reader.endArray();
                    }
                    else if (key.equals("description")) {
                        description = reader.nextString();
                    }
                    else {
                        reader.skipValue();
                    }
                }
                artists.add(new ArtistData(name, small_cover, large_cover, tracks, albums, genres, description));
                reader.endObject();
            }
        }
        finally {
            reader.close();
            Log.i(TAG + ":entries", Integer.toString(n_entries));
        }
        return artists;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");
        try {
            final URL location = new URL(SERVER_ENDPOINT_URL);
            InputStream stream = null;

            try {
                Log.i(TAG, "Streaming data from network: " + location);
                // creating a stream to read json
                stream = downloadUrl(location);
                // reading an ArrayList of ArtistData for the received json
                ArrayList<ArtistData> artists = readJSONFromStream(stream);
                ContentValues[] cvArray = new ContentValues[artists.size()];
                Log.i(TAG, Integer.toString(artists.size()));
                for (int i = 0; i < artists.size(); i++) {
                    cvArray[i] = new ContentValues();
                    cvArray[i].put(ArtistsContract.Artist.COLUMN_NAME_NAME, artists.get(i).name);
                    cvArray[i].put(ArtistsContract.Artist.COLUMN_NAME_SMALL_COVER, artists.get(i).small_cover);
                    cvArray[i].put(ArtistsContract.Artist.COLUMN_NAME_LARGE_COVER, artists.get(i).large_cover);
                    cvArray[i].put(ArtistsContract.Artist.COLUMN_NAME_TRACKS, artists.get(i).tracks);
                    cvArray[i].put(ArtistsContract.Artist.COLUMN_NAME_ALBUMS, artists.get(i).albums);
                    cvArray[i].put(ArtistsContract.Artist.COLUMN_NAME_GENRES, artists.get(i).genres);
                    cvArray[i].put(ArtistsContract.Artist.COLUMN_NAME_DESCRITION, artists.get(i).description);
                }
                getContext().getContentResolver().bulkInsert(ArtistsContract.Artist.CONTENT_URI, cvArray);
            } finally {
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        }
        Log.i(TAG, "Network synchronization complete");
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets an input stream.
     */
    private InputStream downloadUrl(final URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
