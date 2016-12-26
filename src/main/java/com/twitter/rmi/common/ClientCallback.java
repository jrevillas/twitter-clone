package com.twitter.rmi.common;

import com.twitter.rmi.client.ClientCallbackImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by dmelero on 5/12/16.
 */
public interface ClientCallback extends Remote {

    public String notifyMe(String username, String msg) throws RemoteException;

}
