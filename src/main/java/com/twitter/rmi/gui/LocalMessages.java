package com.twitter.rmi.gui;

/**
 * Created by migui on 12/01/17.
 */
public class LocalMessages {
    private String sender;
    private String receiver;
    private String date;
    private String body;

    public String getSender() {
        return sender;
    }
    public String getDate() {
        return date;
    }
    public String getReceiver() {
        return receiver;
    }
    public String getBody() {
        return body;
    }

    LocalMessages setSender(String sender) {
        this.sender = sender;
        return this;
    }

    LocalMessages setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    LocalMessages setDate(String date) {
        this.date = date;
        return this;
    }

    LocalMessages setBody(String body) {
        this.body = body;
        return this;
    }
}
