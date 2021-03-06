package com.codernauti.sweetie.chat;

import com.codernauti.sweetie.R;
import com.codernauti.sweetie.utils.DataMaker;

import java.io.Serializable;

public class TextPhotoMessageVM extends MessageVM implements Serializable {
    private String mText;
    private String mUriStorage;
    private int mPercent;

    TextPhotoMessageVM(String text, String creatorUid, boolean mainUser, String date,
                       boolean bookMarked, String key, String uriS) {
        super(mainUser, creatorUid, date, bookMarked, key);
        mText = text;
        mUriStorage = uriS;
    }

    String getText() {
        return mText;
    }

    void setUriStorage(String uriS){
        this.mUriStorage = uriS;
    }
    String getUriStorage() {
        return mUriStorage;
    }

    @Override
    void setPercent(int progress){
        this.mPercent = progress;
    }

    @Override
    public void configViewHolder(ChatViewHolder viewHolder) {
        // TODO: This downcast is secure?
        TextPhotoMessageViewHolder view = (TextPhotoMessageViewHolder) viewHolder;

        //view.setText(mText);
        view.setTextTime(DataMaker.getHH_ss_Local(super.getTime()));
        view.setBookmark(super.isBookmarked());
        view.setImage(mUriStorage);
        view.setPercentUploading(mPercent);
    }

    @Override
    public int getIdView() {
        if (isTheMainUser()) {
            return R.layout.chat_user_list_item_photo;
        }
        else {  // isThePartner()
            return R.layout.chat_partner_list_item_photo;
        }
    }
}