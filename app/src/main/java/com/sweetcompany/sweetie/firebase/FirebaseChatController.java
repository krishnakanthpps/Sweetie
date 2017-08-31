package com.sweetcompany.sweetie.firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sweetcompany.sweetie.model.ActionDiaryFB;
import com.sweetcompany.sweetie.model.ActionFB;
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

    private final String mActionUid;
    private final String mChatTitle;
    private final String mCoupleUid;

    private final String mChatMessagesUrl;          // chat-message/<couple_uid>/<chat_uid>
    private final String mCoupleCalendarUrl;        // calendar/<couple_uid>
    private final String mCoupleActionsDiaryUrl;    // actionsDiary/<couple_uid>/<action_uid>

    private final String mActionUrl;                // actions/<couple_uid>/<action_uid>
    private final DatabaseReference mDatabase;
    private final DatabaseReference mChat;
    private final DatabaseReference mChatMessages;

    private final DatabaseReference mAction;
    private final StorageReference mStorageRef;

    private final FirebaseStorage mStorage;

    private ValueEventListener mChatListener;
    private ChildEventListener mChatMessagesListener;


    private List<ChatControllerListener> mListeners = new ArrayList<>();

    public interface ChatControllerListener {
        void onChatChanged(ChatFB chat);

        void onMessageAdded(MessageFB message);
        void onMessageRemoved(MessageFB message);
        void onMessageChanged(MessageFB message);
        void onUploadPercent(MessageFB media, int perc);
    }


    public FirebaseChatController(String coupleUid, String chatKey, String chatTitle, String actionKey) {
        mActionUid = actionKey;
        mChatTitle = chatTitle;

        mChatMessagesUrl = Constraints.CHAT_MESSAGES + "/" + coupleUid + "/" + chatKey;
        mCoupleCalendarUrl = Constraints.CALENDAR + "/" + coupleUid;
        mCoupleActionsDiaryUrl = Constraints.ACTIONS_DIARY + "/" + coupleUid;
        mActionUrl = Constraints.ACTIONS + "/" + coupleUid + "/" + actionKey;

        FirebaseDatabase firebaseDb = FirebaseDatabase.getInstance();
        mDatabase = firebaseDb.getReference();

        mChat = firebaseDb.getReference(Constraints.CHATS + "/" + coupleUid + "/" + chatKey);
        mChatMessages = firebaseDb.getReference(Constraints.CHAT_MESSAGES + "/" + coupleUid + "/" + chatKey);
        mAction = firebaseDb.getReference(Constraints.ACTIONS + "/" + coupleUid + "/" + actionKey);

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mCoupleUid = coupleUid;
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

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(mChatMessagesUrl + "/" + msg.getKey() + "/" + Constraints.BOOKMARK, msg.isBookmarked());

        String actionDiaryDataUrl = mCoupleActionsDiaryUrl + "/" + msg.getDate() + "/" + mActionUid;
        final String actionDiaryUrl = mCoupleCalendarUrl + "/" + msg.getYearAndMonth() + "/"
                                + msg.getDay() + "/" + mActionUid;

        if (msg.isBookmarked()) {
            ActionDiaryFB action = new ActionDiaryFB(ActionFB.CHAT, msg.getDate(), mChatTitle, msg.getText());

            updates.put(actionDiaryUrl, action);
            updates.put(actionDiaryDataUrl + "/" + msg.getKey(), msg);
        }
        else {
            updates.put(actionDiaryDataUrl + "/" + msg.getKey(), null);

            mDatabase.child(actionDiaryDataUrl)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() <= 0) {
                                // user remove all messages associated with this ActionDiary
                                mDatabase.child(actionDiaryUrl).removeValue();
                            }
                        }
                        public void onCancelled(DatabaseError databaseError) { }
            });
        }
        mDatabase.updateChildren(updates);
    }


    public String sendMessage(MessageFB msg) {
        Log.d(TAG, "Send text MessageFB: " + msg);

        Map<String, Object> updates = new HashMap<>();
        final String newMessageUid = mChatMessages.push().getKey();

        // push a message into mChatMessages reference
        updates.put(mChatMessagesUrl + "/" + newMessageUid, msg);

        // update description and dataTime of action of this associated Chat
        updates.put(mActionUrl + "/" + Constraints.Actions.DESCRIPTION, msg.getText());
        updates.put(mActionUrl + "/" + Constraints.Actions.DATE_TIME, msg.getDateTime());

        mDatabase.updateChildren(updates);
        return newMessageUid;
    }

    public String sendMedia(final MessageFB media) {
        Log.d(TAG, "Send photoText MessageFB: " + media);

        final String newMessageUID = mChatMessages.push().getKey();

        Uri uriLocal;
        uriLocal = Uri.parse(media.getUriLocal());
        StorageReference photoRef = mStorageRef.child(Constraints.GALLERY_PHOTOS_DIRECTORY + "/" + mCoupleUid + "/" + uriLocal.getLastPathSegment());
        UploadTask uploadTask = photoRef.putFile(uriLocal);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "onFailure sendFileFirebase " + exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                final String stringUriStorage;
                stringUriStorage = downloadUrl.toString();
                media.setUriStorage(stringUriStorage);

                // push a message into mGalleryPhotos reference
                mChatMessages.child(newMessageUID).setValue(media);

                // update description and dataTime of action of this associated Gallery
                Map<String, Object> actionUpdates = new HashMap<>();
                actionUpdates.put("description","\uD83D\uDCF7"); //camera emoji
                actionUpdates.put("dataTime", media.getDateTime());
                mAction.updateChildren(actionUpdates);
            }
        }).addOnProgressListener(
                new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        //progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");

                        for (FirebaseChatController.ChatControllerListener listener : mListeners) {
                            listener.onUploadPercent(media, ((int) progress));
                        }
                    }
                });

        return newMessageUID;
    }

}
