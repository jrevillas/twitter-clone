package com.twitter.rmi.server;

import com.twitter.rmi.common.ClientCallback;
import com.twitter.rmi.common.ServerCallback;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jrevillas on 06/12/2016.
 */
public class ServerLauncher {

    private static final String REGISTRY_ENDPOINT = "localhost";
    public static Map<String, ClientCallback> callbacks = new HashMap<String, ClientCallback>();

    public static void subscribe(String username, ClientCallback callback) {
        callbacks.put(username, callback);
        System.out.println("Se ha registrado un callback para " + username);
    }

    // Elimina el callback especificado. Si .remove() recibe dos parametros, solo lo elimina del mapa
    // si esa clave guarda la referencia pasada como segundo argumento.
    public static void unsubscribe(String username, ClientCallback callback) {
        if (callbacks.remove(username, callback)) {
            System.out.println("Se ha eliminado un callback de " + username);
        } else {
            System.out.println("No se ha podido eliminar un callback de " + username);
        }
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
            ServerCallback exportedObj = new ServerCallbackImpl();
            Naming.rebind("com.twitter.rmi.server.ServerCallbackImpl", exportedObj);
            registry.rebind("com.twitter.rmi.server.TwitterImpl", new TwitterImpl());
            System.out.println("[INFO] Servidor de objetos escuchando en " + REGISTRY_ENDPOINT + ":" + Registry.REGISTRY_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
