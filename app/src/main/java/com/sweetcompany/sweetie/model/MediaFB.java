package com.sweetcompany.sweetie.model;

import android.net.Uri;

import com.google.firebase.database.Exclude;

/**
 * Created by ghiro on 22/07/2017.
 */

public class MediaFB {
    @Exclude
    private String key;

    private String email;   //TODO: add a user identifier
    private String description;
    private String dateTime;
    //private String encode;
    private String uri;
    private boolean bookmarked;

    // For firebase serialization
    public MediaFB() {}

    public MediaFB(String email, String desc, String date, boolean bookmarked, String uri) {
        this.email = email;
        this.description = desc;
        this.dateTime = date;
        this.bookmarked = bookmarked;
        //this.encode = encode;
        this.uri = uri;
    }


    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getText() {
        return description;
    }
    public void setText(String desc) {
        this.description = desc;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    // TODO: for firebase, remove in future
    public String getDate() { return dateTime; }
    public void setDate(String date) { this.dateTime = date; }

    public boolean isBookmarked() {
        return bookmarked;
    }
    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String getUri() {
        return uri;
    }
    public void setUri(String uri){
        this.uri = uri;
    }

    @Exclude
    @Override
    public String toString() {
        return "{" +
                " key: " + key +
                " email: " + email +
                " text: " + description +
                " dateTime: " + dateTime +
                " bookmarked: " + bookmarked +
                "}";
    }
}
