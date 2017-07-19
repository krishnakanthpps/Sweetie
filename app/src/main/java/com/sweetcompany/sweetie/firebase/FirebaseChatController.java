package com.sweetcompany.sweetie.firebase;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sweetcompany.sweetie.model.ChatFB;
import com.sweetcompany.sweetie.model.MessageFB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eduard on 21-May-17.
 */

public class FirebaseChatController {

    private static final String TAG = "FbChatController";

    private final DatabaseReference mChat;
    private final DatabaseReference mChatMessages;
    private final DatabaseReference mAction;

    private ValueEventListener mChatListener;
    private ChildEventListener mChatMessagesListener;


    private List<ChatControllerListener> mListeners = new ArrayList<>();

    public interface ChatControllerListener {
        void onChatChanged(ChatFB chat);

        void onMessageAdded(MessageFB message);
        void onMessageRemoved(MessageFB message);
        void onMessageChanged(MessageFB message);
    }


    public FirebaseChatController(String coupleUid, String chatKey, String actionKey) {
        mChat = FirebaseDatabase.getInstance()
                .getReference(Constraints.CHATS_NODE + "/" + coupleUid + "/" + chatKey);
        mChatMessages = FirebaseDatabase.getInstance()
                .getReference(Constraints.CHAT_MESSAGES_NODE + "/" + coupleUid + "/" + chatKey);
        mAction = FirebaseDatabase.getInstance()
                .getReference(Constraints.ACTIONS_NODE + "/" + coupleUid + "/" + actionKey);
    }

    public void addListener(ChatControllerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(ChatControllerListener listener) {
        mListeners.remove(listener);
    }


    public void attachListeners() {
        if (mChatMessagesListener == null) {
            mChatMessagesListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MessageFB newMessage = dataSnapshot.getValue(MessageFB.class);
                    newMessage.setKey(dataSnapshot.getKey());
                    Log.d(TAG, "onMessageAdded to chat: " + newMessage.getText());

                    for (ChatControllerListener listener : mListeners) {
                        listener.onMessageAdded(newMessage);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    MessageFB newMessage = dataSnapshot.getValue(MessageFB.class);
                    newMessage.setKey(dataSnapshot.getKey());
                    Log.d(TAG, "onChildChanged of chat: " + newMessage.getText());

                    for (ChatControllerListener listener : mListeners) {
                        listener.onMessageChanged(newMessage);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    MessageFB removedMessage = dataSnapshot.getValue(MessageFB.class);
                    Log.d(TAG, "onMessageRemoved from chat: " + removedMessage.getText());

                    for (ChatControllerListener listener : mListeners) {
                        listener.onMessageRemoved(removedMessage);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mChatMessages.addChildEventListener(mChatMessagesListener);
        }

        if (mChatListener == null) {
            mChatListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // TODO: test
                    ChatFB chat = dataSnapshot.getValue(ChatFB.class);
                    chat.setKey(dataSnapshot.getKey());

                    for (ChatControllerListener listener : mListeners) {
                        listener.onChatChanged(chat);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mChat.addValueEventListener(mChatListener);
        }
    }

    public void detachListeners() {
        if (mChatListener != null) {
            mChat.removeEventListener(mChatListener);
        }
        mChatListener = null;

        if (mChatMessagesListener != null) {
            mChatMessages.removeEventListener(mChatMessagesListener);
        }
        mChatMessagesListener = null;
    }


    public void updateMessage(MessageFB msg) {
        Log.d(TAG, "Update MessageFB: " + msg);

        DatabaseReference ref = mChatMessages.child(msg.getKey()).child("bookmarked");
        ref.setValue(msg.isBookmarked());
    }

    // push message to db and update action of this chat
    public void sendMessage(MessageFB msg) {
        Log.d(TAG, "Send MessageFB: " + msg);

        // push a message into mChatMessages reference
        mChatMessages.push().setValue(msg);

        // update description and dataTime of action of this associated Chat
        Map<String, Object> actionUpdates = new HashMap<>();
        actionUpdates.put("description", msg.getText());
        actionUpdates.put("dataTime", msg.getDateTime());
        mAction.updateChildren(actionUpdates);
    }

    private void updateActionLastMessage(String actionKey, MessageFB msg){
        DatabaseReference mActionReference = FirebaseDatabase.getInstance()
                .getReference().child("actions").child(actionKey).child("description");
        mActionReference.setValue(msg.getText());
        mActionReference = FirebaseDatabase.getInstance()
                .getReference().child("actions").child(actionKey).child("dataTime");
        mActionReference.setValue(msg.getDateTime());
    }


}