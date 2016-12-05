package core;

import client.ClientCallback;
import redis.Status;
import redis.StatusImpl;
import redis.User;
import redis.UserImpl;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by dmelero on 30/11/16.
 */
public class CoreManagerImpl extends UnicastRemoteObject implements CoreManager, Serializable {

    private static final long serialVersionUID = 1740227210084638053L;
    private List<Status> statusList;
    private List<User> usersList;
    private Vector clientList;

    protected CoreManagerImpl() throws RemoteException {
        super();
        this.statusList = new ArrayList<Status>();
        this.usersList = new ArrayList<User>();
        this.clientList = new Vector();
    }

    @Override
    public void userIn(String handle, String password) throws RemoteException {

        System.out.println("User In");
        User u = new UserImpl();
        System.out.println(handle);
        System.out.println(password);
        u.setHandle(handle);
        u.setPassword(password);
        System.out.println(u.getHandle());
        System.out.println(u.getPassword());
        this.usersList.add(u);
        System.out.println("");
    }

    @Override
    public void statusIn(String name, String body) throws RemoteException {

        System.out.println("Status In");
        Status s = new StatusImpl();
        System.out.println(name);
        System.out.println(body);
        s.setName(name);
        s.setBody(body);
        this.statusList.add(s);
        System.out.println("");
    }

    @Override
    public void printUsers() throws RemoteException {

        System.out.println("Users List");
        for(int i = 0; i < this.usersList.size(); i++)
            System.out.println("Handle: " + this.usersList.get(i).getHandle() +
                    " | Password: " + this.usersList.get(i).getPassword());
        System.out.println("");

    }

    @Override
    public void printStatus() throws RemoteException {

        System.out.println("Status List");
        for(int i = 0; i < this.statusList.size(); i++)
            System.out.println("Name: " + this.statusList.get(i).getName() +
                    " | Body: " + this.statusList.get(i).getBody());
        System.out.println("");

    }

    @Override
    public String sayHello() throws RemoteException {
        return("Hello");
    }

    @Override
    public synchronized void registerForCallback(ClientCallback callbackClientObject) throws RemoteException {

        if (!(clientList.contains(callbackClientObject))) {
            clientList.addElement(callbackClientObject);
            System.out.println("Registered new client ");
            doCallbacks();
        }
    }

    @Override
    public synchronized void unregisterForCallback(ClientCallback callbackClientObject) throws RemoteException {
        if (clientList.removeElement(callbackClientObject)) {
            System.out.println("Unregistered client ");
        } else {
            System.out.println("unregister: clientwasn't registered.");
        }
    }

    private synchronized void doCallbacks( ) throws java.rmi.RemoteException{
        // make callback to each registered client
        System.out.println("**************************************\n"
                + "Callbacks initiated ---");
        for (int i = 0; i < clientList.size(); i++){
            System.out.println("doing "+ i +"-th callback\n");
            // convert the vector object to a callback object
            ClientCallback nextClient = (ClientCallback) clientList.elementAt(i);
            // invoke the callback method
            nextClient.notifyMe("Number of registered clients=" +  clientList.size());
        }// end for
        System.out.println("********************************\n" +
                "Server completed callbacks ---");
    }

    @Override
    public boolean signup(User user) throws RemoteException {
        return false;
    }

    @Override
    public boolean login(User user) throws RemoteException {
        return false;
    }

    @Override
    public boolean logout(User user) throws RemoteException {
        return false;
    }

    @Override
    public Status createStatus(User user, String text) throws RemoteException {
        return null;
    }

    @Override
    public Status[] getTimeline(User user) throws RemoteException {
        return new Status[0];
    }

    @Override
    public Status[] getStatusByUser(User user) throws RemoteException {
        return new Status[0];
    }

    @Override
    public boolean follow(User user) throws RemoteException {
        return false;
    }

    @Override
    public boolean unfollow(User user) throws RemoteException {
        return false;
    }

    @Override
    public void sendPrivate(User user) throws RemoteException {

    }
}
