package com.sweetcompany.sweetie.actions;

import com.sweetcompany.sweetie.geogift.GeoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eduard on 10/05/2017.
 */

public interface ActionsContract {

    interface View {
        void setPresenter(ActionsContract.Presenter presenter);
        void updateActionsList(List<ActionVM> actionsVM);
    }

    interface Presenter {
        List<String> addAction(String actionTitle, String username, int actionType);
        void removeAction(String actionUid, int actionChildType, String actionChildUid);
    }

    interface DialogView {
        void setPresenter(Presenter presenter);
    }
}
