package core;

import client.ClientCallback;
import redis.Status;
import redis.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by dmelero on 30/11/16.
 */
public interface CoreManager extends Remote {

    /**
     * Funciones de Prueba
     */

    public void userIn(String handle, String password) throws RemoteException;

    public void statusIn(String name, String body) throws RemoteException;

    public void printUsers() throws RemoteException;

    public void printStatus() throws RemoteException;

    /**
     * Métodos del Callback
     */

    public String sayHello() throws RemoteException;

    public void registerForCallback(ClientCallback callbackClientObject) throws RemoteException;

    public void unregisterForCallback(ClientCallback callbackClientObject) throws RemoteException;

    /**
     * Métodos de Twitter
     */

    public boolean signup(User user) throws RemoteException;

    public boolean login(User user) throws RemoteException;

    public boolean logout(User user) throws RemoteException;

    public Status createStatus(User user, String text) throws RemoteException;

    public Status[] getTimeline(User user) throws RemoteException;

    public Status[] getStatusByUser(User user) throws RemoteException;

    public boolean follow(User user) throws RemoteException;

    public boolean unfollow(User user) throws RemoteException;

    public void sendPrivate(User user) throws RemoteException;

}
