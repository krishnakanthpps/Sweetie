package com.sweetcompany.sweetie.Chat;

import android.util.Log;

import com.sweetcompany.sweetie.Firebase.ChatFB;
import com.sweetcompany.sweetie.Firebase.FirebaseChatController;
import com.sweetcompany.sweetie.Firebase.MessageFB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghiro on 11/05/2017.
 */

public class ChatPresenter implements ChatContract.Presenter, FirebaseChatController.OnFirebaseChatDataChange {

    private static final String TAG = "ChatPresenter";

    private ChatContract.View mView;
    private FirebaseChatController mFirebaseController;
    private String mUserMail;   // id of messages of main user
    private String mChatKey;

    public ChatPresenter(ChatContract.View view, String userMail, String chatKey){
        mView = view;
        mView.setPresenter(this);
        mFirebaseController = FirebaseChatController.getInstance();
        mUserMail = userMail;
        mChatKey = chatKey;
    }

    @Override
    public void start() {
        mFirebaseController.attachNetworkDatabase();
        mFirebaseController.addListener(this);
    }

    @Override
    public void pause() {
        mFirebaseController.detachNetworkDatabase();
        mFirebaseController.removeListener(this);
    }

    @Override
    public void sendMessage(MessageVM message) {
        // TODO: remove down cast -> use Factory method
        TextMessageVM messageVM = (TextMessageVM)message;

        MessageFB newMessage = new MessageFB(mUserMail, messageVM.getText(), messageVM.getTime(), messageVM.isBookmarked());
        mFirebaseController.pushMessage(newMessage);
    }

    @Override
    public void notifyNewMessages(List<MessageFB> messages) {
        List<MessageVM> messagesVM = new ArrayList<>();
        for (MessageFB msg : messages) {
            boolean who = MessageVM.THE_PARTNER;

            if (msg.getEmail() != null) {   // TODO remove check in future
                if (msg.getEmail().equals(mUserMail)) {
                    who = MessageVM.THE_MAIN_USER;
                }
            }

            TextMessageVM msgVM = new TextMessageVM(msg.getText(), who, msg.getTime(),
                    msg.isBookmarked(), msg.getKey());
            messagesVM.add(msgVM);
        }

        mView.updateMessages(messagesVM);
    }

    @Override
    public void notifyChats(List<ChatFB> chats) {
        ChatVM chatVM = new ChatVM("", "");
        for (ChatFB chat : chats) {
            // search for the exact chat opened
            if (chat.getKey().equals(mChatKey)) {
                chatVM = new ChatVM(chat.getKey(), chat.getTitle());
            }
            else {
                Log.w(TAG, "notifyChats(): can't find Chat in database");
            }
        }
        mView.updateChatInfo(chatVM);
    }

    @Override
    public void bookmarkMessage(MessageVM messageVM) {
        // TODO: remove down cast -> use Factory method
        TextMessageVM msgVM = (TextMessageVM) messageVM;
        MessageFB updateMessage = new MessageFB(mUserMail, msgVM.getText(), msgVM.getTime(), msgVM.isBookmarked());
        updateMessage.setKey(msgVM.getKey());

        mFirebaseController.updateMessage(updateMessage);
    }

}
