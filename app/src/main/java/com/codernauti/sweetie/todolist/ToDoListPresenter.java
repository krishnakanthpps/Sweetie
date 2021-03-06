package com.codernauti.sweetie.todolist;

import com.codernauti.sweetie.firebase.FirebaseToDoListController;
import com.codernauti.sweetie.model.CheckEntryFB;
import com.codernauti.sweetie.model.ToDoListFB;
import com.codernauti.sweetie.utils.DataMaker;


class ToDoListPresenter implements ToDoListContract.Presenter, FirebaseToDoListController.Listener {


    private static final String TAG = "ToDoListPresenter";

    private ToDoListContract.View mView;
    private FirebaseToDoListController mController;
    private String mUserUid;   // id of checkEntries of main user

    ToDoListPresenter(ToDoListContract.View view, FirebaseToDoListController controller, String userUid){
        mView = view;
        mView.setPresenter(this);
        mController = controller;
        mController.addListener(this);

        mUserUid = userUid;
    }

    @Override
    public void addCheckEntry(CheckEntryVM checkEntry) {
        CheckEntryFB newCheckEntry = new CheckEntryFB(mUserUid, checkEntry.getText(),
                checkEntry.isChecked(),checkEntry.getTime());

        mController.addCheckEntry(newCheckEntry);
    }

    @Override
    public void removeCheckEntry(String key) {
        mController.removeCheckEntry(key);
    }

    @Override
    public void changeCheckEntry(CheckEntryVM checkEntry) {
        CheckEntryFB updateCheckEntry = new CheckEntryFB(mUserUid, checkEntry.getText(),
                checkEntry.isChecked(), DataMaker.get_UTC_DateTime());
        updateCheckEntry.setKey(checkEntry.getKey());

        mController.updateCheckEntry(updateCheckEntry);
    }

    @Override
    public void checkedCheckEntry(CheckEntryVM checkEntry) {
        CheckEntryFB updateCheckEntry = new CheckEntryFB(mUserUid, checkEntry.getText(),
                checkEntry.isChecked(),checkEntry.getTime());
        updateCheckEntry.setKey(checkEntry.getKey());

        mController.updateCheckEntry(updateCheckEntry);
    }


    @Override
    public void onToDoListChanged(ToDoListFB todolist) {
        ToDoListVM toDoListVM = new ToDoListVM(todolist.getKey(), todolist.getTitle());
        mView.updateToDoListInfo(toDoListVM);
    }

    @Override
    public void onCheckEntryAdded(CheckEntryFB checkEntry) {
        boolean who = CheckEntryVM.THE_PARTNER;
        if (checkEntry.getUserUid().equals(mUserUid)) {
            who = CheckEntryVM.THE_MAIN_USER;
        }
        CheckEntryVM checkEntryVM = new CheckEntryVM(who,checkEntry.getKey(),checkEntry.getText(),
                checkEntry.getDateTime(),checkEntry.isChecked());
        mView.addCheckEntry(checkEntryVM);
    }

    @Override
    public void onCheckEntryRemoved(CheckEntryFB checkEntry) {
        boolean who = CheckEntryVM.THE_PARTNER;
        if (checkEntry.getUserUid().equals(mUserUid)) {
            who = CheckEntryVM.THE_MAIN_USER;
        }
        CheckEntryVM checkEntryVM = new CheckEntryVM(who,checkEntry.getKey(),checkEntry.getText(),
                checkEntry.getDateTime(),checkEntry.isChecked());
        mView.removeCheckEntry(checkEntryVM);
    }

    @Override
    public void onCheckEntryChanged(CheckEntryFB checkEntry) {
        boolean who = CheckEntryVM.THE_PARTNER;
        if (checkEntry.getUserUid().equals(mUserUid)) {
            who = CheckEntryVM.THE_MAIN_USER;
        }
        CheckEntryVM checkEntryVM = new CheckEntryVM(who,checkEntry.getKey(),checkEntry.getText(),
                checkEntry.getDateTime(),checkEntry.isChecked());
        mView.changeCheckEntry(checkEntryVM);
    }
}
