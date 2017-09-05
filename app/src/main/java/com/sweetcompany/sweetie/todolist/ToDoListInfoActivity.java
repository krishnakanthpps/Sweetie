package com.sweetcompany.sweetie.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.sweetcompany.sweetie.BaseActivity;
import com.sweetcompany.sweetie.R;
import com.sweetcompany.sweetie.actionInfo.ActionInfoFragment;
import com.sweetcompany.sweetie.actionInfo.ActionInfoPresenter;
import com.sweetcompany.sweetie.firebase.FirebaseActionInfoController;
import com.sweetcompany.sweetie.model.ToDoListFB;

/**
 * Created by Eduard on 05-Sep-17.
 */

public class ToDoListInfoActivity extends BaseActivity {

    private static final String TAG = "ChatInfoActivity";

    private static final String TODOLIST_UID_KEY = "toDoListUid";
    private static final String PARENT_ACTION_UID_KEY = "parentActionUid";

    private String mToDoListUid;
    private String mParentActionUid;

    private FirebaseActionInfoController<ToDoListFB> mController;
    private ActionInfoPresenter<ToDoListFB> mPresenter;

    public static Intent getStartActivityIntent(Context context, String galleryUid, String parentActionUid) {
        Intent intent = new Intent(context, ToDoListInfoActivity.class);
        intent.putExtra(TODOLIST_UID_KEY, galleryUid);
        intent.putExtra(PARENT_ACTION_UID_KEY, parentActionUid);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_info_activity);


        if (savedInstanceState == null) {   // first opened
            savedInstanceState = getIntent().getExtras();
        }

        if (savedInstanceState != null) {
            mToDoListUid = savedInstanceState.getString(TODOLIST_UID_KEY);
            mParentActionUid = savedInstanceState.getString(PARENT_ACTION_UID_KEY);

            Log.d(TAG, "ToDoListUid = " + mToDoListUid + "\n" + "ParentActionUid = " + mParentActionUid);
        }
        else {
            Log.w(TAG, "No savedInstanceState or intentArgs!");
        }


        ActionInfoFragment view = (ActionInfoFragment) getSupportFragmentManager()
                .findFragmentById(R.id.action_info_fragment_container);

        if (view == null) {
            view = ActionInfoFragment.newInstance(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.add(R.id.action_info_fragment_container, view);
            transaction.commit();
        }

        if (mToDoListUid != null) {
            mController = new FirebaseActionInfoController<>(super.mCoupleUid,
                    mParentActionUid, mToDoListUid, ToDoListFB.class);
            mPresenter = new ActionInfoPresenter<>(view, mController);
        } else {
            Log.w(TAG, "ToDOList Uid is null, impossible to instantiate Controller and presenter");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.attachListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mController.detachListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TODOLIST_UID_KEY, mToDoListUid);
        outState.putString(PARENT_ACTION_UID_KEY, mParentActionUid);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}