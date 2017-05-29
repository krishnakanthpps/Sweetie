package com.sweetcompany.sweetie.Chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.sweetcompany.sweetie.Actions.ActionNewChatFragment;
import com.sweetcompany.sweetie.Actions.ActionsFragment;
import com.sweetcompany.sweetie.Actions.ActionsPresenter;
import com.sweetcompany.sweetie.R;
import com.sweetcompany.sweetie.Utils.Utility;

/**
 * Created by ghiro on 11/05/2017.
 */

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    // key for Intent extras
    public static final String CHAT_DATABASE_KEY = "ChatDatabaseKey";
    public static final String CHAT_TITLE = "ChatTitle";    // For offline user
    public static final String ACTION_FB_DATABASE_KEY = "ActionFbDatabaseKey";

    ChatPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        String actionKey = null;
        String chatKey = null;

        if (savedInstanceState == null) { // first Activity open
            Bundle chatBundle = getIntent().getExtras();
            if (chatBundle != null) {
                actionKey = chatBundle.getString(ACTION_FB_DATABASE_KEY);
                chatKey = chatBundle.getString(CHAT_DATABASE_KEY);

                Log.d(TAG, "from Intent ACTION_FB_DATABASE_KEY: " +
                        chatBundle.getString(ACTION_FB_DATABASE_KEY));
                Log.d(TAG, "from Intent CHAT_TITLE: " +
                        chatBundle.getString(CHAT_TITLE));
                Log.d(TAG, "from Intent CHAT_DATABASE_KEY: " +
                        chatBundle.getString(CHAT_DATABASE_KEY));
            }
            else {
                Log.w(TAG, "getIntent FAILED!");
            }
        } else {
            // TODO: restore data from savedInstanceState
        }

        ChatFragment view = (ChatFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.chat_fragment_container);

        if (view == null) {
            // TODO: not sure if ChatFragment need extras
            view = ChatFragment.newInstance(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.chat_fragment_container, view);
            transaction.commit();
        }

        String userMail = Utility.getStringPreference(this, Utility.MAIL);
        mPresenter = new ChatPresenter(view, userMail, chatKey, actionKey);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
