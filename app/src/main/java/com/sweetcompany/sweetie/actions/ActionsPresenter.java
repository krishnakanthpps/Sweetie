package com.sweetcompany.sweetie.actions;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sweetcompany.sweetie.geogift.GeoItem;
import com.sweetcompany.sweetie.model.ActionFB;
import com.sweetcompany.sweetie.firebase.FirebaseActionsController;
import com.sweetcompany.sweetie.model.GeogiftFB;
import com.sweetcompany.sweetie.utils.DataMaker;
import com.sweetcompany.sweetie.utils.GeoUtils;
import com.sweetcompany.sweetie.utils.MiniPrefDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Eduard on 10/05/2017.
 */

public class ActionsPresenter implements ActionsContract.Presenter,
                                        FirebaseActionsController.OnFirebaseActionsDataChange {

    public static final String TAG = "Action.presenter" ;

    private final ActionsContract.View mView;
    private final FirebaseActionsController mController;

    private List<ActionVM> mActionsList = new ArrayList<>();
    private List<ActionVM> mActionsGeofitList = new ArrayList<>();
    private String mUserID;
    private MiniPrefDB miniPrefDB;
    private GeoUtils geoUtils;
    private Context mContext;


    public ActionsPresenter(ActionsContract.View view, FirebaseActionsController controller, String userID, Context context) {
        mView = view;
        mView.setPresenter(this);
        mController = controller;
        mController.addListener(this);

        mUserID = userID;
        mContext = context;
    }

    @Override
    public List<String> pushChatAction(String userInputChatTitle, String username) {
        ActionFB action = null;
        // TODO: add description and fix username variable, what username???
        action = new ActionFB(userInputChatTitle, mUserID, username, "", DataMaker.get_UTC_DateTime(), ActionFB.CHAT);

        if (action != null) {
            return mController.pushChatAction(action, userInputChatTitle);
        }
        else {
            Log.d(TAG, "An error in the creation of a new ChatAction occurs!");
            return null;
        }
    }

    @Override
    public List<String> pushGalleryAction(String userInputGalleryTitle, String username) {
        ActionFB action = null;
        // TODO: add description and fix username variable, what username???
        action = new ActionFB(userInputGalleryTitle, mUserID, username, "", DataMaker.get_UTC_DateTime(), ActionFB.GALLERY);

        if (action != null) {
            return mController.pushGalleryAction(action, userInputGalleryTitle);
        }
        else {
            Log.d(TAG, "An error in the creation of a new GalleryAction occurs!");
            return null;
        }
    }

    @Override
    public List<String> pushToDoListAction(String userInputToDoListTitle, String username) {
        ActionFB action = null;
        // TODO: add description and fix username variable, what username???

            action = new ActionFB(userInputToDoListTitle, mUserID, username, "", DataMaker.get_UTC_DateTime(), ActionFB.TODOLIST);


        if (action != null) {
            return mController.pushToDoListAction(action, userInputToDoListTitle);
        }
        else {
            Log.d(TAG, "An error in the creation of a new ToDoListAction occurs!");
            return null;
        }
    }

    @Override
    public void retrieveGeogift() {
        mController.retrieveGeogiftFB();
    }

    /*@Override
    public List<String> pushGeogiftAction(String userInputGeogiftTitle, String username) {
        ActionFB action = null;
        // TODO: add description and fix username variable, what username???
        action = new ActionFB(userInputGeogiftTitle, mUserID, username, "", DataMaker.get_UTC_DateTime(), ActionFB.GEOGIFT);

        if (action != null) {
            //return mController.pushGeogiftAction(action, userInputGeogiftTitle);
            return null;
        }
        else {
            Log.d(TAG, "An error in the creation of a new GeogiftAction occurs!");
            return null;
        }
    }*/

    /*@Override
    public void pushAction(String userInputGalleryTitle, String username) {
        ActionFB action = null;
        try {
            action = new ActionFB(userInputGalleryTitle, username, "", DataMaker.get_UTC_DateTime(), ActionFB.PHOTO);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mController.pushAction(action);
    }*/

    // Controller callbacks

    // Clear actions, retrieve all actions on server
    @Override
    public void updateActionsList(List<ActionFB> actionsFB) {
        ActionVM newActionVM;
        mActionsList.clear();

        for(ActionFB action : actionsFB){
            // TODO: use a Factory Method
            // for example use ActionConverter.convertToViewModel(action);
            switch (action.getType()) {
                case 0:
                    newActionVM = new ActionChatVM(action.getTitle(), action.getDescription(),
                            action.getDataTime(), action.getType(), action.getChildKey(), action.getKey());
                    mActionsList.add(newActionVM);
                    break;
                case 1:
                    newActionVM = new ActionGalleryVM(action.getTitle(), action.getDescription(),
                            action.getDataTime(), action.getType(), action.getChildKey(), action.getKey());
                    mActionsList.add(newActionVM);
                    break;
                case 2:
                    newActionVM = new ActionToDoListVM(action.getTitle(), action.getDescription(),
                            action.getDataTime(), action.getType(), action.getChildKey(), action.getKey());
                    mActionsList.add(newActionVM);
                    break;
                case 3:
                    Log.d(TAG, "geogift finded!");
                        if(action.getUserCreator().equals(mUserID) || action.isVisited()) {
                            newActionVM = new ActionGeogiftVM(action.getTitle(), action.getDescription(),
                                    action.getDataTime(), action.getType(), action.getChildKey(), action.getKey(), action.isVisited());
                            mActionsList.add(newActionVM);
                        }else{
                            mView.checkGeofences();
                        }
                    break;
            }
        }
        mView.updateActionsList(mActionsList);
    }

    @Override
    public void onRetrievedGeogift(List<GeogiftFB> geogiftsFB) {
        ArrayList<GeoItem> geoItems = new ArrayList<>();
        for(GeogiftFB geogift: geogiftsFB){
            GeoItem geoItem = createGeoItem(geogift);
            geoItems.add(geoItem);
        }
        mView.registerGeofence(geoItems);
    }

    private GeoItem createGeoItem(GeogiftFB geoItemFB){
        GeoItem geoItemNew = new GeoItem();
        geoItemNew.setUserCreatorUID(geoItemFB.getUserCreatorUID());
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
