package com.codernauti.sweetie.geogift;

import android.util.Log;

import com.codernauti.sweetie.firebase.FirebaseGeogiftMakerController;
import com.codernauti.sweetie.model.ActionFB;
import com.codernauti.sweetie.utils.DataMaker;

import java.util.List;


public class GeogiftMakerPresenter implements GeogiftMakerContract.Presenter, FirebaseGeogiftMakerController.GeogiftMakerControllerListener{

    public static final String TAG = "GeogiftMaker.presenter" ;

    private final GeogiftMakerContract.View mView;
    private final FirebaseGeogiftMakerController mController;
    private String mUserUID;   // id of messages of main user

    public GeogiftMakerPresenter(GeogiftMakerContract.View view, FirebaseGeogiftMakerController controller, String userMail) {
        mView = view;
        mView.setPresenter(this);
        mController = controller;
        mController.addListener(this);

        mUserUID = userMail;
    }

    @Override
    public List<String> pushGeogiftAction(GeogiftVM geoItem, String userInputGeogiftTitle, String userUid) {
        ActionFB action = null;
        action = new ActionFB(userInputGeogiftTitle, mUserUID, userUid, "", DataMaker.get_UTC_DateTime(), ActionFB.GEOGIFT);

        if (action != null) {
            return mController.pushGeogiftAction(action, userInputGeogiftTitle, geoItem);
        }
        else {
            Log.d(TAG, "An error in the creation of a new GeogiftAction occurs!");
            return null;
        }
    }

    @Override
    public void uploadMedia(String uriImage) {
        mController.uploadMedia(uriImage);
    }

    @Override
    public void onMediaAdded(String uriStorage) {
        mView.setUriStorage(uriStorage);
    }

    @Override
    public void onUploadPercent(int perc) {
        mView.updatePercentUpload(perc);
    }
}