package com.twitter.rmi.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by jrevillas on 06/12/2016.
 */
public class ServerLauncher {

    private static final String REGISTRY_ENDPOINT = "localhost";

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
