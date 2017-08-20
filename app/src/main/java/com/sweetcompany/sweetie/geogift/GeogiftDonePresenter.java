package com.sweetcompany.sweetie.geogift;

import com.sweetcompany.sweetie.firebase.FirebaseGeogiftDoneController;
import com.sweetcompany.sweetie.model.GeogiftFB;

/**
 * Created by ghiro on 17/08/2017.
 */

public class GeogiftDonePresenter implements GeogiftDoneContract.Presenter, FirebaseGeogiftDoneController.GeogiftDoneControllerListener{

    public static final String TAG = "GeogiftDone.presenter" ;

    private final GeogiftDoneContract.View mView;
    private final FirebaseGeogiftDoneController mController;
    private String mUserUID;   // id of messages of main user

    public GeogiftDonePresenter(GeogiftDoneContract.View view, FirebaseGeogiftDoneController controller, String userMail) {
        mView = view;
        mView.setPresenter(this);
        mController = controller;
        mController.addListener(this);

        mUserUID = userMail;
    }

    @Override
    public void onGeogiftChanged(GeogiftFB geogiftFB) {
        GeoItem geoItem = new GeoItem();
        geoItem = createGeoItem(geogiftFB);
        mView.updateGeogift(geoItem);
    }

    /**
     * Convert GeogiftFB to GeoItem
     */
    private GeoItem createGeoItem(GeogiftFB geoItemFB) {
        GeoItem geoItemNew = new GeoItem();
        geoItemNew.setType(geoItemFB.getType());
        geoItemNew.setMessage(geoItemFB.getMessage());
        geoItemNew.setAddress(geoItemFB.getAddress());
        geoItemNew.setLat(geoItemFB.getLat());
        geoItemNew.setLon(geoItemFB.getLon());
        geoItemNew.setUriS(geoItemFB.getUriS());
        geoItemNew.setBookmarked(geoItemFB.isBookmarked());
        geoItemNew.setDatetimeCreation(geoItemFB.getDatetimeCreation());
        geoItemNew.setDatetimeVisited(geoItemFB.getDatetimeVisited());

        return geoItemNew;
    }
}