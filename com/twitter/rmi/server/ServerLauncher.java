package com.twitter.rmi.server;

import com.twitter.rmi.common.Client;
import com.twitter.rmi.database.Database;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jrevillas on 06/12/2016.
 */
public class ServerLauncher {

    private static final String REGISTRY_ENDPOINT = "localhost";
    public static HashMap<String,Client> callbackHashMap = null;

    static {
        callbackHashMap = new HashMap<>();
    }

    public static synchronized void statusNotification(String user, String content) throws RemoteException {

        List<String> followers = Database.getFollowers(user);

        for(int i = 0; i < followers.size(); i++){
            callbackHashMap.get(followers.get(i)).notifyMe("\n" +
                    "++++++++++++++++++++++++++++++\n" +
                    "Status de: @" + user + "\n" +
                    "Contenido: " + content + "\n" +
                    "++++++++++++++++++++++++++++++\n");
        }
    }

    public static synchronized void followNotificacion(String following, String follower) throws RemoteException {

        callbackHashMap.get(following).notifyMe("\n" +
                "++++++++++++++++++++++++++++++\n" +
                "Te esta siguiento: @" + follower + "\n" +
                "++++++++++++++++++++++++++++++\n");
    }

    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", REGISTRY_ENDPOINT);
        System.setProperty("java.rmi.server.codebase", "http://" + REGISTRY_ENDPOINT + "/server/");
        System.setProperty("java.security.policy", "security.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            registry.rebind("com.twitter.rmi.server.TwitterImpl", new TwitterImpl());
            System.out.println("[INFO] Servidor de objetos escuchando en " + REGISTRY_ENDPOINT + ":" + Registry.REGISTRY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
