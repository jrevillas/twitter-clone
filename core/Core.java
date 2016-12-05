package core;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by dmelero on 30/11/16.
 */
public class Core {

    // IP del servidor
    private static final String server_ip = "192.168.0.100";

    public static void main(String[] args) throws RemoteException {

        // IP de los objetos remotos
        System.setProperty("java.rmi.server.hostname", server_ip);

        // URI de las Clases
        System.setProperty("java.rmi.server.codebase", "http://" + server_ip + "/servidor/");

        // Política de Seguridad
        System.setProperty("java.security.policy", "core.policy");

        try {
            // Comprobación para poder descargar los ficheros
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            registry.rebind("core.twitter-rmi", new CoreManagerImpl());

            System.out.println("INFO: Servidor de objetos en marcha en IP "+ server_ip + " ...");
            System.out.println("");

        } catch (RemoteException e) {
            System.err.println("INFO: Error al crear el registro o el banco remoto...");
        }
    }

}
