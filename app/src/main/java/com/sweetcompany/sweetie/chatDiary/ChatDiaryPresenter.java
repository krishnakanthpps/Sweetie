package com.sweetcompany.sweetie.chatDiary;

import com.sweetcompany.sweetie.chat.MessageConverter;
import com.sweetcompany.sweetie.chat.MessageVM;
import com.sweetcompany.sweetie.firebase.FirebaseChatDiaryController;
import com.sweetcompany.sweetie.model.MessageFB;

/**
 * Created by Eduard on 22-Aug-17.
 */

class ChatDiaryPresenter implements ChatDiaryContract.Presenter,
        FirebaseChatDiaryController.Listener {

    private static final String TAG = "ChatDiaryPresenter";

    private final FirebaseChatDiaryController mController;
    private final ChatDiaryContract.View mView;

    private final String mUserUid;

    ChatDiaryPresenter(ChatDiaryContract.View view, FirebaseChatDiaryController controller, String userUid) {
        mUserUid = userUid;

        mController = controller;
        mController.setListener(this);

        mView = view;
        mView.setPresenter(this);
    }


    @Override
    public void removeBookmarkedMessage(MessageVM message) {
        mController.removeBookmarkedMessage(message.getKey());
    }


    // Controller callbacks

    @Override
    public void onMessageAdded(MessageFB message) {
        MessageVM messageVM = MessageConverter.createMessageVM(message, mUserUid);
        mView.updateMessage(messageVM);
    }

    @Override
    public void onMessageRemoved(MessageFB message) {
        mView.removeMessage(message.getKey());
    }
}
