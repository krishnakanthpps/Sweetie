package com.sweetcompany.sweetie.Actions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sweetcompany.sweetie.Chat.ChatActivity;
import com.sweetcompany.sweetie.DashboardActivity;
import com.sweetcompany.sweetie.R;

/**
 * Created by Eduard on 13-May-17.
 */

// TODO: decide if use DialogFragment of this class or go to ChatsActivity
public class ActionNewChatFragment extends DialogFragment {

    public static final String TAG = "ActionNewChatFragment";

    private static final String USER_POSITIVE_RESPONSE = "Ok";
    private static final String USER_NEGATIVE_RESPONSE = "Cancel";

    private EditText mTitleChatEditText;


    static ActionNewChatFragment newInstance() {
        ActionNewChatFragment fragment = new ActionNewChatFragment();

        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String dialogTitle = getString(R.string.action_new_chat_title_dialog);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogLayout = inflater.inflate(R.layout.action_new_chat_dialog, null);
        mTitleChatEditText = (EditText) dialogLayout.findViewById(R.id.action_new_chat_title);

        return new AlertDialog.Builder(getActivity())
                //.setIcon(R.drawable.alert_dialog_icon)
                .setView(dialogLayout)
                .setTitle(dialogTitle)
                .setPositiveButton(USER_POSITIVE_RESPONSE,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String userInputChatTitle = mTitleChatEditText.getText().toString();

                                if (!userInputChatTitle.isEmpty()) {
                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("ChatTitle", userInputChatTitle);

                                    startActivity(intent);
                                }
                            }
                        }
                )
                .setNegativeButton(USER_NEGATIVE_RESPONSE,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();

    }
}
