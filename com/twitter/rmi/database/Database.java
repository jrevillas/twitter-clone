package com.twitter.rmi.database;

import com.twitter.rmi.server.StatusImpl;
import com.twitter.rmi.common.*;
import com.twitter.rmi.server.UserImpl;
import redis.clients.jedis.Jedis;

import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by jruiz on 12/7/16.
 */
public class Database {

    private static Jedis jedis;
    private static Long nextPost;
    static {
        jedis = new Jedis("localhost");
        // jedis.auth("sandsand");
        nextPost = 0L;
        jedis.set("postId", "0");
    }
    public Database () {}

    public static void submitStatus(String handle, String content) throws RemoteException{
        Status status = new StatusImpl().setUserHandle(handle).setBody(content).setPostId(nextPost);

        Map<String, String> statusMap = new HashMap<String, String>();
        statusMap.put("user", status.getUserHandle());
        statusMap.put("body", status.getBody());

        jedis.hmset(jedis.get("postId") + ":post", statusMap);
        // Tweets del usuario
        jedis.lpush(status.getUserHandle() + ":status", jedis.get("postId"));
        // Timeline global
        jedis.lpush("timeline", jedis.get("postId"));
        // Incrementamos el id del post
        nextPost = jedis.incr("postId");
    }

    public static User register (String handle, String password) throws RemoteException{
        User user = new UserImpl().setHandle(handle).setPassword(password);

        // Comprobamos que el usuario no existe ya
        if (userExists(handle))
            return null;

        Map<String, String> userProperties = new HashMap<String, String>();

        userProperties.put("username", handle);
        userProperties.put("password", password);
        userProperties.put("bio", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s.");
        userProperties.put("verified", String.valueOf(false));

        jedis.hmset(handle + ":profile", userProperties);
        return user;
    }

    private static boolean userExists (String username) {
        Set<String> users = jedis.keys("*:profile");
        List<String> userMap;
        for (String user: users) {
            userMap = jedis.hmget(user, "username");
            if (userMap.get(0).equals(username))
                return true;
        }
        return false;
    }

    public static User login (String handle, String password) throws RemoteException {
        User newUser = new UserImpl().setHandle(handle).setPassword(password);

        if (userExists(handle)) {
            List<String> userPasswd = jedis.hmget(handle + ":profile", "password");
            if (userPasswd.get(0).equals(password)) {

                return newUser;
            }
        }
        return null;
    }

    public static List<Status> getTimeline(String handle) throws RemoteException{
        List<Status> result = new ArrayList<>();
        List<String> timeline = jedis.lrange("timeline", 0, -1);
        List<String> followers = jedis.lrange(handle + ":following", 0, -1);
        List<String> status;
        Long postIdLocal = null;
        for (String id: timeline) {
            status = jedis.hmget(id + ":post", "user", "body");
            if (followers.contains(status.get(0)) || status.get(0).equals(handle)) {
                postIdLocal.valueOf(id);
                Status newStatus = new StatusImpl().setUserHandle(status.get(0)).setPostId(postIdLocal).setBody(status.get(1));
                result.add(newStatus);
            }
        }
        return result;
    }

    public static void follow (User user, String follow) throws RemoteException{
        jedis.lpush(user.getHandle() + ":following", follow);
        jedis.lpush(follow + ":followers", user.getHandle());
    }

    public static void unfollow (User user, String unfollow) throws RemoteException {
        jedis.lrem(user.getHandle() + ":following", -1, unfollow);
        jedis.lrem(unfollow + ":followers", -1, user.getHandle());
    }

    public static List<String> getFollowers (String user) throws RemoteException {
        return jedis.lrange(user + ":followers", 0, -1);
    }

    public static List<String> getFollowing (String user) throws RemoteException {
        return jedis.lrange(user + ":following", 0, -1);
    }

    public static void addBio(String user, String bio) {
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
