package com.twitter.rmi.web;

import static spark.Spark.*;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.twitter.rmi.client.ClientCallbackImpl;
import com.twitter.rmi.common.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.eclipse.jetty.websocket.api.*;

import redis.clients.jedis.exceptions.JedisDataException;

/**
 * Created by jrevillas on 09/12/2016.
 */
public class WebLauncher {

    private static final String REGISTRY_ENDPOINT = "localhost";
    private static Twitter twitter;
    private static List<String> verifiedUsers;
    private static Map<String, User> tokens;
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static Map<String, Session> userMap = new ConcurrentHashMap<>();
    static Map<String, Session> socketConnections = new ConcurrentHashMap<>();
    static int nextUserNumber = 1;

    public static ServerCallback callback;
    public static ClientCallback clientCallback;

    public static void sendMessage(String username, String message) {
        Session session = socketConnections.get(username);
        if (session != null && session.isOpen()) {
            try {
                session.getRemote().sendString(new JSONObject().put("msg", message).toString(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Se acaba de enviar un mensaje a " + username);
        }
    }

    public static void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("msg", "Notificación de broadcast.")
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });
        tokens = new ConcurrentHashMap<String, User>();


        verifiedUsers = new ArrayList<>();
        verifiedUsers.add("tomholland1996");
        verifiedUsers.add("jrevillas");

        webSocket("/notifications", NotificationsSocketHandler.class);

        port(8080);
        init();
        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("java.security.policy", "security.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // Localizamos el registro remoto que escucha en el puerto 1099.
            Registry registry = LocateRegistry.getRegistry(REGISTRY_ENDPOINT, Registry.REGISTRY_PORT);
            // Solicitamos al registro una instancia de Twitter para empezar a operar.
            twitter = (Twitter) registry.lookup("com.twitter.rmi.server.TwitterImpl");
            System.out.format("Version de twitter-rmi: %s\n", twitter.getVersion());
            // user = twitter.login("pepito", "12345");
            // System.out.format("Nombre de usuario: %s\n", user.getHandle());
            // user.submitStatus("holita son las " + System.currentTimeMillis());


            // A VER, ESTO DEBE IR EN EL LOGIN (y en el registro).
            // Y SE TIENE QUE MANEJAR PARA MUCHOS CLIENTES.
            callback = (ServerCallback) registry.lookup("com.twitter.rmi.server.ServerCallbackImpl");
            clientCallback = new ClientCallbackImpl();
            // callback.registerForCallback("jrevillas", clientCallback);


        } catch (Exception e) {
            e.printStackTrace();
        }

        get("/version", (req, res) -> twitter.getVersion());

        get("/notify", (req, res) -> {
            broadcastMessage("HTTP-server", new JSONObject().put("msg", "Notificación de prueba.").toString());
            return "OK";
        });

        get("/getpm/:token", (req, res) -> {
            res.type("application/json;charset=utf-8");

            User user = tokens.get(req.params(":token"));
            if (user == null) {
                return new JSONObject().put("error", "token is not valid");
            }

            List<PrivateMessage> pmList = user.getReceivedPM();
            JSONArray result = new JSONArray();
            for (PrivateMessage pm : pmList) {
                result.put(new JSONObject()
                .put("sender", pm.getSender())
                .put("to", pm.getReceiver())
                .put("body", pm.getBody())
                .put("timestamp", pm.getDate()));
            }
            return result.toString(1);
        });

        get("/timeline/:token", (req, res) -> {
            res.type("application/json");

            User userAuth = tokens.get(req.params(":token"));
            if (userAuth == null) {
                return new JSONObject().put("error", "login incorrecto");
            }

            List<Status> timeline = userAuth.getTimeline();
            JSONArray response = new JSONArray();
            for (Status status : timeline) {

                if (verifiedUsers.contains(status.getUserHandle())) {
                    response.put(new JSONObject()
                            .put("user_handler", status.getUserHandle())
                            .put("verified", true)
                            .put("body", status.getBody())
                    );
                } else {
                    response.put(new JSONObject()
                            .put("user_handler", status.getUserHandle())
                            .put("verified", false)
                            .put("body", status.getBody())
                    );
                }
            }
            response.put(new JSONObject()
                    .put("user_handler", "twitter-rmi")
                    .put("verified", true)
                    .put("body", "¡Bienvenido a twitter-rmi! ¿Qué tal si publicas algún tweet mientras descubres gente estupenda?")
            );
            Thread.sleep(500);
            return response.toString(1);
        });

        get("/login/:username/:password", (req, res) -> {
            res.type("application/json");
            System.out.println("login() -> username: " + req.params(":username"));
            System.out.println("login() -> password: " + req.params(":password"));
            User userLogin = twitter.login(req.params(":username"), req.params(":password"));
            if (userLogin == null) {
                return new JSONObject().put("error", "login incorrecto");
            }
            // users.put(userLogin.getHandle(), userLogin);
            JSONObject response = new JSONObject();
            System.out.println("Bio -> " + userLogin.getBio());

            response.put("bio", userLogin.getBio());
            response.put("handle", userLogin.getHandle());
            String token = UUID.randomUUID().toString();
            response.put("token", token);

            // response.put("follows", userLogin.getFollowing(req.params(":username")));
            List<User> following = userLogin.getFollowing(req.params(":username"));
            JSONArray followingJSON = new JSONArray();
            for (User user : following) {
                followingJSON.put(user.getHandle());
            }
            response.put("follows", followingJSON);

            tokens.put(token, userLogin);
            System.out.println("Logged in as " + userLogin.getHandle());
            System.out.println("login() -> " + userLogin.getHandle() + " - " + token);

            ClientCallback callback = new ClientCallbackImpl();
            userLogin.pushSubscribe(callback);

            return response.toString(1);
        });

        get("/register/:username/:password", (req, res) -> {
            res.type("application/json");
            System.out.println("register() -> username: " + req.params(":username"));
            System.out.println("register() -> password: " + req.params(":password"));
            User userLogin = twitter.register(req.params(":username"), req.params(":password"));
            if (userLogin == null) {
                return new JSONObject().put("error", "login incorrecto");
            }
            // users.put(userLogin.getHandle(), userLogin);
            JSONObject response = new JSONObject();
            response.put("handle", userLogin.getHandle());
            String token = UUID.randomUUID().toString();
            response.put("token", token);
            response.put("follows", new JSONArray());
            response.put("bio", userLogin.getBio());
            tokens.put(token, userLogin);
            System.out.println("Logged in as " + userLogin.getHandle());
            System.out.println("login() -> " + userLogin.getHandle() + " - " + token);
            return response.toString(1);
        });

        get("/meetnewpeople/:token", (req, res) -> {
            res.type("application/json");
            User userAuth = tokens.get(req.params(":token"));
            if (userAuth == null) {
                return new JSONObject().put("error", "login incorrecto");
            }
            JSONArray newPeople = new JSONArray();

            List<User> users = userAuth.getUsers();
            for (User user : users) {
                JSONObject userJSON = new JSONObject();
                userJSON.put("handle", user.getHandle());
                userJSON.put("bio", user.getBio());
                if (verifiedUsers.contains(user.getHandle())) {
                    userJSON.put("verified", true);
                } else {
                    userJSON.put("verified", false);
                }
                newPeople.put(userJSON);
            }
            Thread.sleep(1500);
            return newPeople.toString();
        });

        get("/follow/:token/:username", (req, res) -> {
            res.type("application/json");
            User userAuth = tokens.get(req.params(":token"));
            if (userAuth == null) {
                return new JSONObject().put("error", "login incorrecto");
            }
            userAuth.follow(req.params(":username"));
            System.out.println("El usuario " + userAuth.getHandle() + " ha seguido a " + req.params(":username"));
            return new JSONObject().put("status", "ok");
        });

        get("/unfollow/:token/:username", (req, res) -> {
            res.type("application/json");
            User userAuth = tokens.get(req.params(":token"));
            if (userAuth == null) {
                return new JSONObject().put("error", "login incorrecto");
            }
            userAuth.unfollow(req.params(":username"));
            System.out.println("El usuario " + userAuth.getHandle() + " ha seguido a " + req.params(":username"));
            return new JSONObject().put("status", "ok");
        });

        post("/status/:token", (req, res) -> {
            res.type("application/json");

            User userAuth = tokens.get(req.params(":token"));
            if (userAuth == null) {
                return new JSONObject().put("error", "login incorrecto");
            }

            Thread.sleep(2000);
            userAuth.submitStatus(new String(req.body().getBytes(), "UTF-8"));
            return new JSONObject().put("status", "ok");
        });

        post("/pm/:token", (req, res) -> {
            res.type("application/json");

            User userAuth = tokens.get(req.params(":token"));
            if (userAuth == null) {
                return new JSONObject().put("error", "login incorrecto");
            }

            Thread.sleep(2000);

            JSONObject body = new JSONObject(new String(req.body().getBytes(), "UTF-8"));

            System.out.println("[PM] from " + userAuth.getHandle());
            System.out.println("[PM] to " + body.getString("to"));
            System.out.println("[PM] body " + body.getString("body"));

            userAuth.submitPm(body.getString("body"), body.getString("to"));

            //userAuth.submitStatus(new String(req.body().getBytes(), "UTF-8"));
            return new JSONObject().put("status", "ok");
        });

    }

}
