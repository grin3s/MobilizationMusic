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
    private static String TAG = "ArtistDetailFragment";
    public static String DETAIL_URI = "URI";
    private Uri mUri;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private TextView mGenresView;
    private TextView mAlbumsTracksView;
    private TextView mBiographyView;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "creating detail fragment");
        View rootView = inflater.inflate(R.layout.artist_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
        }
        mImageView = (ImageView) rootView.findViewById(R.id.detail_image_view);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.detail_image_progress_bar);
        mGenresView = (TextView) rootView.findViewById(R.id.detail_artist_genres);
        mAlbumsTracksView = (TextView) rootView.findViewById(R.id.detail_artist_albumns_tracks);
        mBiographyView = (TextView) rootView.findViewById(R.id.detail_artist_biography);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
            Picasso.with(getContext()).load(data.getString(ArtistListAdapter.COLUMN_LARGE_COVER)).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    Log.e(TAG, "Error loading the image");
                }
            });
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