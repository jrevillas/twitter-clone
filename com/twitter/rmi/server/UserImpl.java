package com.twitter.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import com.twitter.rmi.common.Status;
import com.twitter.rmi.common.User;

/**
 * Created by jrevillas on 06/12/2016.
 */
public class UserImpl extends UnicastRemoteObject implements User {

    protected UserImpl() throws RemoteException {
        super();
    }

    @Override
    public String getHandle() throws RemoteException {
        return null;
    }

    @Override
    public List<Status> getTimeline() throws RemoteException {
        return null;
    }

    @Override
    public void submitStatus(String content) throws RemoteException {
        return;
    }

    @Override
    public void updatePassword(String password) throws RemoteException {
        return;
    }

}
