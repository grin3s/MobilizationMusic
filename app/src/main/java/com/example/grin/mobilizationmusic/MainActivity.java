package com.example.grin.mobilizationmusic;

import android.accounts.Account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;


import com.example.grin.mobilizationmusic.fragment.ArtistDetailFragment;
import com.example.grin.mobilizationmusic.fragment.ArtistListFragment;

/**
 * An activity representing a list of Artists. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ArtistDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MainActivity";
    private static Parcelable mListViewState = null;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private Object mSyncObserverHandle;
    private MusicIntentReceiver headsetReceiver;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    Account mAccount;
    ArtistListAdapter mAdapter;
    Context mContext;
    ListView mListView;
    ProgressBar mLoadingBar;

    //key in SharedPreferences, that knows if we have synced with the server
    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        setContentView(R.layout.main_layout);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, new ArtistListFragment()).commit();
        }

//        if (findViewById(R.id.artist_detail_container) != null) {
//            // The detail container view will be present only in the
//            // large-screen layouts (res/values-w900dp).
//            // If this view is present, then the
//            // activity should be in two-pane mode.
//            mTwoPane = true;
//        }
//
//        //adding the toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        headsetReceiver = new MusicIntentReceiver();
//
//        //circle progress bar that rotates while sync adapter is fetching data
//        mLoadingBar = (ProgressBar) findViewById(R.id.loading_bar);
//
//        //creating an account that is needed for sync adapter
//        mAccount = CreateSyncAccount(this);
//
//        //list view holding all artists
//        mListView = (ListView) findViewById(R.id.artist_list);
//
//        //context variable for later usage
//        mContext = this;
//
//        //creating the adapter for the list of artists
//        mAdapter = new ArtistListAdapter(this, null, 0);
//
//        mListView.setAdapter(mAdapter);
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
//        // initializing the loader that fetches artists' info from the content provider in another thread
//        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onResume() {
        super.onResume();
//        // setting the title of an action bar
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetReceiver, filter);
//
//        // creating an observer that looks at the status of the sync adapter. We use it to hide mLoadingBar later
//        mSyncStatusObserver.onStatusChanged(0);
//
//        // Watch for sync state changes
//        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
//                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
//        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(headsetReceiver);
//        // have to do this
//        if (mSyncObserverHandle != null) {
//            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
//            mSyncObserverHandle = null;
//        }
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        // fetching artists' data using cursor loader
//        // even if there is no network connection, we still have all data cached in the database
//        // images themselves are cached by Picasso (if you loaded them)
//        return new CursorLoader(this,  // Context
//                ArtistsContract.Artist.CONTENT_URI,
//                null,
//                null,
//                null,
//                null);
//    }
//
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        Log.i(TAG, "onLoadFinished");
//        // giving the newly creating cursor to the adapter
//        mAdapter.swapCursor(cursor);
//        // if we have the previous state for mListView, restore it. This is convenient for the users' experience
//        // when he/she returns from the detail fragment
//        if(mListViewState != null) {
//            Log.i(TAG, "trying to restore listview state..");
//            mListView.onRestoreInstanceState(mListViewState);
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//        mAdapter.swapCursor(null);
//    }
//
//    // this function creates a system account which is needed to use sync adapter
//    public static Account CreateSyncAccount(Context context) {
//        Log.i(TAG, "CreateSyncAccount");
//        boolean newAccount = false;
//        // have we already synced the data? Store the answer in SharedPreferences
//        boolean setupComplete = PreferenceManager
//                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);
//
//        // Create account, if it's missing. (Either first run, or user has deleted account.)
//        Account account = new Account(Authenticator.ACCOUNT, Authenticator.ACCOUNT_TYPE);
//        AccountManager accountManager =
//                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
//        if (accountManager.addAccountExplicitly(account, null, null)) {
//            // Inform the system that this account supports sync
//            ContentResolver.setIsSyncable(account, ArtistsContract.CONTENT_AUTHORITY, 1);
//
//            newAccount = true;
//        }
//
//        /*
//        Right now sync adapter fetches the data only once, when the app is first started, or we cleared its data
//        One can easily set up scheduled updates here
//         */
//        if (newAccount || !setupComplete) {
//            Log.i(TAG, "refreshing data");
//            // force refreshing
//            TriggerRefresh();
//            PreferenceManager.getDefaultSharedPreferences(context).edit()
//                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
//        }
//        return account;
//    }
//
//    public static void TriggerRefresh() {
//        Bundle b = new Bundle();
//        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
//        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//        ContentResolver.requestSync(
//                new Account(Authenticator.ACCOUNT, Authenticator.ACCOUNT_TYPE),
//                ArtistsContract.CONTENT_AUTHORITY,
//                b);
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        // save the current state of mListView
//        mListViewState = mListView.onSaveInstanceState();
//        super.onSaveInstanceState(outState);
//    }
//
//    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
//        // Callback invoked with the sync adapter status changes.
//        @Override
//        public void onStatusChanged(int which) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Account account = new Account(Authenticator.ACCOUNT, Authenticator.ACCOUNT_TYPE);
//                    if (account == null) {
//                        Log.e(TAG, "Account error");
//                        return;
//                    }
//
//                    // Test the ContentResolver to see if the sync adapter is active or pending.
//                    // set the state of mLoadingBar accordingly
//                    boolean syncActive = ContentResolver.isSyncActive(
//                            account, ArtistsContract.CONTENT_AUTHORITY);
//                    boolean syncPending = ContentResolver.isSyncPending(
//                            account, ArtistsContract.CONTENT_AUTHORITY);
//                    if (syncActive || syncPending) {
//                        mLoadingBar.setVisibility(View.VISIBLE);
//                    }
//                    else {
//                        mLoadingBar.setVisibility(View.GONE);
//                    }
//                }
//            });
//        }
//    };

    public void launchDetails(int artist_id) {
        Bundle args = new Bundle();
        args.putInt(ArtistDetailFragment.ARTIST_ID_KEY, artist_id);

        ArtistDetailFragment fragment = new ArtistDetailFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_layout, fragment)
                .addToBackStack(DETAILFRAGMENT_TAG)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }


    private static class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d(TAG, "Headset is unplugged");
                        break;
                    case 1:
                        Log.d(TAG, "Headset is plugged");
                        break;
                    default:
                        Log.d(TAG, "I have no idea what the headset state is");
                }
            }
        }
    }

}
