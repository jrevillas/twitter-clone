package com.twitter.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by jrevillas on 06/12/2016.
 */
public interface User extends Remote {

    // Devuelve el identificador del usuario autenticado.
    public String getHandle() throws RemoteException;
    // Devuelve los estados publicos que hay en la base de datos.
    public List<Status> getTimeline() throws RemoteException;
    // Envia un estado al servidor para que se publique.
    public void submitStatus(String content) throws RemoteException;
    // Actualiza la clave de acceso del usuario, no requiere desconexion.
    public void updatePassword(String password) throws RemoteException;
    // Seguir a otro usuario
    public void follow(String user) throws RemoteException;

    public void unfollow (String user) throws RemoteException;

    public List<User> getFollowers(String user) throws RemoteException;
    public List<User> getFollowing(String user) throws RemoteException;
    public List<User> getUserFollowing() throws RemoteException;

    public String getBio() throws  RemoteException;
    public boolean getVerified() throws RemoteException;
    public void setBio(String bio) throws RemoteException;

    public List<User> getUsers() throws RemoteException;

    public void pushSubscribe(ClientCallback callbackClient) throws RemoteException;
    public void pushUnsubscribe(ClientCallback callbackClient) throws RemoteException;

    public void submitPm(String content, String receiver) throws RemoteException;

    public List<PrivateMessage> getSentPM () throws RemoteException;
    public List<PrivateMessage> getReceivedPM() throws RemoteException;

    public List<Status> getStatuses(String username) throws RemoteException;

    public Boolean isFollowing(String user) throws RemoteException;
    public Boolean isFollowed(String user) throws RemoteException;

}
