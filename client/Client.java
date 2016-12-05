package client;

import core.CoreManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by dmelero on 2/12/16.
 */
public class Client {

    private static final String remote_ip = "10.10.3.40";

    public static void main(String[] args) {

        // Política de Seguridad
        System.setProperty("java.security.policy", "core.policy");

        // ¿Codebases remotos?
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");

        try {

            // Comprobación para poder descargar los ficheros
            if(System.getSecurityManager()==null){
                System.setSecurityManager(new SecurityManager());
            }

            Registry registro = LocateRegistry.getRegistry(remote_ip, Registry.REGISTRY_PORT);
            CoreManager core = (CoreManager) registro.lookup("core.twitter-rmi");

            // Preparación del Callback

            System.out.println("Lookup completed " );
            System.out.println("Server said " + core.sayHello());
            ClientCallback callbackObj = new ClientCallbackImpl();

            // Registro del Callback
            core.registerForCallback(callbackObj);
            System.out.println("Registered for callback.");

            try {
                Thread.sleep(10 * 1000);
            }
            catch (InterruptedException ex){ // sleep over
            }

            core.unregisterForCallback(callbackObj);
            System.out.println("Unregistered for callback.");

            // Pruebas

            core.userIn("dmelero", "123");
            core.userIn("mnunez", "123");

            core.statusIn("dmelero", "El primer tweet de Daniel");
            core.statusIn("mnunez", "El primer tweet de Miguel");

            core.printUsers();
            core.printStatus();

        } catch (Exception e) {
            System.err.println("INFO: No puedo acceder al registro RMI o al banco remoto en la dirección " + remote_ip);
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }

}
