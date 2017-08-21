package com.sweetcompany.sweetie.chat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sweetcompany.sweetie.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ghiro on 16/05/2017.
 */

class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>
        implements MessageViewHolder.OnViewHolderClickListener {

    private static final String TAG = "ChatAdapter";

    interface ChatAdapterListener {
        void onBookmarkClicked(MessageVM messageVM, int type);
        void onPhotoClicked(TextPhotoMessageVM photoMessage);
    }

    private List<ChatItemVM> mMessageList = new ArrayList<>();
    private ChatAdapterListener mListener;

    /**
     * Call when create ChatAdapter or when destroy ChatAdapter, in this case pass null
     * @param listener
     */
    void setListener(ChatAdapterListener listener) {
        mListener = listener;
    }


    @Override
    public int getItemViewType(int position) {
        // use the unique id of the view for the type
        return mMessageList.get(position).getIdView();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //MessageVM message = mMessageList.get(viewType);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View viewToInflate = inflater.inflate(viewType, parent, false);
        ChatViewHolder viewHolder;
        switch (viewType) {

            case R.layout.chat_user_list_item_text:
            case R.layout.chat_partner_list_item_text:
                viewHolder = new TextMessageViewHolder(viewToInflate);
                ((MessageViewHolder) viewHolder).setViewHolderClickListener(this);
                break;

            case R.layout.chat_user_list_item_photo:
            case R.layout.chat_partner_list_item_photo:
                viewHolder = new TextPhotoMessageViewHolder(viewToInflate);
                ((MessageViewHolder) viewHolder).setViewHolderClickListener(this);
                break;

            case R.layout.chat_date_list_item:
                viewHolder = new DateViewHolder(viewToInflate);
                break;

            default:
                Log.w(TAG, "Error: no MessageViewHolder type match");
                // TODO: create a ErrorMessageViewHolder
                viewHolder = new TextMessageViewHolder(viewToInflate);
                break;
        }

        Log.d(TAG, "onCreateViewHolder(): " + viewHolder.toString());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatItemVM msgVM = mMessageList.get(position);
        msgVM.configViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    void addDateItem(DateItemVM date) {
        mMessageList.add(date);
        notifyItemInserted(mMessageList.size() - 1);
    }

    void addMessage(MessageVM message) {
        //TODO optimize change object field instead of remove
        if(searchIndexMessageOf(message) != -1){
            removeMessage(message);
        }
        mMessageList.add(message);
        notifyItemInserted(mMessageList.size() - 1);
    }

    void removeMessage(MessageVM msgVM) {
        int indexOldMessage = searchIndexMessageOf(msgVM);
        if (indexOldMessage != -1) {
            mMessageList.remove(indexOldMessage);
            notifyItemRemoved(indexOldMessage);
        }
    }

    void changeMessage(MessageVM msgVM) {
        int indexOldMessage = searchIndexMessageOf(msgVM);
        if (indexOldMessage != -1) {
            mMessageList.set(indexOldMessage, msgVM);
            notifyItemChanged(indexOldMessage);
        }
    }

    private int searchIndexMessageOf(MessageVM msg) {
        String modifyMsgKey = msg.getKey();
        for (int i = 0; i < mMessageList.size(); i++) {
            String msgKey = mMessageList.get(i).getKey();
            if (msgKey.equals(modifyMsgKey)) {
                return i;
            }
        }
        return -1;
    }

    void updateMessageList(List<MessageVM> messagesVM) {
        mMessageList.clear();
        mMessageList.addAll(messagesVM);
        Collections.reverse(mMessageList);
        this.notifyDataSetChanged();
    }

    void updatePercentUpload(MessageVM mediaVM, int perc){
        int indexOldMedia = searchIndexMessageOf(mediaVM);
        if (indexOldMedia != -1) {
            mediaVM.setPercent(perc);
            mMessageList.set(indexOldMedia, mediaVM);
            notifyItemChanged(indexOldMedia);
        }
    }


    // Callbacks from ViewHolders

    @Override
    public void onBookmarkClicked(int adapterPosition, boolean isBookmarked, int type) {
        // Only MessageViewHolder are listened, cast is secured
        MessageVM msgToUpdate = (MessageVM) mMessageList.get(adapterPosition);

        // Update MessageVM associate with ViewHolder
        msgToUpdate.setBookmarked(isBookmarked);

        mListener.onBookmarkClicked(msgToUpdate, type);
    }

    @Override
    public void onPhotoClicked(int adapterPosition) {
        mListener.onPhotoClicked((TextPhotoMessageVM) mMessageList.get(adapterPosition));
    }
}
