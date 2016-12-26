package com.twitter.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.twitter.rmi.common.*;
import com.twitter.rmi.database.Database;

/**
 * Created by jrevillas on 06/12/2016.
 */
public class TwitterImpl extends UnicastRemoteObject implements Twitter {

    private static final String TWITTER_RMI_VERSION = "0.0.1";

    protected TwitterImpl() throws RemoteException {
        super();
    }

    @Override
    public String getVersion() throws RemoteException {
        return TWITTER_RMI_VERSION;
    }

    @Override
    public User login(String handle, String password) throws RemoteException{
        User user = Database.login(handle, password);
        if (user != null) {
            System.out.println(handle + " -> login(...)");
            return user;
        }
        return null;
    }

    @Override
    public User register(String handle, String password) throws RemoteException{
        User user = Database.register(handle, password);
        if (user != null) {
            System.out.println(handle + " -> register(...)");
            return user;
        }
        return null;
    }

}
