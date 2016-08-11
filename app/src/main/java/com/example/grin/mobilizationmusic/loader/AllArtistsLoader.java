package com.example.grin.mobilizationmusic.loader;

import android.support.v4.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.JsonReader;
import android.util.Log;

import com.example.grin.mobilizationmusic.db.ArtistsDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by grin3s on 22.07.16.
 */
public class AllArtistsLoader extends AsyncTaskLoader<Cursor> {

    private static final String SERVER_ENDPOINT_URL = "http://download.cdn.yandex.net/mobilization-2016/artists.json";
    private static String TAG = "MobilizationMusic:AllArtistsLoader";
    private ArtistsDatabase artistDb;

    private Cursor mData;
    private int artist_id;

    public AllArtistsLoader(Context context) {
        super(context);
        artistDb = ArtistsDatabase.getInstance(context.getApplicationContext());
    }

    // a class, where the artist's data is stored before passing it to the database
    private static class ArtistData {
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

    // parsing JSON from the server
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



    /****************************************************/
    /** (1) A task that performs the asynchronous load **/
    /****************************************************/

    @Override
    public Cursor loadInBackground() {
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

                // putting it to the db using content resolver
                ContentValues[] cvArray = new ContentValues[artists.size()];
                Log.i(TAG, Integer.toString(artists.size()));
                for (int i = 0; i < artists.size(); i++) {
                    cvArray[i] = new ContentValues();
                    cvArray[i].put(ArtistsDatabase.Artist.COLUMN_NAME_NAME, artists.get(i).name);
                    cvArray[i].put(ArtistsDatabase.Artist.COLUMN_NAME_SMALL_COVER, artists.get(i).small_cover);
                    cvArray[i].put(ArtistsDatabase.Artist.COLUMN_NAME_LARGE_COVER, artists.get(i).large_cover);
                    cvArray[i].put(ArtistsDatabase.Artist.COLUMN_NAME_TRACKS, artists.get(i).tracks);
                    cvArray[i].put(ArtistsDatabase.Artist.COLUMN_NAME_ALBUMS, artists.get(i).albums);
                    cvArray[i].put(ArtistsDatabase.Artist.COLUMN_NAME_GENRES, artists.get(i).genres);
                    cvArray[i].put(ArtistsDatabase.Artist.COLUMN_NAME_DESCRITION, artists.get(i).description);
                }
                artistDb.insertArtists(cvArray);
                Log.i(TAG, "Network synchronization complete");
            } finally {
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "URL is malformed", e);
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
        }

        return artistDb.getAllArtists();
    }

    /********************************************************/
    /** (2) Deliver the results to the registered listener **/
    /********************************************************/

    @Override
    public void deliverResult(Cursor data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        Cursor oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    /*********************************************************/
    /** (3) Implement the Loader’s state-dependent behavior **/
    /*********************************************************/

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

        // Begin monitoring the underlying data source.
//        if (mObserver == null) {
//            mObserver = new SampleObserver();
//            register observer
//        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.
//        if (mObserver != null) {
//            // TODO: unregister the observer
//            mObserver = null;
//        }
    }

    @Override
    public void onCanceled(Cursor data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    private void releaseResources(Cursor data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
        if (data != null) {
            data.close();
        }
    }


    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 1500;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

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