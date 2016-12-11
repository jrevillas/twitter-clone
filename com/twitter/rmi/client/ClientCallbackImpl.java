package com.twitter.rmi.client;

import com.twitter.rmi.common.ClientCallback;
import com.twitter.rmi.server.ServerCallbackImpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by dmelero on 5/12/16.
 */
public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {

    public ClientCallbackImpl() throws RemoteException {
        super();
    }

    public String notifyMe(String message){
        String returnMessage = "\nCallback recibido: " + message;
        System.out.println(returnMessage);
        return returnMessage;
    }

}
