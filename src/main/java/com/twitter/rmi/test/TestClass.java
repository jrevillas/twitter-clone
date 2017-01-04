package main.java.com.twitter.rmi.test;

import junit.framework.*;
import main.java.com.twitter.rmi.client.ClientCallbackImpl;
import main.java.com.twitter.rmi.common.*;
import main.java.com.twitter.rmi.server.ServerLauncher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Created by dmelero on 02/01/2017.
 */
public class TestClass extends TestCase {

    private static final String REGISTRY_ENDPOINT = "localhost";
    public static Twitter twitter = null;

    static{

        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("java.security.policy", "security.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            Registry registry = LocateRegistry.getRegistry(REGISTRY_ENDPOINT, Registry.REGISTRY_PORT);
            twitter = (Twitter) registry.lookup("com.twitter.rmi.server.TwitterImpl");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Compueba que se ha establecido la conexion correctamente
    public void testConnection(){
        assertNotNull("Conexión fallida", twitter);
    }

    // Comprueba que un registro se ha hecho correctamente
    public void testRegistry01() {

        try {
            assertNotNull("El registro no se ha hecho correctamente",
                    twitter.register("registry01", "12345"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    // Comprueba que el segundo registro no es correcto
    public void testRegistry02() {

        try {
            assertNotNull("El registro no se ha hecho correctamente",
                    twitter.register("registry02", "12345"));
            assertNull("El registro se ha hecho correctamente",
                    twitter.register("registry02", "12345"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    // Comprueba que el login es incorrecto
    public void testLogin01(){

        try {
            assertNull(twitter.login("login01", "12345"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    // Comprueba que el login es correcto
    public void testLogin02(){

        try {
            assertNotNull("El registro no se ha hecho correctamente",
                    twitter.register("login02", "12345"));
            assertNotNull("El login no se ha hecho correctamente",
                    twitter.login("login02", "12345"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     Comprueba que el timeline es correcto:
        - El timeline solo tiene 1 status
        - El status que hay es el creado
     */
    public void testStatus01(){
        try {
            User user = twitter.register("status01", "12345");
            String status = "Status del testStatus01";

            user.submitStatus(status);

            List<Status> statuses = user.getTimeline();
            assertEquals(status, statuses.get(0).getBody());
            assertEquals(1,statuses.size());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que el timeline es correcto:
     - El timeline solo tiene 3 status
     - Los status que hay son los creados
     */
    public void testStatus02(){

        try {
            User user = twitter.register("status02", "12345");
            String status1 = "Status 1 del testStatus02";
            String status2 = "Status 2 del testStatus02";
            String status3 = "Status 3 del testStatus02";

            user.submitStatus(status1);
            user.submitStatus(status2);
            user.submitStatus(status3);

            List<Status> statuses = user.getTimeline();
            assertEquals(status1, statuses.get(2).getBody());
            assertEquals(status2, statuses.get(1).getBody());
            assertEquals(status3, statuses.get(0).getBody());
            assertEquals(3,statuses.size());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que en el timeline de un usuario estan: (SIMPLE)
        - Los status de dicho usuario
        - Los status de la gente que sigue
     */
    public void testStatus03(){

        try {
            User user1 = twitter.register("status03-1", "12345");
            User user2 = twitter.register("status03-2", "12345");
            String status1 = "Status 1 del testStatus03-1";
            String status2 = "Status 2 del testStatus03-1";
            String status3 = "Status 1 del testStatus03-2";

            user1.follow(user2.getHandle());
            user1.submitStatus(status1);
            user2.submitStatus(status3);
            user1.submitStatus(status2);

            List<Status> statuses = user1.getTimeline();
            assertEquals(status1, statuses.get(2).getBody());
            assertEquals(status3, statuses.get(1).getBody());
            assertEquals(status2, statuses.get(0).getBody());
            assertEquals(3,statuses.size());

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que en el timeline de un usuario estan: (AVANZADA)
     - Los status de dicho usuario
     - Los status de la gente que sigue
     */
    public void testStatus04(){

        try {
            User user1 = twitter.register("status04-1", "12345");
            User user2 = twitter.register("status04-2", "12345");
            User user3 = twitter.register("status04-3", "12345");
            String status1 = "Status 1 del testStatus04-1";
            String status2 = "Status 2 del testStatus04-1";
            String status3 = "Status 1 del testStatus04-2";
            String status4 = "Status 1 del testStatus04-3";

            user1.follow(user2.getHandle());
            user1.follow(user3.getHandle());
            user1.submitStatus(status1);
            user2.submitStatus(status3);
            user3.submitStatus(status4);
            user1.submitStatus(status2);

            List<Status> statuses = user1.getTimeline();
            assertEquals(status1, statuses.get(3).getBody());
            assertEquals(status3, statuses.get(2).getBody());
            assertEquals(status4, statuses.get(1).getBody());
            assertEquals(status2, statuses.get(0).getBody());
            assertEquals(4,statuses.size());

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que un usuario sigue a otro correctamente
     */
    public void testFollow01(){

        try {
            User user1 = twitter.register("follow01-1", "12345");
            User user2 = twitter.register("follow01-2", "12345");
            user2.follow(user1.getHandle());
            List<User> followersUser1 = user1.getFollowers(user1.getHandle());
            assertEquals(followersUser1.get(0).getHandle(), user2.getHandle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que varios usuarios siguen a otro correctamente
     */
    public void testFollow02(){

        try {
            User user1 = twitter.register("follow02-1", "12345");
            User user2 = twitter.register("follow02-2", "12345");
            User user3 = twitter.register("follow02-3", "12345");
            user2.follow(user1.getHandle());
            user3.follow(user1.getHandle());
            List<User> followersUser1 = user1.getFollowers(user1.getHandle());
            assertEquals(followersUser1.get(1).getHandle(), user2.getHandle());
            assertEquals(followersUser1.get(0).getHandle(), user3.getHandle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que dos usuarios se siguen mutuamente
     */
    public void testFollow03(){

        try {
            User user1 = twitter.register("follow03-1", "12345");
            User user2 = twitter.register("follow03-2", "12345");
            user1.follow(user2.getHandle());
            user2.follow(user1.getHandle());
            List<User> followersUser1 = user1.getFollowers(user1.getHandle());
            assertEquals(followersUser1.get(0).getHandle(), user2.getHandle());
            List<User> followersUser2 = user2.getFollowers(user2.getHandle());
            assertEquals(followersUser2.get(0).getHandle(), user1.getHandle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que un usuario no sigue a nadie después
     de seguir y dejar de seguir a alguien
     */
    public void testFollow04(){

        try {
            User user1 = twitter.register("follow04-1", "12345");
            User user2 = twitter.register("follow04-2", "12345");
            user1.follow(user2.getHandle());
            user1.unfollow(user2.getHandle());
            List<User> followersUser2 = user2.getFollowers(user2.getHandle());
            assertEquals(0, followersUser2.size());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que el mensaje le ha llegado a otro usuario
     */
    public void testPM01(){

        try {
            User user1 = twitter.register("testPM01-1", "12345");
            User user2 = twitter.register("testPM01-2", "12345");
            String pm1 = "Mensaje privado de testPM01-1 a testPM01-2";

            user1.submitPm(pm1,user2.getHandle());

            List<PrivateMessage> sentPMU1 = user1.getSentPM();
            assertEquals(sentPMU1.get(0).getBody(), pm1);
            assertEquals(1, sentPMU1.size());

            List<PrivateMessage> receivedPMU2 = user2.getReceivedPM();
            assertEquals(receivedPMU2.get(0).getBody(), pm1);
            assertEquals(1, receivedPMU2.size());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que se han intercambiado bien los mensajes entre 2 usuarios (SIMPLE)
     */
    public void testPM02(){

        try {
            User user1 = twitter.register("testPM02-1", "12345");
            User user2 = twitter.register("testPM02-2", "12345");
            String pm1 = "Mensaje privado de testPM02-1 a testPM02-2";
            String pm2 = "Mensaje privado de testPM02-2 a testPM02-1";

            user1.submitPm(pm1,user2.getHandle());
            user2.submitPm(pm2,user1.getHandle());

            List<PrivateMessage> sentPMU1 = user1.getSentPM();
            assertEquals(sentPMU1.get(0).getBody(), pm1);
            assertEquals(1, sentPMU1.size());
            List<PrivateMessage> receivedPMU1 = user1.getReceivedPM();
            assertEquals(receivedPMU1.get(0).getBody(), pm2);
            assertEquals(1, receivedPMU1.size());

            List<PrivateMessage> sentPMU2 = user2.getSentPM();
            assertEquals(sentPMU2.get(0).getBody(), pm2);
            assertEquals(1, sentPMU2.size());
            List<PrivateMessage> receivedPMU2 = user2.getReceivedPM();
            assertEquals(receivedPMU2.get(0).getBody(), pm1);
            assertEquals(1, receivedPMU2.size());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que se han intercambiado bien los mensajes entre 2 usuarios (AVANZADA)
     */
    public void testPM03(){

        try {
            User user1 = twitter.register("testPM03-1", "12345");
            User user2 = twitter.register("testPM03-2", "12345");
            String pm1 = "Mensaje privado (1) de testPM03-1 a testPM03-2";
            String pm2 = "Mensaje privado (1) de testPM03-2 a testPM03-1";
            String pm3 = "Mensaje privado (2) de testPM03-1 a testPM03-2";
            String pm4 = "Mensaje privado (2) de testPM03-2 a testPM03-1";

            user1.submitPm(pm1,user2.getHandle());
            user2.submitPm(pm2,user1.getHandle());
            user2.submitPm(pm4,user1.getHandle());
            user1.submitPm(pm3,user2.getHandle());

            List<PrivateMessage> sentPMU1 = user1.getSentPM();
            assertEquals(sentPMU1.get(1).getBody(), pm1);
            assertEquals(sentPMU1.get(0).getBody(), pm3);
            assertEquals(2, sentPMU1.size());

            List<PrivateMessage> receivedPMU1 = user1.getReceivedPM();
            assertEquals(receivedPMU1.get(1).getBody(), pm2);
            assertEquals(receivedPMU1.get(0).getBody(), pm4);
            assertEquals(2, receivedPMU1.size());

            List<PrivateMessage> sentPMU2 = user2.getSentPM();
            assertEquals(sentPMU2.get(1).getBody(), pm2);
            assertEquals(sentPMU2.get(0).getBody(), pm4);
            assertEquals(2, sentPMU2.size());

            List<PrivateMessage> receivedPMU2 = user2.getReceivedPM();
            assertEquals(receivedPMU2.get(1).getBody(), pm1);
            assertEquals(receivedPMU2.get(0).getBody(), pm3);
            assertEquals(2, receivedPMU2.size());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que un usuario no se puede enviar un mensaje a si mismo
     */
    public void testPM04(){

        try {
            User user1 = twitter.register("testPM04-1", "12345");
            String pm1 = "Mensaje privado de testPM04-1 a testPM04-1";

            user1.submitPm(pm1,user1.getHandle());

            List<PrivateMessage> sentPMU1 = user1.getSentPM();
            assertEquals(0, sentPMU1.size());

            List<PrivateMessage> receivedPMU1 = user1.getReceivedPM();
            assertEquals(0, receivedPMU1.size());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que un usuario esta bien registrado para los callbacks
     */
    public void testCallback01(){

        try {
            User user = twitter.register("testCallback01", "12345");
            ClientCallback clientCallback = new ClientCallbackImpl();

            user.pushSubscribe(clientCallback);

            System.out.println("Tamaño HashCallbacks: " + ServerLauncher.callbacks.size());

            assertEquals(1, ServerLauncher.callbacks.size());
            assertTrue(ServerLauncher.callbacks.containsKey(user.getHandle()));

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que varios usuarios están bien registrados para los callbacks
     */
    public void testCallback02(){

        try {
            User user1 = twitter.register("testCallback02-1", "12345");
            ClientCallback clientCallback1 = new ClientCallbackImpl();
            User user2 = twitter.register("testCallback02-2", "12345");
            ClientCallback clientCallback2 = new ClientCallbackImpl();
            User user3 = twitter.register("testCallback02-3", "12345");
            ClientCallback clientCallback3 = new ClientCallbackImpl();

            user1.pushSubscribe(clientCallback1);
            user2.pushSubscribe(clientCallback2);
            user3.pushSubscribe(clientCallback3);

            assertEquals(3, ServerLauncher.callbacks.size());
            assertTrue(ServerLauncher.callbacks.containsKey(user1.getHandle()));
            assertTrue(ServerLauncher.callbacks.containsKey(user2.getHandle()));
            assertTrue(ServerLauncher.callbacks.containsKey(user3.getHandle()));

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que un usuario esta bien registrado y luego no para los callbacks
     */
    public void testCallback03(){

        try {
            User user = twitter.register("testCallback03", "12345");
            ClientCallback clientCallback = new ClientCallbackImpl();

            user.pushSubscribe(clientCallback);

            assertEquals(1, ServerLauncher.callbacks.size());
            assertTrue(ServerLauncher.callbacks.containsKey(user.getHandle()));

            user.pushUnsubscribe(clientCallback);

            assertEquals(0, ServerLauncher.callbacks.size());
            assertFalse(ServerLauncher.callbacks.containsKey(user.getHandle()));

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     Comprueba que varios usuarios están bien registrados y luego no para los callbacks
     */
    public void testCallback04(){

        try {
            User user1 = twitter.register("testCallback04-1", "12345");
            ClientCallback clientCallback1 = new ClientCallbackImpl();
            User user2 = twitter.register("testCallback04-2", "12345");
            ClientCallback clientCallback2 = new ClientCallbackImpl();
            User user3 = twitter.register("testCallback04-3", "12345");
            ClientCallback clientCallback3 = new ClientCallbackImpl();

            user1.pushSubscribe(clientCallback1);
            user2.pushSubscribe(clientCallback2);
            user3.pushSubscribe(clientCallback3);

            assertEquals(3, ServerLauncher.callbacks.size());
            assertTrue(ServerLauncher.callbacks.containsKey(user1.getHandle()));
            assertTrue(ServerLauncher.callbacks.containsKey(user2.getHandle()));
            assertTrue(ServerLauncher.callbacks.containsKey(user3.getHandle()));

            user1.pushUnsubscribe(clientCallback1);
            user2.pushUnsubscribe(clientCallback2);
            user3.pushUnsubscribe(clientCallback3);

            assertEquals(0, ServerLauncher.callbacks.size());
            assertFalse(ServerLauncher.callbacks.containsKey(user1.getHandle()));
            assertFalse(ServerLauncher.callbacks.containsKey(user2.getHandle()));
            assertFalse(ServerLauncher.callbacks.containsKey(user3.getHandle()));

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static Test suite() {
        return new TestSuite(TestClass.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
