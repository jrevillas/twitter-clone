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

}
