package com.twitter.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.twitter.rmi.common.Status;

/**
 * Created by jruiz on 06/12/2016.
 */
public class StatusImpl extends UnicastRemoteObject implements Status {

    private Long postId;
    private String userHandle;
    private String body;
    private String date;

    public StatusImpl() throws RemoteException {
        super();
    }

    @Override
    public String getDate() throws RemoteException{
        return this.date;
    }

    public StatusImpl setDate(String date) {
        this.date = date;
        return this;
    }

    public Long getPostId() throws RemoteException {
        return this.postId;
    }

    public StatusImpl setPostId(Long postId) throws RemoteException {
        this.postId = postId;
        return this;
    }

    @Override
    public String getUserHandle() throws RemoteException {
        return this.userHandle;
    }

    public StatusImpl setUserHandle (String userHandle) throws RemoteException {
        this.userHandle = userHandle;
        return this;
    }

    @Override
    public String getBody() throws RemoteException {
        return this.body;
    }

    public StatusImpl setBody(String body) throws RemoteException {
        this.body = body;
        return this;
    }

}
