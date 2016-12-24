package com.twitter.rmi.client;

import com.twitter.rmi.common.Client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by dmelero on 5/12/16.
 */
public class ClientImpl extends UnicastRemoteObject implements Client {

    public ClientImpl() throws RemoteException {
        super();
    }

    public String notifyMe(String message){
        String returnMessage = "\nCallback recibido: " + message;
        System.out.println(returnMessage);
        return returnMessage;
    }

}
