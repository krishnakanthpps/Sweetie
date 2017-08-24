package com.sweetcompany.sweetie.gallery;

/**
 * Created by ghiro on 22/07/2017.
 */

class GalleryVM {
    private String mKey;
    private String mTitle;
    private String mLat;
    private String mLon;
    private String mUriCover;

    GalleryVM(String key, String title) {
        mKey = key;
        mTitle = title;
    }

    String getTitle() {
        return mTitle;
    }

    public String getmLat() {
        return mLat;
    }

    public void setmLat(String mLat) {
        this.mLat = mLat;
    }

    public String getmLon() {
        return mLon;
    }

    public void setmLon(String mLon) {
        this.mLon = mLon;
    }

    public String getmUriCover() {
        return mUriCover;
    }

    public void setmUriCover(String mUriCover) {
        this.mUriCover = mUriCover;
    }
}
