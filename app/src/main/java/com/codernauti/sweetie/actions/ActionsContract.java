package com.codernauti.sweetie.actions;

import android.net.Uri;

import java.util.List;


public interface ActionsContract {

    interface View {
        void setPresenter(ActionsContract.Presenter presenter);
        void updateActionsList(List<ActionVM> actionsVM);
    }

    interface Presenter {
        List<String> addAction(String actionTitle, String username, int actionType);
        void removeAction(String actionUid, int actionChildType, String actionChildUid);
        void uploadActionImage(String actionUid, Uri imageUri);
    }

    interface DialogView {
        void setPresenter(Presenter presenter);
    }
}
