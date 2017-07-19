package com.sweetcompany.sweetie;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sweetcompany.sweetie.actions.ActionsContract;
import com.sweetcompany.sweetie.actions.ActionsPresenter;
import com.sweetcompany.sweetie.Calendar.CalendarFragment;
import com.sweetcompany.sweetie.firebase.FirebaseActionsController;
import com.sweetcompany.sweetie.Folders.FoldersFragment;
import com.sweetcompany.sweetie.actions.ActionsFragment;
import com.sweetcompany.sweetie.Map.MapFragment;


public class DashboardPagerAdapter extends FragmentPagerAdapter {

    private final static int NUM_TAB = 4;
    private final static int CALENDAR_TAB = 0;
    public final static int HOME_TAB = 1;
    private final static int FOLDERS_TAB = 2;
    private final static int MAP_TAB = 3;

    // For getString From Resource
    private final Context mContext;

    private final FirebaseActionsController mActionsController;
    private ActionsContract.Presenter mActionsPresenter;

    DashboardPagerAdapter(FragmentManager fm, Context context, FirebaseActionsController actionsController) {
        super(fm);
        mContext = context;
        mActionsController = actionsController;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case CALENDAR_TAB:
                return new CalendarFragment();
            case HOME_TAB:
                ActionsFragment view = new ActionsFragment();
                mActionsPresenter = new ActionsPresenter(view, mActionsController);
                return view;
            case FOLDERS_TAB:
                return new FoldersFragment();
            case MAP_TAB:
                return new MapFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_TAB;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case CALENDAR_TAB:
                return mContext.getString(R.string.calendar_tab_name);
            case HOME_TAB:
                return mContext.getString(R.string.home_tab_name);
            case FOLDERS_TAB:
                return mContext.getString(R.string.folders_tab_name);
            //case MAP_TAB:
                //return context.getString(R.string.map_tab_name);
        }
        return null;
    }


}
