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
        List<String> followers = jedis.lrange(handle + ":followers", 0, -1);
        List<String> status;
        Long postId = null;
        for (String id: timeline) {
            status = jedis.hmget(id + ":post", "user", "body");
            if (followers.contains(status.get(0)) || status.get(0).equals(handle)) {
                postId.valueOf(id);
                Status newStatus = new StatusImpl().setUserHandle(handle).setPostId(postId).setBody(status.get(1));
                result.add(newStatus);
            }
        }
        return result;
    }

    public static void follow (User user, String follow) throws RemoteException{
        jedis.lpush(user.getHandle() + ":followers", follow);
        jedis.lpush(follow + ":following", user.getHandle());
    }
}
