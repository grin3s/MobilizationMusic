package com.example.grin.mobilizationmusic;

import android.app.Activity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.grin.mobilizationmusic.dummy.DummyContent;
import com.example.grin.mobilizationmusic.provider.ArtistsContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Artist detail screen.
 * This fragment is either contained in a {@link ArtistListActivity}
 * in two-pane mode (on tablets) or a {@link ArtistDetailActivity}
 * on handsets.
 */
public class ArtistDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // tag for logging functions
    private static String TAG = "ArtistDetailFragment";
    // the key to extract from arguments containing the uri to fetch from the content provider
    public static String DETAIL_URI = "URI";
    // this uri
    private Uri mUri;

    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private TextView mGenresView;
    private TextView mAlbumsTracksView;
    private TextView mBiographyView;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "creating detail fragment");
        View rootView = inflater.inflate(R.layout.artist_detail, container, false);

        // fetching content uri from the arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        // getting all views
        mImageView = (ImageView) rootView.findViewById(R.id.detail_image_view);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.detail_image_progress_bar);
        mGenresView = (TextView) rootView.findViewById(R.id.detail_artist_genres);
        mAlbumsTracksView = (TextView) rootView.findViewById(R.id.detail_artist_albumns_tracks);
        mBiographyView = (TextView) rootView.findViewById(R.id.detail_artist_biography);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // initializing the loader, that fetches mUri
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // loading the big image, we resize it, because the big one looks ugly on small screens
            // TODO: do something with the image size...
            Picasso.with(getContext()).load(data.getString(ArtistListAdapter.COLUMN_LARGE_COVER)).resize(600, 600).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    // when the image is loaded, we can hide the circle progress bar
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    Log.e(TAG, "Error loading the image");
                }
            });
            // populating other views
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(data.getString(ArtistListAdapter.COLUMN_NAME));
            mGenresView.setText(data.getString(ArtistListAdapter.COLUMN_GENRES));
            mAlbumsTracksView.setText(String.format(ArtistListAdapter.sAlbumsTracksTemplate, data.getInt(ArtistListAdapter.COLUMN_ALBUMS), ArtistListAdapter.COLUMN_TRACKS));
            mBiographyView.setText(data.getString(ArtistListAdapter.COLUMN_DESCRIPTION));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}