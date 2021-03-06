package com.codernauti.sweetie.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.codernauti.sweetie.utils.DataMaker;
import com.codernauti.sweetie.utils.SharedPrefKeys;
import com.codernauti.sweetie.model.CoupleFB;
import com.codernauti.sweetie.model.PairingRequestFB;
import com.codernauti.sweetie.model.UserFB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class FirebasePairingController {
    private static final String TAG = "FbPairingController";

    private List<PairingControllerListener> mListeners = new ArrayList<>();
    private NewPairingListener mActivityListener;

    private final String mUserUid;
    private final String mUserUsername;
    private final String mUserImageUri;

    private final DatabaseReference mUserPairingRequests;
    private final DatabaseReference mUsers;
    private final DatabaseReference mDatabase;

    private ValueEventListener mUserPairingRequestsListener;
    private ValueEventListener mUsersEqualToListener;

    public interface NewPairingListener {
        void onCreateNewPairingRequestComplete(String futurePartnerUid);
    }

    public interface PairingControllerListener {
        void onDownloadPairingRequestsCompleted(List<PairingRequestFB> userPairingRequests);
        void onSearchUserWithPhoneNumberFinished(UserFB user);
        void onCreateNewCoupleComplete();
        void onCreateNewPairingRequestComplete();
    }


    public FirebasePairingController(String userUid, String userUsername, String userImageUri) {
        mUserUid = userUid;
        mUserUsername = userUsername;
        mUserImageUri = userImageUri;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();

        mUserPairingRequests = database.getReference().child(Constraints.PAIRING_REQUESTS).child(mUserUid);
        mUsers = database.getReference().child(Constraints.USERS);

        mUserPairingRequests.keepSynced(true);
    }


    public void addListener(PairingControllerListener listener) {mListeners.add(listener);}

    public void removeListener(PairingControllerListener listener) {mListeners.remove(listener);}

    public void setPairingListener(NewPairingListener listener) { mActivityListener = listener; }


    public void detachListeners() {
        if (mUserPairingRequestsListener != null) {
            mUserPairingRequests.removeEventListener(mUserPairingRequestsListener);
        }
        mUserPairingRequestsListener = null;

        if (mUsersEqualToListener != null) {
            mUsers.removeEventListener(mUsersEqualToListener);
        }
        mUsersEqualToListener = null;
    }


    public void downloadPairingRequest() {
        mUserPairingRequestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());

                List<PairingRequestFB> userPairingRequests = new ArrayList<>();
                for (DataSnapshot pairingRequestData : dataSnapshot.getChildren()) {
                    PairingRequestFB pairingRequest = pairingRequestData.getValue(PairingRequestFB.class);
                    pairingRequest.setKey(pairingRequestData.getKey());
                    userPairingRequests.add(pairingRequest);
                }

                for (PairingControllerListener listener : mListeners) {
                    listener.onDownloadPairingRequestsCompleted(userPairingRequests);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        // N.B. we use a addForSingleValueEvent because
        // if we use addForValueEventListener when we remove the request this listener start a callback
        mUserPairingRequests.addListenerForSingleValueEvent(mUserPairingRequestsListener);
    }

    public void createNewCouple(PairingRequestFB partnerPairingRequest) {
        String now = DataMaker.get_UTC_DateTime();
        String partnerUid = partnerPairingRequest.getKey();
        String partnerUsername = partnerPairingRequest.getUsername();
        String partnerImageUri = partnerPairingRequest.getImageUri();
        CoupleFB newCouple = new CoupleFB(mUserUid, partnerUid, mUserUsername, partnerUsername, now);

        DatabaseReference newCoupleRef = mDatabase.child(Constraints.COUPLES).push();
        String newCoupleKey = newCoupleRef.getKey();

        Map<String, Object> updates = new HashMap<>();

        removeAcceptedPairingRequest(updates, partnerUid);

        addNewCouple(updates, newCouple, newCoupleKey);

        updateUsersCoupleInfo(updates, newCoupleKey, partnerUid, partnerUsername, partnerImageUri);

        updateUserFuturePartner(updates, partnerUid);

        mDatabase.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                for (PairingControllerListener listener : mListeners) {
                    listener.onCreateNewCoupleComplete();
                }
            }
        });
    }

    private void removeAcceptedPairingRequest(Map<String, Object> updates, String partnerUid) {
        // remove the pairing request received
        // pairing-request/<mUserUid>/<partnerUid>
        String userPairingRequestUrl = Constraints.PAIRING_REQUESTS + "/" +
                mUserUid + "/" +
                partnerUid;
        updates.put(userPairingRequestUrl, null);
    }

    private void addNewCouple(Map<String, Object> updates, CoupleFB newCouple, String newCoupleKey) {
        // push the new couple: couples/<newCoupleUid>
        String newCoupleUrl = Constraints.COUPLES + "/" + newCoupleKey;
        updates.put(newCoupleUrl, newCouple);
    }

    private void updateUsersCoupleInfo(Map<String, Object> updates, String newCoupleKey,
                                       String partnerUid, String partnerUsername, String partnerImageUri) {

        // users/<userUid>/coupleInfo
        String userCoupleInfoUrl = Constraints.USERS + "/" +
                mUserUid + "/" +
                Constraints.COUPLE_INFO;

        updates.put(userCoupleInfoUrl + "/" + Constraints.ACTIVE_COUPLE, newCoupleKey);
        updates.put(userCoupleInfoUrl + "/" + Constraints.PARTNER_USERNAME, partnerUsername);
        updates.put(userCoupleInfoUrl + "/" + Constraints.PARTNER_IMAGE_URI, partnerImageUri);

        // users/<partnerUid>/coupleInfo
        String partnerCoupleInfoUrl = Constraints.USERS + "/" +
                partnerUid + "/" +
                Constraints.COUPLE_INFO;

        updates.put(partnerCoupleInfoUrl + "/" + Constraints.ACTIVE_COUPLE, newCoupleKey);
        updates.put(partnerCoupleInfoUrl + "/" + Constraints.PARTNER_USERNAME, mUserUsername);
        updates.put(partnerCoupleInfoUrl + "/" + Constraints.PARTNER_IMAGE_URI, mUserImageUri);
    }

    private void updateUserFuturePartner(Map<String, Object> updates, String partnerUid) {
        // users/<userUid>/futurePartner
        String userFuturePartnerUrl = Constraints.USERS + "/"
                + mUserUid + "/"
                + Constraints.FUTURE_PARTNER;
        updates.put(userFuturePartnerUrl, partnerUid);
    }


    public void searchUserWithPhoneNumber(final String phonePartner) {
        if (mUsersEqualToListener == null) {
            mUsersEqualToListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // only one user could have that phone number
                    Iterator<DataSnapshot> resultIterator = dataSnapshot.getChildren().iterator();
                    if (resultIterator.hasNext()) {
                        DataSnapshot userDataSnapshot = resultIterator.next();
                        Log.d(TAG, userDataSnapshot.toString());

                        UserFB user = userDataSnapshot.getValue(UserFB.class);
                        user.setKey(userDataSnapshot.getKey());

                        for (PairingControllerListener listener : mListeners) {
                            listener.onSearchUserWithPhoneNumberFinished(user);
                        }
                    }
                    else {
                        Log.d(TAG, "No user with that phone number.");
                        for (PairingControllerListener listener : mListeners) {
                            listener.onSearchUserWithPhoneNumberFinished(null);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            };
            mUsers.orderByChild("phone").equalTo(phonePartner).addListenerForSingleValueEvent(mUsersEqualToListener);
        }
    }

    public void createNewPairingRequest(final UserFB futurePartner, String userUsername, String userImageUri,
                                        String userPhoneNumber, String oldPairingRequestedUserUid) {
        PairingRequestFB newRequest = new PairingRequestFB(userUsername, userPhoneNumber, userImageUri);

        Map<String, Object> updates = new HashMap<>();

        if (!oldPairingRequestedUserUid.equals(SharedPrefKeys.DEFAULT_VALUE)) {
            // remove previous pairing request send by mUserUid
            // pairing-request/<oldPairingRequestUserUid>/<mUserUid>
            String oldUserPairingRequestUrl =   Constraints.PAIRING_REQUESTS + "/" +
                                                oldPairingRequestedUserUid + "/" +
                    mUserUid;
            updates.put(oldUserPairingRequestUrl, null);
        }

        // pairing-request/<futurePartner.getKey()>/<mUserUid>
        String receiverPairingRequestsUrl =     Constraints.PAIRING_REQUESTS + "/" +
                                                futurePartner.getKey() + "/" +
                mUserUid;
        updates.put(receiverPairingRequestsUrl, newRequest);

        // mUserUid push a pairing-request to user.getKey
        // users/<mUserUid>/futurePartner
        String userFuturePartnerUrl =   Constraints.USERS + "/" +
                mUserUid + "/" +
                                        Constraints.FUTURE_PARTNER;
        updates.put(userFuturePartnerUrl, futurePartner.getKey());

        mDatabase.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mActivityListener.onCreateNewPairingRequestComplete(futurePartner.getKey());

                for (PairingControllerListener listener : mListeners) {
                    listener.onCreateNewPairingRequestComplete();
                }
            }
        });
    }

}
