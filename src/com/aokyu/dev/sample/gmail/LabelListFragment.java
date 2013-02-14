/*
 * Copyright 2013 Yu AOKI
 */

package com.aokyu.dev.sample.gmail;

import android.app.Activity;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LabelListFragment extends LabelLoaderFragment {

    /* package */ static final String TAG = LabelListFragment.class.getSimpleName();

    private ListView mLabelView;
    private LabelAdapter mLabelAdapter;

    public LabelListFragment() {
        super();
    }

    public static LabelListFragment newInstance() {
        LabelListFragment fragment = new LabelListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.label_panel, null);
        setupViews(contentView);
        return contentView;
    }

    private void setupViews(View rootView) {
        mLabelView = (ListView) rootView.findViewById(R.id.label_view);
        mLabelAdapter = new LabelAdapter(mContext, null);
        mLabelView.setAdapter(mLabelAdapter);
    }

    @Override
    protected void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mLabelAdapter != null) {
            Cursor oldCursor = mLabelAdapter.swapCursor(cursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }

    @Override
    protected void onLoaderReset(Loader<Cursor> loader) {
        if (mLabelAdapter != null) {
            mLabelAdapter.swapCursor(null);
        }
    }

    private static class LabelAdapter extends CursorAdapter {

        private LayoutInflater mInflater;

        private static final class ViewCache {
            public final TextView nameView;
            public final TextView countView;
            public final TextView unreadView;

            public ViewCache(View rootView) {
                nameView = (TextView) rootView.findViewById(R.id.name_view);
                countView = (TextView) rootView.findViewById(R.id.count_view);
                unreadView = (TextView) rootView.findViewById(R.id.unread_view);
            }
        }

        public LabelAdapter(Context context, Cursor c) {
            super(context, c, false);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.label_list_item, null);
            view.setTag(new ViewCache(view));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewCache cache = (ViewCache) view.getTag();

            String name = cursor.getString(ColumnIndex.NAME);
            cache.nameView.setText(name);

            int unread = cursor.getInt(ColumnIndex.UNREAD_CONVERSATIONS);
            cache.unreadView.setText(String.valueOf(unread));

            int count = cursor.getInt(ColumnIndex.CONVERSATIONS);
            cache.countView.setText(String.valueOf(count));
        }
    }
}
