package com.twitter.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.twitter.rmi.common.Status;

/**
 * Created by jrevillas on 06/12/2016.
 */
public class StatusImpl extends UnicastRemoteObject implements Status {

    protected StatusImpl() throws RemoteException {
        super();
    }

    @Override
    public String getContent() throws RemoteException {
        return null;
    }

    @Override
    public String getUserHandle() throws RemoteException {
        return null;
    }

}
