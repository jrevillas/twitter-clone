package com.twitter.rmi.server;

import com.twitter.rmi.common.PrivateMessage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by jruiz on 12/24/16.
 */
public class PrivateMessageImpl extends UnicastRemoteObject implements PrivateMessage {

    private String body;
    private String sender;
    private String date;
    private String receiver;
    private Long pmId;

    public PrivateMessageImpl() throws RemoteException {
        super();
    }

    @Override
    public Long getPostIdPm() throws RemoteException{
        return this.pmId;
    }

    public PrivateMessageImpl setPostId(Long postIdPm) {
        this.pmId = postIdPm;
        return this;
    }

    @Override
    public String getSender() throws RemoteException{
        return sender;
    }

    public PrivateMessageImpl setSender(String sender) {
        this.sender = sender;
        return this;
    }

    @Override
    public String getDate() throws RemoteException{
        return date;
    }

    public PrivateMessageImpl setDate(String date) {
        this.date = date;
        return this;
    }

    @Override
    public String getReceiver() throws RemoteException{
        return receiver;
    }

    public PrivateMessageImpl setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    @Override
    public String getBody() throws RemoteException{
        return this.body;
    }

    public PrivateMessageImpl setBody(String body) {
        this.body = body;
        return this;
    }


}
