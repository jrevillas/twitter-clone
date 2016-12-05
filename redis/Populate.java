package redis;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jruiz on 12/1/16.
 */
public class Populate {

    // Metemos 4 usuarios, cada uno con sus tweets y siguiendo y siendo seguidos por los otros
    public static Jedis jedis = new Jedis("twitter-rmi.com");
    public static Long postId;


    public static void main(String[] args) {

        jedis.auth("sandsand");
        jedis.flushAll();

//        jedis.set("postId", "0");

        User jruiz = new User().setHandle("jruiz").setPassword("1qazxsw2");
        User jrevillas = new User().setHandle("jrevillas").setPassword("sandsand");
        User dmelero = new User().setHandle("dmelero").setPassword("frozen");
        User mnunez = new User().setHandle("mnunez").setPassword("hum");

        createUser(jruiz);
        createUser(jrevillas);
        createUser(dmelero);
        createUser(mnunez);


        Status jruizTweet = new Status().setBody("holita").setUser(jruiz.getHandle());

        Status jrevillasTweet = new Status().setBody("Tuturu").setUser(jrevillas.getHandle());

        Status dmeleroTweet = new Status().setBody("let it go").setUser(dmelero.getHandle());

        Status mnunezTweet = new Status().setBody("hum hum").setUser(mnunez.getHandle());

        userStatus(jruizTweet);
        userStatus(jrevillasTweet);
        userStatus(dmeleroTweet);
        userStatus(mnunezTweet);

        jruizTweet.setBody("segundo intento");
        userStatus(jruizTweet);

        // Follow
        follows(jruiz, dmelero);
        follows(jrevillas, mnunez);
        follows(jrevillas, jruiz);
        follows(mnunez, dmelero);

        // private messages
        pm(jruiz, "holita que tal", jrevillas);
        pm(mnunez, "ola ke ase", dmelero);

        // Pintamos la timeline con todos los mensajes :)
        List<String> ids = jedis.lrange("timeline", 0, -1);
        List<String> info;
        for (String id : ids) {
            info = jedis.hmget(id + ":post", "user", "body");
            System.out.println("[USER]: " + info.get(0)
                    + " tweeted [TWEET]: " + info.get(1));
        }
    }

    public static void pm (User sender, String body, User receiver) {
        // dos listas, una con el usario y todos los mensajes que manda
        // y otra con todos los mensajes que recibe
        // más un hashmap donde se muestren todos los mensajes

        jedis.lpush(sender.getHandle() + ":pm:sent", body + "|" + receiver.getHandle());
        jedis.lpush(receiver.getHandle() + ":pm:inbox", body + "|" + sender.getHandle());

    }

    public static void follows(User following, User followed) {
        jedis.lpush(followed.getHandle() + ":followers", following.getHandle());
        jedis.lpush(following.getHandle() + ":following", followed.getHandle());
    }

    public static void createUser(User user) {

        // Just for fun, tambien vamos a tener una lista con todos los usuarios
        //jedis.lpush("users", user.getHandle());
        // Se pueden conseguir con > KEYS *:users
        Map<String, String> userProperties = new HashMap<String, String>();

        userProperties.put("username", user.getHandle());
        userProperties.put("password", user.getPassword());

        jedis.hmset(user.getHandle() + ":profile", userProperties);
    }

    public static void userStatus(Status status) {

        // HM en el que tenemos: postId:post y tenemos toda la info
        postId = jedis.incr("postId");
        status.setPostId(postId);
        Map<String, String> statusMap = new HashMap<String, String>();
        statusMap.put("user", status.getUser());
        statusMap.put("body", status.getBody());

        jedis.hmset(jedis.get("postId") + ":post", statusMap);

        // Tweets del usuario
        jedis.lpush(status.getUser() + ":status", jedis.get("postId"));

        // Lo metemos en la timeline global
        jedis.lpush("timeline", jedis.get("postId"));
    }
}
