package com.twitter.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by jrevillas on 06/12/2016.
 */
public interface Status extends Remote {

    // Devuelve el cuerpo del mensaje.
    public String getContent() throws RemoteException;

    // Devuelve el identificador del usuario que publico el estado.
    public String getUserHandle() throws RemoteException;

}
