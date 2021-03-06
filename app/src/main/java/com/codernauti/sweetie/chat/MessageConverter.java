package com.codernauti.sweetie.chat;

import android.util.Log;

import com.codernauti.sweetie.model.MessageFB;

/**
 * Utility class that convert MessageFB to MessageVM
 */
public class MessageConverter {

    private static final String TAG = "MessageConverter";

    public static MessageVM createMessageVM(MessageFB message, String userUid) {

        boolean who = checkWho(message.getUserUid(), userUid);

        // understand type of MessageFB and create the correct type of message
        MessageVM msgVM = null;
        if (message.getType() == MessageFB.TEXT_MSG) {
            msgVM = MessageConverter.createTextMessageVM(message, who);
        }
        else if (message.getType() == MessageFB.PHOTO_MSG)
        {
            msgVM = MessageConverter.createTextPhotoMessageVM(message, who);
        }
        else {
            Log.w(TAG, "messageVM not initialize!");
            // TODO: build default messageVM?
        }

        return msgVM;
    }

    private static boolean checkWho(String msgUserUid, String userUid) {
        return msgUserUid.equals(userUid)? MessageVM.THE_MAIN_USER : MessageVM.THE_PARTNER;
    }

    private static MessageVM createTextMessageVM(MessageFB message, boolean who) {
        return new TextMessageVM(message.getText(), message.getUserUid(), who, message.getDateTime(),
                message.isBookmarked(), message.getKey());
    }

    private static MessageVM createTextPhotoMessageVM(MessageFB message, boolean who) {
        return new TextPhotoMessageVM(message.getText(), message.getUserUid(), who, message.getDateTime(),
                message.isBookmarked(), message.getKey(), message.getUriStorage());
    }
}
