package com.example.grin.mobilizationmusic.fragment;

import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.grin.mobilizationmusic.ArtistListAdapter;
import com.example.grin.mobilizationmusic.MainActivity;
import com.example.grin.mobilizationmusic.R;
import com.example.grin.mobilizationmusic.loader.AllArtistsLoader;

import java.lang.ref.WeakReference;

/**
 * Created by grin3s on 20.07.16.
 */
public class ArtistListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    ArtistListAdapter mAdapter;
    ListView mListView;

    private static Parcelable mListViewState = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        //list view holding all artists
        View rootView = inflater.inflate(R.layout.artist_list, container, false);
        mListView = (ListView) rootView.findViewById(R.id.artist_list);

        //creating the adapter for the list of artists
        mAdapter = new ArtistListAdapter(getContext(), null, 0);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new ArtistClickListener((MainActivity) getActivity()));
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    private static class ArtistClickListener implements AdapterView.OnItemClickListener {

        WeakReference<MainActivity> activityRef;

        ArtistClickListener(MainActivity mainActivity) {
            activityRef = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
            // building URI which can be used to fetch the current artist's info, which is then passed to the detail fragment
            MainActivity ref = activityRef.get();
            if (ref != null) {
                activityRef.get().launchDetails(cursor.getInt(ArtistListAdapter.COLUMN_ID));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mListViewState = mListView.onSaveInstanceState();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AllArtistsLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mListViewState != null) {
            mListView.onRestoreInstanceState(mListViewState);
            mListViewState = null;
        }
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
