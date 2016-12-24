package com.twitter.rmi.client;

import com.twitter.rmi.common.ClientCallback;
import com.twitter.rmi.server.ServerCallbackImpl;
import com.twitter.rmi.web.WebLauncher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by dmelero on 5/12/16.
 */
public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {

    public ClientCallbackImpl() throws RemoteException {
        super();
    }


    // TODO notificar por el websocket al usuario que corresponda.
    public String notifyMe(String username, String content){
        String returnMessage = "[RPC-CALLBACK] Callback recibido para " + username;
        System.out.println(returnMessage);
        WebLauncher.sendMessage(username, content);
        return returnMessage;
    }

}
