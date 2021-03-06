package com.codernauti.sweetie.actions;

import android.net.Uri;
import android.util.Log;

import com.codernauti.sweetie.model.ActionFB;
import com.codernauti.sweetie.firebase.FirebaseActionsController;
import com.codernauti.sweetie.utils.DataMaker;

import java.util.ArrayList;
import java.util.List;


public class ActionsPresenter implements ActionsContract.Presenter,
                                         FirebaseActionsController.OnFirebaseActionsDataChange {

    public static final String TAG = "ActionsPresenter" ;

    private final ActionsContract.View mView;
    private final FirebaseActionsController mController;

    private List<ActionVM> mActionsList = new ArrayList<>();
    private String mUserUid;

    public ActionsPresenter(ActionsContract.View view, FirebaseActionsController controller, String userUid) {
        mController = controller;
        mController.addListener(this);

        mView = view;
        mView.setPresenter(this);

        mUserUid = userUid;
    }

    @Override
    public List<String> addAction(String actionTitle, String username, int actionType) {
        // TODO: add mDescriptionTextView and fix username variable, what username???
        ActionFB action = new ActionFB(actionTitle, mUserUid, username, "", DataMaker.get_UTC_DateTime(), actionType);
        return mController.pushAction(action);
    }

    @Override
    public void removeAction(String actionUid, int actionChildType, String actionChildUid) {
        mController.removeAction(actionUid, actionChildType, actionChildUid);
    }

    @Override
    public void uploadActionImage(String actionUid, Uri imageUri) {
        mController.uploadImage(actionUid, imageUri);
    }

    // Controller callbacks

    // Clear actions, retrieve all actions on server
    @Override
    public void onActionsListChanged(List<ActionFB> actionsFB) {
        ActionVM newActionVM;
        mActionsList.clear();

        for(ActionFB action : actionsFB){
            int counter = 0;
            if (action.getNotificationCounters() != null && action.getNotificationCounters().containsKey(mUserUid)) {
                counter = action.getNotificationCounters().get(mUserUid).getCounter();
            }

            // TODO: use a Factory Method
            // for example use ActionConverter.convertToViewModel(action);
            switch (action.getType()) {
                case ActionFB.CHAT:
                    newActionVM = new ActionChatVM(
                            action.getTitle(), action.getDescription(), action.getLastUpdateDate(),
                            action.getType(), action.getKey(), action.getKey(),
                            counter);
                    newActionVM.setImageUrl(action.getImageUrl());
                    mActionsList.add(newActionVM);
                    break;

                case ActionFB.GALLERY:
                    newActionVM = new ActionGalleryVM(
                            action.getTitle(), action.getDescription(), action.getLastUpdateDate(),
                            action.getType(), action.getKey(), action.getKey(),
                            counter);
                    newActionVM.setImageUrl(action.getImageUrl());
                    mActionsList.add(newActionVM);
                    break;

                case ActionFB.TODOLIST:
                    newActionVM = new ActionToDoListVM(
                            action.getTitle(), action.getDescription(), action.getLastUpdateDate(),
                            action.getType(), action.getKey(), action.getKey(),
                            counter);
                    newActionVM.setImageUrl(action.getImageUrl());
                    mActionsList.add(newActionVM);
                    break;

                case ActionFB.GEOGIFT:
                    Log.d(TAG, "geogift finded!");
                    if(action.getUserCreator().equals(mUserUid) || action.getIsTriggered()) {
                        newActionVM = new ActionGeogiftVM(
                                action.getTitle(), action.getDescription(), action.getLastUpdateDate(),
                                action.getType(), action.getKey(), action.getKey(),
                                action.getIsTriggered(), counter);
                        newActionVM.setImageUrl(action.getImageUrl());
                        mActionsList.add(newActionVM);
                    }
                    break;
            }
        }
        mView.updateActionsList(mActionsList);
    }

}
