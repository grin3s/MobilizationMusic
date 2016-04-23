package com.example.grin.mobilizationmusic;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.example.grin.mobilizationmusic.authentication.Authenticator;
import com.example.grin.mobilizationmusic.provider.ArtistsContract;

/**
 * An activity representing a list of Artists. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ArtistDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ArtistListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static String TAG = "ArtistListActivity";
    private static Parcelable mListViewState = null;
    private static final String SELECTED_KEY = "selected_position";

    // Column indexes. The index of a column in the Cursor is the same as its relative position in
    // the projection.
    /** Column index for _ID */



    /**
     * List of Cursor columns to read from when preparing an adapter to populate the ListView.
     */
    private static final String[] FROM_COLUMNS = new String[]{
            ArtistsContract.Artist.COLUMN_NAME_NAME,
            ArtistsContract.Artist.COLUMN_NAME_SMALL_COVER
    };

    /**
     * List of Views which will be populated by Cursor data.
     */
    private static final int[] TO_FIELDS = new int[]{
            R.id.list_artist_name,
            R.id.list_image_view
    };

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    Account mAccount;
    ArtistListAdapter mAdapter;
    Context mContext;
    ListView mListView;
    ProgressDialog mProgressDialog;
    // The authority for the sync adapter's content provider

    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_artist_list);

        if (findViewById(R.id.artist_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mAccount = CreateSyncAccount(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mListView = (ListView) findViewById(R.id.artist_list);
        mContext = this;

        mAdapter = new ArtistListAdapter(this, null, 0);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (mTwoPane) {
                    // In two-pane mode, show the detail view in this activity by
                    // adding or replacing the detail fragment using a
                    // fragment transaction.
                } else {
                    Intent intent = new Intent(mContext, ArtistDetailActivity.class)
                            .setData(ArtistsContract.Artist.buildArtistById(cursor.getInt(ArtistListAdapter.COLUMN_ID)));
                    startActivity(intent);
                }
            }
        });
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(getTitle());

    }

//    @Override
//    public void onPause() {
//        Log.d(TAG, "saving listview state @ onPause");
//        mListViewState = mListView.onSaveInstanceState();
//        super.onPause();
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // We only have one loader, so we can ignore the value of i.
        // (It'll be '0', as set in onCreate().)
        return new CursorLoader(this,  // Context
                ArtistsContract.Artist.CONTENT_URI, // URI
                null,                // Projection
                null,                           // Selection
                null,                           // Selection args
                null); // Sort
    }

    /**
     * Move the Cursor returned by the query into the ListView adapter. This refreshes the existing
     * UI with the data in the Cursor.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.i(TAG, "onLoadFinished");
//        mProgressDialog.dismiss();
        mAdapter.swapCursor(cursor);
        if(mListViewState != null) {
            Log.i(TAG, "trying to restore listview state..");
            mListView.onRestoreInstanceState(mListViewState);
        }
//        if (mPosition != ListView.INVALID_POSITION) {
//            Log.i(TAG, "changing position");
//            // If we don't need to restart the loader, and there's a desired position to restore
//            // to, do so now.
//            mListView.smoothScrollToPosition(mPosition);
//        }
    }

    /**
     * Called when the ContentObserver defined for the content provider detects that data has
     * changed. The ContentObserver resets the loader, and then re-runs the loader. In the adapter,
     * set the Cursor value to null. This removes the reference to the Cursor, allowing it to be
     * garbage-collected.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    public static Account CreateSyncAccount(Context context) {
        Log.i(TAG, "CreateSyncAccoun");
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = new Account(Authenticator.ACCOUNT, Authenticator.ACCOUNT_TYPE);
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, ArtistsContract.CONTENT_AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, ArtistsContract.CONTENT_AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
//            ContentResolver.addPeriodicSync(
//                    account, AUTHORITY, new Bundle(), SYNC_FREQUENCY);
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
        }
        //TriggerRefresh();
        return account;
    }

    public static void TriggerRefresh() {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                new Account(Authenticator.ACCOUNT, Authenticator.ACCOUNT_TYPE),
                ArtistsContract.CONTENT_AUTHORITY,
                b);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        mListViewState = mListView.onSaveInstanceState();
//        if (mPosition != ListView.INVALID_POSITION) {
//            outState.putInt(SELECTED_KEY, mPosition);
//            Log.i(TAG, "saving position position " + Integer.toString(mPosition));
//        }
        super.onSaveInstanceState(outState);
    }


}
