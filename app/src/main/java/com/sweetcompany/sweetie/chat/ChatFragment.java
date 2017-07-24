package com.sweetcompany.sweetie.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sweetcompany.sweetie.R;
import com.sweetcompany.sweetie.utils.DataMaker;

import java.text.ParseException;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconsView;

/**
 * Created by ghiro on 11/05/2017.
 */

public class ChatFragment extends Fragment implements ChatContract.View, View.OnClickListener,
        ChatAdapter.ChatAdapterListener {

    private static final String TAG = "ChatFragment";

    private Toolbar mToolBar;
    private RecyclerView mChatListView;
    private LinearLayoutManager mLinearLayoutManager;
    // TODO: implement PhotoPicker
    private ImageButton mEmoticonsButton;
    private EditText mTextMessageInput;
    private Button mSendButton;
    private EmojiconsView mEmoticonsView;

    private ChatAdapter mChatAdapter;
    private ChatContract.Presenter mPresenter;


    public static ChatFragment newInstance(Bundle bundle) {
        ChatFragment newChatFragment = new ChatFragment();
        newChatFragment.setArguments(bundle);

        return newChatFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChatAdapter = new ChatAdapter();
        mChatAdapter.setChatAdapterListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.chat_fragment, container, false);

        String titleChat = getArguments().getString(ChatActivity.CHAT_TITLE);
        Log.d(TAG, "from Intent CHAT_TITLE: " +
                getArguments().getString(ChatActivity.CHAT_TITLE));
        Log.d(TAG, "from Intent CHAT_DATABASE_KEY: " +
                getArguments().getString(ChatActivity.CHAT_DATABASE_KEY));

        mChatListView = (RecyclerView) root.findViewById(R.id.chat_list);

        mToolBar = (Toolbar) root.findViewById(R.id.chat_toolbar);
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        parentActivity.getSupportActionBar().setTitle(titleChat);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);

        mChatListView.setLayoutManager(mLinearLayoutManager);
        mChatListView.setAdapter(mChatAdapter);

        mTextMessageInput = (EditText) root.findViewById(R.id.chat_text_message_input);
        mEmoticonsButton = (ImageButton) root.findViewById(R.id.chat_emoticons_button);
        mSendButton = (Button) root.findViewById(R.id.chat_send_button);
        mEmoticonsView = new EmojiconsView(getActivity());
        mEmoticonsView.setId(View.generateViewId());
        mEmoticonsView.setVisibility(View.GONE);


        /*
        <io.github.rockerhieu.emojicon.EmojiconsView
            android:id="@+id/chat_emoticons_container"
            android:layout_width="match_parent"
            android:layout_height="240dp"
            android:layout_alignParentBottom="true"
            android:visibility="gone" />
        */

        // create layout params (compatible with RelativeLayout)
        // transform pixel of 240dp
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        mEmoticonsView.setLayoutParams(params);

        // root is the RelativeLaout of chat_fragment layout
        ((RelativeLayout)root).addView(mEmoticonsView);



        // android:layout_above="@+id/chat_emoticons_container"
        LinearLayout inputLayout = (LinearLayout) root.findViewById(R.id.chat_user_input_panel);
        RelativeLayout.LayoutParams paramsInput = (RelativeLayout.LayoutParams) inputLayout.getLayoutParams();
        paramsInput.addRule(RelativeLayout.ABOVE, mEmoticonsView.getId());

        mTextMessageInput.setOnClickListener(this);
        mEmoticonsButton.setOnClickListener(this);
        mSendButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void setPresenter(ChatContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void updateMessages(List<MessageVM> messagesVM) {
        mChatAdapter.updateMessageList(messagesVM);
    }

    @Override
    public void updateChatInfo(ChatVM chat) {
        mToolBar.setTitle(chat.getTitle());
    }

    @Override
    public void updateMessage(MessageVM msgVM) {
        mChatAdapter.addMessage(msgVM);
        mChatListView.scrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    @Override
    public void removeMessage(MessageVM msgVM) {
        mChatAdapter.removeMessage(msgVM);
    }

    @Override
    public void changeMessage(MessageVM msgVM) {
        mChatAdapter.changeMessage(msgVM);
    }


    // Callback bottom input bar
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_text_message_input:
                mEmoticonsView.setVisibility(View.GONE);
                break;
            case R.id.chat_send_button:
                String inputText = mTextMessageInput.getText().toString();
                mTextMessageInput.setText("");

                if (!inputText.isEmpty()) {
                    // TODO: is this responsibility of fragment?
                    MessageVM newMessage = null;
                    try {
                        newMessage = new TextMessageVM(inputText, MessageVM.THE_MAIN_USER, DataMaker.get_UTC_DateTime(), false, null);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // TODO: update view to feedback user if he is offline
                    //mChatAdapter.addMessage(newMessage);
                    mPresenter.sendMessage(newMessage);
                }
                break;
            case R.id.chat_emoticons_button:
                //TODO: show emoji view
                if (mEmoticonsView.getVisibility() == View.GONE) {
                    View view = getActivity().getCurrentFocus();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    mEmoticonsView.setVisibility(View.VISIBLE);
                }
                else {
                    mEmoticonsView.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }

    }

    // ChatAdapter callback
    @Override
    public void onBookmarkClicked(MessageVM messageVM) {
        // TODO: is this responsibility of fragment?
        mPresenter.bookmarkMessage(messageVM);
    }
}
