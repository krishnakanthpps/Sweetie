package com.sweetcompany.sweetie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sweetcompany.sweetie.couple.CoupleActivity;
import com.sweetcompany.sweetie.utils.SharedPrefKeys;
import com.sweetcompany.sweetie.utils.Utility;

/**
 * Created by Eduard on 26-Aug-17.
 */

/**
 *  BaseActivity class for every activity of app, her responsibilities are:
 *  - Manage the login status
 *  - Manage the user relationship status
 *  - TODO: Manage the Geogift trigger
 */
public class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String BASE_TAG = "BaseActivity";

    // utils fields for the correct working of all activity
    protected String mUserUid;
    protected String mUserEmail;
    protected String mCoupleUid;

    protected GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        /* Setup the Google API object to allow Google logins */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_ID))
                .requestEmail()
                .build();

        /* Setup the Google API object to allow Google+ logins */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();

        // Getting utils data for all activity
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mUserUid = sp.getString(SharedPrefKeys.USER_UID, SharedPrefKeys.DEFAULT_VALUE);
        mUserEmail = sp.getString(SharedPrefKeys.MAIL, SharedPrefKeys.DEFAULT_VALUE);
        mCoupleUid = sp.getString(SharedPrefKeys.COUPLE_UID, SharedPrefKeys.DEFAULT_VALUE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(BASE_TAG, "onStart()");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);

        // if (!(this instanceof RegisterActivity)) {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) { // user sign out
                    // Clear out shared preferences "sweetie_preferences.xml"
                    Utility.clearSharedPreferences(BaseActivity.this);
                    sp.edit().clear().apply();

                    /*SharedPreferences.Editor spe = mSharedPref.edit();
                    spe.putString(Utility.USER_UID, Utility.DEFAULT_VALUE);
                    spe.putString(Utility.MAIL, Utility.DEFAULT_VALUE);
                    spe.apply();*/

                    takeUserToLoginScreenOnUnAuth();
                }
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthListener);
        //}
    }

    private void takeUserToLoginScreenOnUnAuth() {
        // TODO move user to MainActivity or RegisterActivity?
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(BASE_TAG, "onPause()");

        /* Cleanup the AuthStateListener */
        /*if (!(this instanceof RegisterActivity)) {*/
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
        /*}*/

        // remove shared preferences listener
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }


    // Google API listener

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(BASE_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    // com_sweetcompany_sweetie_preferences.xml listener

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {

        if (key.equals(SharedPrefKeys.USER_RELATIONSHIP_STATUS)) {
            int userRelationshipStatus = sp.getInt(SharedPrefKeys.USER_RELATIONSHIP_STATUS,
                    UserMonitorService.SINGLE);
            String partnerUsername = sp.getString(SharedPrefKeys.PARTNER_USERNAME, SharedPrefKeys.DEFAULT_VALUE);
            mCoupleUid = sp.getString(SharedPrefKeys.COUPLE_UID, SharedPrefKeys.DEFAULT_VALUE);

            Log.d(BASE_TAG, "onSharedPreferenceChanged() - USER_RELATIONSHIP_STATUS = " + userRelationshipStatus);

            if (userRelationshipStatus == UserMonitorService.BREAK_SINGLE) {
                startCoupleActivity("You break up with " + partnerUsername);
            }
            else if (userRelationshipStatus == UserMonitorService.COUPLED) {
                startCoupleActivity("You couple with " + partnerUsername);
            }
        }

    }

    private void startCoupleActivity(String messageToShow) {
        // start activity and clean the Task (back stack of activity)
        Intent intent = new Intent(this, CoupleActivity.class);
        intent.putExtra(CoupleActivity.MESSAGE_TO_SHOW_KEY, messageToShow);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

}
