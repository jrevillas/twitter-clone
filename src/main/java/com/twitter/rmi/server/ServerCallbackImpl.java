package com.twitter.rmi.server;

import com.twitter.rmi.common.ClientCallback;
import com.twitter.rmi.common.ServerCallback;
import com.twitter.rmi.common.User;
import com.twitter.rmi.database.Database;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jrevillas on 8/12/16.
 */
public class ServerCallbackImpl extends UnicastRemoteObject implements ServerCallback{

    public static HashMap<String,User> callbackHashMap = new HashMap<>();

    protected ServerCallbackImpl() throws RemoteException {
        super();
        callbackHashMap = new HashMap<>();
    }

    public static synchronized void notifyOnTweet(String user, String content) throws RemoteException {

        System.out.println("[CALLBACKS] Empezamos los Callbacks de: @" + user);

        List<User> followers = Database.getFollowers(user);

        System.out.println("[CALLBACKS] El usuario " + user + " tiene " + followers.size() + " seguidores.");

        for(int i = 0; i < followers.size(); i++) {
            ClientCallback toNotify = ServerLauncher.callbacks.get(followers.get(i).getHandle());
            if (toNotify != null) {
                System.out.println("[CALLBACKS] Intentando notificar a " + user);
                toNotify.notifyMe(followers.get(i).getHandle(), content);
            } else {
                System.out.println("[CALLBACKS] Imposible notificar a " + followers.get(i) + ": offline");
            }
        }
        System.out.println("[CALLBACKS] Terminados los callbacks de: @" + user);
    }

}
