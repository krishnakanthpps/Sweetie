package com.sweetcompany.sweetie.Chat;

/**
 * Created by ghiro on 16/05/2017.
 */

class TextMessageVM {

    private String mText;
    private boolean mMe; //is me?

    public TextMessageVM(String text, boolean me) {
        mText = text;
        mMe = me;
    }

    public String getText() {
        return mText;
    }
}

