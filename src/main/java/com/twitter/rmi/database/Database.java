package com.twitter.rmi.database;

import com.twitter.rmi.server.PrivateMessageImpl;
import com.twitter.rmi.server.StatusImpl;
import com.twitter.rmi.common.*;
import com.twitter.rmi.server.UserImpl;
import org.mindrot.jbcrypt.BCrypt;
import redis.clients.jedis.Jedis;

import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by jruiz on 12/7/16.
 */
public class Database {

    public static Object mutex = new Object();
    private static final int BCRYPT_COST = 10;
    private static Jedis jedis;
    private static Long nextPost;
    private static Long nextPm;

    static {
        jedis = new Jedis("rogueone.twitter-rmi.com");
        // jedis.auth("sandsand");
        nextPost = 0L;
        jedis.set("postId", "0");
        nextPm = 0L;
        jedis.set("pmId", "0");
    }

    public Database() {
    }

    public static void submitPm(String sender, String content, String receiver) throws RemoteException {
        synchronized (mutex) {
            if (!userExists(receiver))
                return;

            PrivateMessage pm = new PrivateMessageImpl().setBody(content).setSender(sender).
                    setReceiver(receiver).setDate(String.valueOf(System.currentTimeMillis())).setPostId(nextPm);
            Map<String, String> pmMap = new HashMap<String, String>();
            pmMap.put("sender", pm.getSender());
            pmMap.put("receiver", pm.getReceiver());
            pmMap.put("date", pm.getDate());
            pmMap.put("body", pm.getBody());

            jedis.hmset(jedis.get("pmId") + ":pm", pmMap);
            jedis.lpush(pm.getSender() + ":sentPM", jedis.get("pmId"));
            jedis.lpush(pm.getReceiver() + ":receivedPM", jedis.get("pmId"));
            nextPm = jedis.incr("pmId");
        }
    }

    public static void submitStatus(String handle, String content) throws RemoteException {
        synchronized (mutex) {
            Status status = new StatusImpl().setUserHandle(handle).setBody(content).setPostId(nextPost).
                    setDate(String.valueOf(System.currentTimeMillis()));

            Map<String, String> statusMap = new HashMap<String, String>();
            statusMap.put("user", status.getUserHandle());
            statusMap.put("body", status.getBody());
            statusMap.put("timestamp", status.getDate());

            jedis.hmset(jedis.get("postId") + ":post", statusMap);
            // Tweets del usuario
            jedis.lpush(status.getUserHandle() + ":status", jedis.get("postId"));
            // Timeline global
            jedis.lpush("timeline", jedis.get("postId"));
            // Incrementamos el id del post
            nextPost = jedis.incr("postId");
        }
    }

    public static User register(String handle, String password) throws RemoteException {
        synchronized (mutex) {
            User user = new UserImpl().setHandle(handle).setPassword(password);

            // Comprobamos que el usuario no existe ya
            if (userExists(handle))
                return null;

            Map<String, String> userProperties = new HashMap<String, String>();

            String hash = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_COST));

            userProperties.put("username", handle);
            userProperties.put("password", hash);
            userProperties.put("bio", "Lorem Ipsum is simply dummy text of the printing and typesetting industry.");
            userProperties.put("verified", String.valueOf(false));

            jedis.hmset(handle + ":profile", userProperties);
            user.setBio("Lorem Ipsum is simply dummy text of the printing and typesetting industry.");
            return user;
        }
    }

    private static boolean userExists(String username) {
        synchronized (mutex) {
            Set<String> users = jedis.keys("*:profile");
            List<String> userMap;
            for (String user : users) {
                userMap = jedis.hmget(user, "username");
                if (userMap.get(0).equals(username))
                    return true;
            }
            return false;
        }
    }

    public static User login(String handle, String password) throws RemoteException {
        synchronized (mutex) {
            User newUser = new UserImpl().setHandle(handle).setPassword(password);

            if (userExists(handle)) {
                List<String> userPasswd = jedis.hmget(handle + ":profile", "password", "bio");
                if (BCrypt.checkpw(password, userPasswd.get(0))) {
                    newUser.setBio(userPasswd.get(1));
                    return newUser;
                }
            }
            return null;
        }
    }

    public static List<PrivateMessage> getSentPM(String user) throws RemoteException {
        synchronized (mutex) {
            return fillerPM(jedis.lrange(user + ":sentPM", 0, -1));
        }
    }

    public static List<PrivateMessage> getReceivedPM(String user) throws RemoteException {
        synchronized (mutex) {
            return fillerPM(jedis.lrange(user + ":receivedPM", 0, -1));
        }
    }

    private static List<PrivateMessage> fillerPM(List<String> privateMessages) throws RemoteException {
        List<PrivateMessage> result = new ArrayList<PrivateMessage>();
        for (String pm : privateMessages) {
            List<String> pmHm = jedis.hmget(pm + ":pm", "sender", "receiver", "body", "date");
            PrivateMessage newPm = new PrivateMessageImpl().setSender(pmHm.get(0)).
                    setReceiver(pmHm.get(1)).setBody(pmHm.get(2)).setDate(pmHm.get(3));
            result.add(newPm);
        }
        return result;
    }

    public static List<Status> getTimeline(String handle) throws RemoteException {
        synchronized (mutex) {
            List<Status> result = new ArrayList<>();
            List<String> timeline = jedis.lrange("timeline", 0, -1);
            List<String> followers = jedis.lrange(handle + ":following", 0, -1);

            List<String> status;
            Long postIdLocal = null;
            for (String id : timeline) {
                status = jedis.hmget(id + ":post", "user", "body", "timestamp");
                if (followers.contains(status.get(0)) || status.get(0).equals(handle)) {
                    postIdLocal.valueOf(id);
                    Status newStatus = new StatusImpl().setUserHandle(status.get(0)).setPostId(postIdLocal).setBody(status.get(1)).
                            setDate(status.get(2));
                    result.add(newStatus);
                }
            }
            return result;
        }
    }

    public static void follow(User user, String follow) throws RemoteException {
        synchronized (mutex) {
            List<String> following = jedis.lrange(user.getHandle() + ":following", 0, -1);
            if (!following.contains(follow)) {
                jedis.lpush(user.getHandle() + ":following", follow);
                jedis.lpush(follow + ":followers", user.getHandle());
            }
        }
    }

    public static void unfollow(User user, String unfollow) throws RemoteException {
        synchronized (mutex) {
            jedis.lrem(user.getHandle() + ":following", -1, unfollow);
            jedis.lrem(unfollow + ":followers", -1, user.getHandle());
        }
    }

    public static List<User> getFollowers(String userName) throws RemoteException {
        synchronized (mutex) {
            return filler(jedis.lrange(userName + ":followers", 0, -1));
        }
    }

    public static List<User> getFollowing(String userName) throws RemoteException {
        synchronized (mutex) {
            return filler(jedis.lrange(userName + ":following", 0, -1));
        }
    }

    // Devuelve una lista de usuarios a partir de la lista de strings que reciba como argumento
    private static List<User> filler(List<String> users) throws RemoteException {
        List<User> resultList = new ArrayList<User>();
        for (String user : users) {
            List<String> userHm = jedis.hmget(user + ":profile", "username", "bio", "verified");
            User newUser = new UserImpl().setVerified(Boolean.valueOf(userHm.get(2))).
                    setHandle(userHm.get(0))._setBio(userHm.get(1)).setPassword("hidden");
            resultList.add(newUser);
        }
        return resultList;
    }

    public static void addBio(String user, String bio) {
        synchronized (mutex) {
            List<String> previousMap = jedis.hmget(user + ":profile", "username", "password", "verified");
            Map<String, String> newMap = new HashMap<String, String>();
            newMap.put("username", previousMap.get(0));
            newMap.put("password", previousMap.get(1));
            newMap.put("bio", bio);
            newMap.put("verified", previousMap.get(2));
            // jedis.del(user + ":profile");
            jedis.hmset(user + ":profile", newMap);
        }
    }

    public static List<User> getUsers() throws RemoteException {
        synchronized (mutex) {
            Set<String> users = jedis.keys("*:profile");
            List<User> result = new ArrayList<>();
            for (String user : users) {
                List<String> hmUser = jedis.hmget(user, "username", "bio", "verified");
                User newUser = new UserImpl().setHandle(hmUser.get(0)).setVerified(Boolean.valueOf(hmUser.get(2))).setPassword("hidden");
                newUser.setBio(hmUser.get(1));
                result.add(newUser);
            }
            return result;
        }
    }

    public static List<Status> getStatuses (String user) throws RemoteException{
        synchronized (mutex) {
            List<String> statusList = jedis.lrange(user + ":status", 0, -1);
            List<Status> result = new ArrayList<Status>();
            for (String status: statusList) {
                List<String> statusHm = jedis.hmget(status + ":post", "user", "body", "timestamp");
                Status newStatus = new StatusImpl().setBody(statusHm.get(0)).setBody(statusHm.get(1)).
                        setDate(statusHm.get(2));
                result.add(newStatus);
            }
            return result;
        }
    }

    public static Boolean isFollowing(String user1, String user2) throws RemoteException {
        synchronized (mutex) {
            List<String> following = jedis.lrange(user1 + ":following", 0, -1);
            if (following.contains(user2))
                return true;
            return false;
        }
    }

    public static Boolean isFollowed (String user1, String user2) throws RemoteException {
        synchronized (mutex) {
            List<String> followed = jedis.lrange(user1 + ":followed", 0, -1);
            if (followed.contains(user2))
                return true;
            return false;
        }
    }
}
