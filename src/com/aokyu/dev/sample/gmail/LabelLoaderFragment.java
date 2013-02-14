/*
 * Copyright 2013 Yu AOKI
 */

package com.aokyu.dev.sample.gmail;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gm.contentprovider.GmailContract;

public abstract class LabelLoaderFragment extends Fragment {

    public final class ColumnIndex {

        public static final int ID = 0;
        public static final int NAME = 1;
        public static final int CONVERSATIONS = 2;
        public static final int UNREAD_CONVERSATIONS = 3;

        private ColumnIndex() {}
    }

    public final class Argument {
        public static final String LABELS_URI = "arg_labels_uri";

        private Argument() {}
    }

    protected static final Uri NO_URI = Uri.EMPTY;

    protected Context mContext;
    private LoaderManager mLoaderManager;

    private LabelLoaderCallbacks mLoaderCallbacks;

    private static final int ID_LABEL_LOADER = 0x00000002;

    protected Uri mUri = NO_URI;

    protected abstract void onLoadFinished(Loader<Cursor> loader, Cursor cursor);
    protected abstract void onLoaderReset(Loader<Cursor> loader);

    public LabelLoaderFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
            mContext = activity.getApplicationContext();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoaderManager = getLoaderManager();
        mLoaderCallbacks = new LabelLoaderCallbacks(mContext, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        loadLabels(args);
    }

    private void loadLabels(Bundle args) {
        Loader<Cursor> loader = mLoaderManager.getLoader(ID_LABEL_LOADER);

        if (loader != null) {
            mLoaderManager.restartLoader(ID_LABEL_LOADER, args, mLoaderCallbacks);
        } else {
            mLoaderManager.initLoader(ID_LABEL_LOADER, args, mLoaderCallbacks);
        }
    }

    private static final class LabelCursorLoader extends CursorLoader {

        private static final String[] PROJECTION = new String[] {
            GmailContract.Labels._ID,
            GmailContract.Labels.NAME,
            GmailContract.Labels.NUM_CONVERSATIONS,
            GmailContract.Labels.NUM_UNREAD_CONVERSATIONS,
        };

        public LabelCursorLoader(Context context, Uri uri) {
            super(context, uri, PROJECTION, null, null, null);
        }
    }

    private static class LabelLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private WeakReference<Context> mContext;
        private WeakReference<LabelLoaderFragment> mFragment;

        public LabelLoaderCallbacks(Context context, LabelLoaderFragment fragment) {
            mContext = new WeakReference<Context>(context);
            mFragment = new WeakReference<LabelLoaderFragment>(fragment);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri = args.getParcelable(Argument.LABELS_URI);
            return new LabelCursorLoader(mContext.get(), uri);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            LabelLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoadFinished(loader, cursor);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            LabelLoaderFragment fragment = mFragment.get();
            if (fragment != null) {
                fragment.onLoaderReset(loader);
            }
        }
    }
}
