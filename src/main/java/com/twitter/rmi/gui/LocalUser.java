package com.twitter.rmi.gui;

/**
 * Created by migui on 12/01/17.
 */
public class LocalUser {
    private String handle;

    public String getHandle() {
        return handle;
    }

    LocalUser setHandle(String handle) {
        this.handle = handle;
        return this;
    }
}
