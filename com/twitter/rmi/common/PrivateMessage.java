package com.twitter.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by jruiz on 12/24/16.
 */
public interface PrivateMessage extends Remote{
    public Long getPostIdPm() throws RemoteException;
    public String getSender() throws RemoteException;
    public String getDate() throws RemoteException;
    public String getReceiver() throws RemoteException;
    public String getBody() throws RemoteException;
}
