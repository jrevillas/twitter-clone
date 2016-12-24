package com.twitter.rmi.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.twitter.rmi.common.Client;
import com.twitter.rmi.common.Twitter;
import com.twitter.rmi.common.User;

/**
 * Created by jrevillas on 06/12/2016.
 */
public class ClientLauncher {

    private static final String REGISTRY_ENDPOINT = "localhost";
    private static String USER_NAME = "dmelero";

    public static void main(String[] args) {
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("java.security.policy", "security.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Localizamos el registro remoto que escucha en el puerto 1099.
            Registry registry = LocateRegistry.getRegistry(REGISTRY_ENDPOINT, Registry.REGISTRY_PORT);

            // Gestión del callback
            Client clientCallback = new ClientImpl();

            // Solicitamos al registro una instancia de Twitter para empezar a operar.
            Twitter twitter = (Twitter) registry.lookup("com.twitter.rmi.server.TwitterImpl");

            User me = twitter.register(USER_NAME, "1qazxsw2");
            me.registerForCallback(clientCallback);

            System.out.println("Dormimos 30 segundos");
            try {
                Thread.sleep(30 * 1000);
            }
            catch (InterruptedException ex){ // sleep over
            }

            me.submitStatus("Status para comprobar el callback");

            me.unregisterForCallback(clientCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
