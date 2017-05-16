package com.sweetcompany.sweetie.Actions;


import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sweetcompany.sweetie.Chat.ChatActivity;
import com.sweetcompany.sweetie.IPageChanger;
import com.sweetcompany.sweetie.R;

import java.util.List;

public class ActionsFragment extends Fragment implements ActionsContract.View {

    private ActionsAdapter mActionAdapter;
    private RecyclerView mActionsListView;

    private FloatingActionButton mFabNewAction;
    private FloatingActionButton mFabNewChatAction;
    private FloatingActionButton mFabNewPhotoAction;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private boolean isFabOpen = false;

    private ActionsContract.Presenter mPresenter;

    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionAdapter = new ActionsAdapter();
        mContext = getContext();

        //set animations
        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(mContext, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(mContext ,R.anim.rotate_backward);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.actions_fragment, container, false);

        mActionsListView = (RecyclerView) root.findViewById(R.id.actions_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mActionsListView.setLayoutManager(layoutManager);
        mActionsListView.setAdapter(mActionAdapter);

        mFabNewAction = (FloatingActionButton) root.findViewById(R.id.fab_new_action);

        mFabNewChatAction = (FloatingActionButton) root.findViewById(R.id.fab_new_chat);
        mFabNewChatAction.setClickable(false);

        mFabNewPhotoAction = (FloatingActionButton) root.findViewById(R.id.fab_new_photo);
        mFabNewPhotoAction.setClickable(false);


        // Add listener
        mFabNewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show others action fab
                animateFAB();
            }
        });

        mFabNewChatAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide others action fab
                animateFAB();
                DialogFragment dialogFragment = ActionNewChatFragment.newInstance();
                dialogFragment.show(getActivity().getFragmentManager(), ActionNewChatFragment.TAG);
            }
        });

        return root;
    }

    public void animateFAB(){

        if(isFabOpen){
            mFabNewAction.startAnimation(rotate_backward);
            mFabNewChatAction.startAnimation(fab_close);
            mFabNewPhotoAction.startAnimation(fab_close);
            mFabNewChatAction.setClickable(false);
            mFabNewChatAction.setClickable(false);
            isFabOpen = false;
            mActionsListView.setAlpha(1.0f);
        } else {
            mFabNewAction.startAnimation(rotate_forward);
            mFabNewChatAction.startAnimation(fab_open);
            mFabNewPhotoAction.startAnimation(fab_open);
            mFabNewChatAction.setClickable(true);
            mFabNewPhotoAction.setClickable(true);
            isFabOpen = true;
            mActionsListView.setAlpha(0.5f);
        }
    }

    @Override
    public void setPresenter(ActionsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void updateActionsList(List<ActionVM> actionsVM) {
        for(ActionVM actionVM : actionsVM) {
            actionVM.setPageChanger((IPageChanger)getActivity());
            actionVM.setContext(getContext());
        }

        mActionAdapter.updateActionsList(actionsVM);
    }
}
