package com.sweetcompany.sweetie;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ghiro on 17/05/2017.
 */

public class FirebaseController {

    private static FirebaseController sInstance;

    private Context mContext;
    private MainActivity mMainActivity;

    private GoogleApiClient mGoogleApiClient;
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseUser mFirebaseUser;
    private static FirebaseDatabase database;
    private static DatabaseReference mFirebaseReference;


    public static FirebaseController getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseController();
        }
        return sInstance;
    }

    public static void init(){
        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        //inizialize FirebaseDatabase
        database = FirebaseDatabase.getInstance();
    }

    public void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public Context getAppContext() {
        return mContext;
    }

    public MainActivity getRipActivity() {
        return mMainActivity;
    }

    public FirebaseUser getFirebaseUser() {
        return mFirebaseUser;
    }

    public FirebaseAuth getAuth() {
        return mFirebaseAuth;
    }

    public FirebaseDatabase getDatabase(){
        return database;
    }

    public DatabaseReference getDatabaseUserReferences(){
        mFirebaseReference = database.getReference().child("users");
        //TODO add assertions
        return mFirebaseReference;
    }


    public DatabaseReference getDatabaseActionsReferences(){
        mFirebaseReference = database.getReference().child("actions");

        return mFirebaseReference;
    }
}
