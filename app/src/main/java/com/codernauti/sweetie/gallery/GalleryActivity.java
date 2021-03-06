package com.codernauti.sweetie.gallery;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.codernauti.sweetie.BaseActivity;
import com.codernauti.sweetie.R;
import android.util.Log;
import com.codernauti.sweetie.firebase.FirebaseGalleryController;

public class GalleryActivity extends BaseActivity {

    private static final String TAG = "GalleryActivity";

    // key for Intent extras
    public static final String GALLERY_TITLE = "GalleryTitle";    // For offline user
    public static final String ACTION_DATABASE_KEY = "ActionDatabaseKey";

    private String mActionKey;

    private GalleryContract.Presenter mPresenter;
    private FirebaseGalleryController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);

        if (savedInstanceState == null) {   // first opened
            savedInstanceState = getIntent().getExtras();
        }

        if (savedInstanceState != null) {
            mActionKey = savedInstanceState.getString(ACTION_DATABASE_KEY);

            Log.d(TAG, "from Intent GALLERY_TITLE: " +
                    savedInstanceState.getString(GALLERY_TITLE));
            Log.d(TAG, "from Intent GALLERY_TITLE_DATABASE_KEY: " +
                    savedInstanceState.getString(ACTION_DATABASE_KEY));
        }
        else {
            Log.w(TAG, "No savedInstanceState or intentArgs!");
        }

        GalleryFragment view = (GalleryFragment) getSupportFragmentManager()
                               .findFragmentById(R.id.gallery_fragment_container);

        if (view == null) {
            view = GalleryFragment.newInstance(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.add(R.id.gallery_fragment_container, view);
            transaction.commit();
        }

        if (mActionKey != null) {
            mController = new FirebaseGalleryController(super.mCoupleUid, mActionKey,
                    super.mUserUid, super.mPartnerUid);
            mPresenter = new GalleryPresenter(view, mController, super.mUserUid);
        }
        else {
            Log.w(TAG, "Impossible to create GalleryController and GalleryPresenter because actionUid is NULL");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.attachListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mController.detachListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTION_DATABASE_KEY, mActionKey);
    }
}

