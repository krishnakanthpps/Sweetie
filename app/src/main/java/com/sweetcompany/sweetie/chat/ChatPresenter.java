package com.sweetcompany.sweetie.chat;

import android.util.Log;

import com.sweetcompany.sweetie.model.ChatFB;
import com.sweetcompany.sweetie.firebase.FirebaseChatController;
import com.sweetcompany.sweetie.model.MessageFB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ghiro on 11/05/2017.
 */

class ChatPresenter implements ChatContract.Presenter, FirebaseChatController.ChatControllerListener {

    private static final String TAG = "ChatPresenter";

    private final ChatContract.View mView;
    private final FirebaseChatController mController;
    private final String mUserMail;   // id of messages of main user

    private static final SimpleDateFormat mIsoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private Date mLastMsgDate;
    private Calendar mLastMsgDateCalendar = Calendar.getInstance();
    private Calendar mMsgcalendar = Calendar.getInstance();

    ChatPresenter(ChatContract.View view, FirebaseChatController controller, String userMail){
        mView = view;
        mView.setPresenter(this);
        mController = controller;
        mController.addListener(this);

        mUserMail = userMail;
    }

    @Override
    public void sendTextMessage(MessageVM message) {
        // TODO: remove down cast -> use Factory method
        TextMessageVM messageVM = (TextMessageVM) message;
        MessageFB newMessage = new MessageFB(mUserMail, messageVM.getText(), messageVM.getTime(),
                messageVM.isBookmarked(), MessageFB.TEXT_MSG, "", "");

        // TODO: doesn't work, firebase trigger data also offline
        /*String newMessageUID = */mController.sendMessage(newMessage);
        //newMessage.setKey(newMessageUID);
        //messageVM.setKey(newMessageUID);
        //mView.updateMessage(messageVM);
    }

    @Override
    public void sendPhotoMessage(MessageVM messageVM) {
        // TODO: remove down cast -> use Factory method
        TextPhotoMessageVM photoMessageVM = (TextPhotoMessageVM) messageVM;
        MessageFB newMessage = new MessageFB(mUserMail, photoMessageVM.getText(),
                photoMessageVM.getTime(), photoMessageVM.isBookmarked(), MessageFB.PHOTO_MSG,
                photoMessageVM.getUriLocal(), "");

        String newMessageUID = mController.sendMedia(newMessage);
        newMessage.setKey(newMessageUID);
        photoMessageVM.setKey(newMessageUID);
        mView.updateMessage(photoMessageVM);
    }

    @Override
    public void bookmarkMessage(MessageVM messageVM, int type) {
        // TODO: remove down cast -> use Factory method
        if(type == MessageVM.TEXT_MSG){
            TextMessageVM msgVM = (TextMessageVM) messageVM;
            MessageFB updateMessage = new MessageFB(mUserMail, msgVM.getText(), msgVM.getTime(),
                    msgVM.isBookmarked(), MessageFB.PHOTO_MSG, "", "");
            updateMessage.setKey(msgVM.getKey());

            mController.updateMessage(updateMessage);
        }
        else if(type == MessageVM.TEXT_PHOTO_MSG)
        {
            TextPhotoMessageVM msgVM = (TextPhotoMessageVM) messageVM;
            MessageFB updateMessage = new MessageFB(mUserMail, msgVM.getText(), msgVM.getTime(),
                    msgVM.isBookmarked(), MessageFB.PHOTO_MSG, msgVM.getUriLocal(), msgVM.getUriStorage());
            updateMessage.setKey(msgVM.getKey());

            mController.updateMessage(updateMessage);
        }

    }


    // Callback from Database

    @Override
    public void onChatChanged(ChatFB chat) {
        ChatVM chatVM = new ChatVM(chat.getKey(), chat.getTitle());
        mView.updateChatInfo(chatVM);
    }

    @Override
    public void onMessageAdded(MessageFB message) {
        Date messageDate = getDateFromString(message.getDateTime());

        insertDateItemVM(messageDate);
        insertMessageVM(message);
    }

    private Date getDateFromString(String dateString) {
        Date messageDate = null;
        try {
            messageDate = mIsoFormat.parse(dateString);
        } catch (ParseException ex) {
            Log.w(TAG, ex.getMessage());
            messageDate = new Date();
        }

        return messageDate;
    }

    private void insertDateItemVM(Date messageDate) {
        if (mLastMsgDate == null) {
            // first message
            mLastMsgDate = messageDate;
            mView.insertDateItem(new DateItemVM(messageDate));
        }
        else {
            mLastMsgDateCalendar.setTime(mLastMsgDate);
            mMsgcalendar.setTime(messageDate);

            if (mLastMsgDateCalendar.get(Calendar.YEAR) != mMsgcalendar.get(Calendar.YEAR) ||
                    mLastMsgDateCalendar.get(Calendar.MONTH) != mMsgcalendar.get(Calendar.MONTH) ||
                    mLastMsgDateCalendar.get(Calendar.DAY_OF_MONTH) != mMsgcalendar.get(Calendar.DAY_OF_MONTH)) {
                // insert Data ViewModel if LastMsgDate is before messageDate
                mLastMsgDate = messageDate;
                mView.insertDateItem(new DateItemVM(messageDate));
            }
        }
    }

    private void insertMessageVM(MessageFB message) {
        MessageVM msgVM = null;
        // insert the message ViewModel
        if (message.getType() == MessageFB.TEXT_MSG){
            msgVM = createTextMessageVM(message);
        }
        else if (message.getType() == MessageFB.PHOTO_MSG)
        {
            msgVM = createTextPhotoMessageVM(message);
        }
        else {
            Log.w(TAG, "messageVM not initialize!");
            // TODO: build default messageVM?
        }
        mView.updateMessage(msgVM);
    }


    @Override
    public void onMessageRemoved(MessageFB message) {
        MessageVM msgVM = null;
        if(message.getType()==MessageFB.TEXT_MSG){
            msgVM = createTextMessageVM(message);
        }
        else if(message.getType()==MessageFB.PHOTO_MSG)
        {
            msgVM = createTextPhotoMessageVM(message);
        }
        mView.removeMessage(msgVM);
    }

    @Override
    public void onMessageChanged(MessageFB message) {
        MessageVM msgVM = null;
        if(message.getType()==MessageFB.TEXT_MSG){
            msgVM = createTextMessageVM(message);
        }
        else if(message.getType()==MessageFB.PHOTO_MSG)
        {
            msgVM = createTextPhotoMessageVM(message);
        }
        mView.changeMessage(msgVM);
    }

    private MessageVM createTextMessageVM(MessageFB message) {
        // Understand if the message is of Main User
        boolean who = MessageVM.THE_PARTNER;
        if (message.getEmail().equals(mUserMail)) {
            who = MessageVM.THE_MAIN_USER;
        }
        // Create respective ViewModel
        return new TextMessageVM(message.getText(), who, message.getDateTime(),
                message.isBookmarked(), message.getKey(), 0);
    }

    private MessageVM createTextPhotoMessageVM(MessageFB message) {
        // Understand if the message is of Main User
        boolean who = MessageVM.THE_PARTNER;
        if (message.getEmail().equals(mUserMail)) {
            who = MessageVM.THE_MAIN_USER;
        }
        // Create respective ViewModel
        return new TextPhotoMessageVM(message.getText(), who, message.getDateTime(),
                message.isBookmarked(), message.getKey(), message.getUriLocal(),
                message.getUriStorage(), 0);
    }


    @Override
    public void onUploadPercent(MessageFB media, int perc){
        MessageVM messageVM = createTextPhotoMessageVM(media);
        mView.updatePercentUpload(messageVM, perc);
    }

}
