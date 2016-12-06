package com.twitter.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by jrevillas on 06/12/2016.
 */
public interface Twitter extends Remote {

    // Devuelve una cadena de texto con la version de la API que utilice el servidor.
    public String getVersion() throws RemoteException;

    // Si la clave de acceso se corresponde con la del usuario especificado, el servidor devolvera un objeto User con
    // los metodos correspondientes. Devolvera una referencia nula en cualquier otro caso.
    public User login(String handle, String password) throws RemoteException;

    // Registra un usuario con el identificador y clave de acceso indicados. Devuelve una referencia al objeto User
    // generado siempre y cuando ese identificador este disponible y la clave de acceso cumpla con los requisitos del
    // servidor. Devuelve una referencia nula si el servidor detecta que alguna de las condiciones no se cumple.
    public User register(String handle, String password) throws RemoteException;

}
