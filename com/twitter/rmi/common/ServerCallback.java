package com.twitter.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by dmelero on 8/12/16.
 */
public interface ServerCallback extends Remote {

    public void registerForCallback(String user, ClientCallback callbackClientObject) throws RemoteException;

    public void unregisterForCallback(String user, ClientCallback callbackClientObject) throws RemoteException;
}
