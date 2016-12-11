package com.twitter.rmi.client;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.twitter.rmi.common.ClientCallback;
import com.twitter.rmi.common.ServerCallback;
import com.twitter.rmi.common.Twitter;
import com.twitter.rmi.common.User;

/**
 * Created by jrevillas on 06/12/2016.
 */
public class ClientLauncher {

    private static final String REGISTRY_ENDPOINT = "localhost";
    private static String USER_NAME = "jruiz";

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
            ServerCallback callback = (ServerCallback) Naming.lookup("com.twitter.rmi.server.ServerCallbackImpl");
            ClientCallback clientCallback = new ClientCallbackImpl();
            callback.registerForCallback(USER_NAME,clientCallback);

            // Solicitamos al registro una instancia de Twitter para empezar a operar.
            Twitter twitter = (Twitter) registry.lookup("com.twitter.rmi.server.TwitterImpl");


            User me = twitter.login(USER_NAME, "1qazxsw2");

            System.out.println("Dormimos 30 segundos");
            try {
                Thread.sleep(30 * 1000);
            }
            catch (InterruptedException ex){ // sleep over
            }

            me.submitStatus("Prueba 1 de Daniel");

            System.out.println("Dormimos 30 segundos");
            try {
                Thread.sleep(30 * 1000);
            }
            catch (InterruptedException ex){ // sleep over
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
