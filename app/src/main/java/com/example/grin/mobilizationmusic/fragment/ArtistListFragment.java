package com.example.grin.mobilizationmusic.fragment;

import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.grin.mobilizationmusic.ArtistListAdapter;
import com.example.grin.mobilizationmusic.R;
import com.example.grin.mobilizationmusic.loader.AllArtistsLoader;

/**
 * Created by grin3s on 20.07.16.
 */
public class ArtistListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    ArtistListAdapter mAdapter;
    ListView mListView;

    private static Parcelable mListViewState = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //list view holding all artists
        View rootView = inflater.inflate(R.layout.artist_list, container, false);
        mListView = (ListView) rootView.findViewById(R.id.artist_list);

        //creating the adapter for the list of artists
        mAdapter = new ArtistListAdapter(getContext(), null, 0);

        mListView.setAdapter(mAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
//                // building URI which can be used to fetch the current artist's info, which is then passed to the detail fragment
//                Uri contentUri = ArtistsContract.Artist.buildArtistById(cursor.getInt(ArtistListAdapter.COLUMN_ID));
//                if (mTwoPane) {
//                    // In two-pane mode, show the detail view in this activity by
//                    // adding or replacing the detail fragment using a
//                    // fragment transaction.
//                    Bundle args = new Bundle();
//                    args.putParcelable(ArtistDetailFragment.DETAIL_URI, contentUri);
//
//                    ArtistDetailFragment fragment = new ArtistDetailFragment();
//                    fragment.setArguments(args);
//
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.artist_detail_container, fragment, DETAILFRAGMENT_TAG)
//                            .commit();
//                } else {
//                    // in a small screen we start a new activity with an Intent
//                    Intent intent = new Intent(mContext, ArtistDetailActivity.class)
//                            .setData(contentUri);
//                    startActivity(intent);
//                }
//            }
//        });
        getLoaderManager().initLoader(0, null, this);
        return rootView;
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
