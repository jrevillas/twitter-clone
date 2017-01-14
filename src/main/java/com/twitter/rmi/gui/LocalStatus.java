package com.twitter.rmi.gui;

/**
 * Created by migui on 12/01/17.
 */
public class LocalStatus {
    private String body;
    private String userHandle;
    private String date;


    public String getBody() {
        return body;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public String getDate() {
        return date;
    }

    LocalStatus setBody(String body) {
        this.body = body;
        return this;
    }

    LocalStatus setUserHandle(String userHandle) {
        this.userHandle = userHandle;
        return this;
    }

    LocalStatus setDate(String date) {
        this.date = date;
        return this;
    }
}
