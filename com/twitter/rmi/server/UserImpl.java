package com.twitter.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Optional;

import com.twitter.rmi.common.ClientCallback;
import com.twitter.rmi.common.PrivateMessage;
import com.twitter.rmi.common.Status;
import com.twitter.rmi.common.User;
import com.twitter.rmi.database.Database;

import javax.xml.crypto.Data;

/**
 * Created by jruiz on 06/12/2016.
 */
public class UserImpl extends UnicastRemoteObject implements User {

    private String handle;
    private String password;
    private String bio;
    private boolean verified;


    public UserImpl() throws RemoteException {
        super();
    }

    @Override
    public String getBio() throws RemoteException {
        return bio;
    }

    public UserImpl _setBio(String bio) {
        this.bio = bio;
        return this;
    }

    @Override
    public void setBio(String bio) throws RemoteException {
        this.bio = bio;
        Database.addBio(this.handle, bio);
    }

    @Override
    public boolean getVerified() throws RemoteException {
        return verified;
    }

    public UserImpl setVerified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    @Override
    public String getHandle() throws RemoteException {
        return handle;
    }

    public UserImpl setHandle(String handle) throws RemoteException {
        this.handle = handle;
        return this;
    }

    public String getPassword() throws RemoteException {
        return this.password;
    }

    public UserImpl setPassword(String password) throws RemoteException {
        this.password = password;
        return this;
    }

    @Override
    public List<Status> getTimeline() throws RemoteException {
        System.out.println(this.getHandle() + " -> getTimeline()");
        List<Status> result = Database.getTimeline(this.getHandle());
        return result;
    }

    @Override
    public void submitStatus(String content) throws RemoteException {
        System.out.println(this.getHandle() + " -> submitStatus(...)");
        Database.submitStatus(this.handle, content);
        ServerCallbackImpl.notifyOnTweet(this.handle, content);
    }

    @Override
    public void updatePassword(String password) throws RemoteException {
        System.out.println(this.getHandle() + " -> updatePassword(...)");
        return;
    }

    @Override
    public void follow(String user) throws RemoteException {
        System.out.println(this.getHandle() + " -> follow(" + user + ")");
        Database.follow(this, user);
        return;
    }

    @Override
    public void unfollow(String user) throws RemoteException {
        System.out.println(this.getHandle() + " -> follow(" + user + ")");
        Database.unfollow(this, user);
        return;
    }

    @Override
    public List<User> getFollowers(String user) throws RemoteException {
        System.out.println(this.getHandle() + " -> getFollowers(...)");
        return Database.getFollowers(user);
    }

    @Override
    public List<User> getFollowing(String user) throws RemoteException {
        System.out.println(this.getHandle() + " -> getFollowing(...)");
        return Database.getFollowing(user);
    }

    @Override
    public List<User> getUserFollowing() throws RemoteException {
        System.out.println(this.getHandle() + " -> getUserFollowing()");
        return Database.getFollowing(this.getHandle());
    }

    @Override
    public List<User> getUsers() throws RemoteException {
        System.out.println(this.getHandle() + " -> getUsers()");
        return Database.getUsers();
    }

    @Override
    public void pushSubscribe(ClientCallback callback) throws RemoteException {
        System.out.println(this.getHandle() + " -> pushSubscribe(...)");
        ServerLauncher.subscribe(this.getHandle(), callback);
    }

    @Override
    public void pushUnsubscribe(ClientCallback callback) throws RemoteException {
        System.out.println(this.getHandle() + " -> pushUnsubscribe(...)");
        ServerLauncher.unsubscribe(this.getHandle(), callback);
    }

    @Override
    public void submitPm(String content, String receiver) throws RemoteException {
        System.out.println(this.getHandle() + " -> submitPM(...)");
        Database.submitPm(this.getHandle(), content, receiver);
    }

    @Override
    public List<PrivateMessage> getSentPM() throws RemoteException {
        System.out.println(this.getHandle() + " -> getSentPM()");
        return Database.getSentPM(this.getHandle());
    }


    @Override
    public List<PrivateMessage> getReceivedPM() throws RemoteException {
        System.out.println(this.getHandle() + " -> getReceivedPM()");
        return Database.getReceivedPM(this.getHandle());
    }

    @Override
    public List<Status> getStatuses(String username) throws RemoteException {
        System.out.println(username + " -> getStatuses(...)");
        return Database.getStatuses(username);
    }

    @Override
    public Boolean isFollowing(String user) throws RemoteException {
        System.out.println(this.getHandle() + " -> isFollowing(...)");
        return Database.isFollowing(this.getHandle(), user);
    }

    @Override
    public Boolean isFollowed(String user) throws RemoteException {
        System.out.println(this.getHandle() + " -> isFollowed(...)");
        return Database.isFollowed(this.getHandle(), user);
    }
}
