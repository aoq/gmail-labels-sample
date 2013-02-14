/*
 * Copyright 2013 Yu AOKI
 */

package com.aokyu.dev.sample.gmail;

import android.accounts.Account;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;

import com.aokyu.dev.sample.gmail.AccountListFragment.OnAccountItemClickListener;
import com.aokyu.dev.sample.gmail.LabelLoaderFragment.Argument;
import com.google.android.gm.contentprovider.GmailContract;

public class MailActivity extends Activity implements OnAccountItemClickListener {

    private boolean mTransactionAllowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_screen);

        mTransactionAllowed = true;
        showAccountListFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTransactionAllowed = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTransactionAllowed = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mTransactionAllowed = false;
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mail_screen, menu);
        return true;
    }

    private void showAccountListFragment() {
        FragmentManager manager = getFragmentManager();

        AccountListFragment fragment =
                (AccountListFragment) manager.findFragmentByTag(AccountListFragment.TAG);
        if (fragment == null) {
            fragment = AccountListFragment.newInstance();
        }

        showFragment(fragment);
    }

    private void showLabelListFragment(Account account) {
        FragmentManager manager = getFragmentManager();

        LabelListFragment fragment =
                (LabelListFragment) manager.findFragmentByTag(LabelListFragment.TAG);
        if (fragment == null) {
            fragment = LabelListFragment.newInstance();
        }

        Bundle args = new Bundle();
        Uri uri = GmailContract.Labels.getLabelsUri(account.name);
        args.putParcelable(Argument.LABELS_URI, uri);
        fragment.setArguments(args);
        showFragment(fragment, LabelListFragment.TAG);
    }

    private void showFragment(Fragment fragment) {
        if (!isFragmentTransactionAllowed()) {
            return;
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_view, fragment);
        transaction.commit();
    }

    private void showFragment(Fragment fragment, String tag) {
        if (!isFragmentTransactionAllowed()) {
            return;
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.addToBackStack(tag);
        transaction.replace(R.id.container_view, fragment, tag);
        transaction.commit();
    }

    public boolean isFragmentTransactionAllowed() {
        return mTransactionAllowed;
    }

    @Override
    public void onAccountItemClick(Account account) {
        showLabelListFragment(account);
    }
}
