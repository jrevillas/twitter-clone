package com.twitter.rmi.gui;

import com.twitter.rmi.common.ClientCallback;
import com.twitter.rmi.common.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by migui on 12/01/17.
 */
public class GUICallbackImpl extends UnicastRemoteObject implements ClientCallback {

    private GUI gui;
    private User activeUser;

    GUICallbackImpl() throws RemoteException {}

    GUICallbackImpl setThings(GUI gui, User activeUser){
        this.gui = gui;
        this.activeUser = activeUser;
        return this;
    }

    GUICallbackImpl subscribe(){
        try {
            activeUser.pushSubscribe(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return this;
    }

    void unsubscribe(){
        try {
            activeUser.pushUnsubscribe(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String notifyMe(String username, String msg) throws RemoteException {
        System.out.println(username);
        System.out.println(msg);
        gui.notifyStatus(username);
        return null;
    }
}
