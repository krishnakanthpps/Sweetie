package com.codernauti.sweetie.todolist;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codernauti.sweetie.R;


class ButtonViewHolder extends ToDoListViewHolder implements View.OnClickListener {

    private final TextView mAddText;
    private final ImageView mIcon;
    private final LinearLayout mAddLayout;
    private OnButtonViewHolderClickListener mListener;

    interface OnButtonViewHolderClickListener {
        void onAddButtonClicked();
    }


    ButtonViewHolder(View itemView) {
        super(itemView);
        mAddLayout = (LinearLayout) itemView.findViewById(R.id.todolist_add_layout);
        mIcon = (ImageView) itemView.findViewById(R.id.todolist_add_icon);
        mAddText = (TextView) itemView.findViewById(R.id.todolist_add_label);

        mAddLayout.setOnClickListener(this);
        mIcon.setOnClickListener(this);
        mAddText.setOnClickListener(this);
    }

    void setButtonViewHolderClickListener(OnButtonViewHolderClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        // same click for all views
        mListener.onAddButtonClicked();
    }
}
