package com.example.grin.mobilizationmusic;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by grin on 4/21/16.
 */
public class ArtistListAdapter extends CursorAdapter {
    public static final String TAG = "ArtistListAdapter";

    private static final int COLUMN_ID = 0;
    /** Column index for name */
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_SMALL_COVER = 2;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView coverView;
        public final ProgressBar progressView;
        public final TextView nameView;


        public ViewHolder(View view) {
            coverView = (ImageView) view.findViewById(R.id.list_image_view);
            progressView = (ProgressBar) view.findViewById(R.id.list_image_progress_bar);
            nameView = (TextView) view.findViewById(R.id.list_artist_name);
        }
    }

    public ArtistListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.artist_list_content, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.progressView.setVisibility(View.VISIBLE);

        Picasso.with(context).load(cursor.getString(COLUMN_SMALL_COVER)).into(viewHolder.coverView, new Callback() {
            @Override
            public void onSuccess() {
                viewHolder.progressView.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                Log.e(TAG, "Picasso error");
            }
        });
        viewHolder.nameView.setText(cursor.getString(COLUMN_NAME));
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
