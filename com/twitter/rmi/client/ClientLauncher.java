package com.twitter.rmi.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.twitter.rmi.common.Twitter;

/**
 * Created by jrevillas on 06/12/2016.
 */
public class ClientLauncher {

    private static final String REGISTRY_ENDPOINT = "twitter-rmi.com";

    public static void main(String[] args) {
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("java.security.policy", "security.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Localizamos el registro remoto que escucha en el puerto 1099.
            Registry registry = LocateRegistry.getRegistry(REGISTRY_ENDPOINT, Registry.REGISTRY_PORT);
            // Solicitamos al registro una instancia de Twitter para empezar a operar.
            Twitter twitter = (Twitter) registry.lookup("com.twitter.rmi.server.TwitterImpl");
            System.out.format("Version de twitter-rmi: %s", twitter.getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
