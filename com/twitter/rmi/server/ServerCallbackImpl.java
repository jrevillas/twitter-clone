package com.twitter.rmi.server;

import com.twitter.rmi.common.ClientCallback;
import com.twitter.rmi.common.ServerCallback;
import com.twitter.rmi.common.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dmelero on 8/12/16.
 */
public class ServerCallbackImpl extends UnicastRemoteObject implements ServerCallback{

    public static HashMap<String,ClientCallback> callbackHashMap = new HashMap<>();

    protected ServerCallbackImpl() throws RemoteException {
        super();
        callbackHashMap = new HashMap<>();
    }

    @Override
    public synchronized void registerForCallback(String user, ClientCallback callbackClientObject)
            throws RemoteException {

        if(!callbackHashMap.containsKey(user)){
            callbackHashMap.put(user,callbackClientObject);
            System.out.println("@" + user + " se ha registrado en el Callback");
        }
    }

    @Override
    public synchronized void unregisterForCallback(String user, ClientCallback callbackClientObject)
            throws RemoteException {

        if(callbackHashMap.remove(user,callbackClientObject)){
            System.out.println("@" + user + " se ha desregistrado para el Callback");
        } else {
            System.out.println("Fallo: Cliente no registrado");
        }
    }

    public static synchronized void doCallbacks(String user, String content) throws RemoteException {

        System.out.println("Empezamos los Callbacks de: @" + user);

        List<User> followers = Controler.getFollowers(user);

        for(int i = 0; i < followers.size(); i++){
            callbackHashMap.get(followers.get(i).getHandle()).notifyMe("\n" +
                    "++++++++++++++++++++++++++++++\n" +
                    "Status de: @" + user + "\n" +
                    "Contenido: " + content + "\n" +
                    "++++++++++++++++++++++++++++++\n");
        }

        System.out.println("Terminamos los Callbacks de: @" + user + "\n");
    }

}
