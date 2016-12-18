package com.twitter.rmi.web;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

/**
 * Created by jrevillas on 15/12/2016.
 */
@WebSocket
public class NotificationsSocketHandler {

    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + WebLauncher.nextUserNumber++;
        WebLauncher.userUsernameMap.put(user, username);
        WebLauncher.userMap.put(username, user);
        System.out.println("Servicio se notificaciones: se acaba de conectar " + username);
        // WebLauncher.broadcastMessage(sender = "Server", msg = (username + " joined the chat"));
        WebLauncher.sendMessage(username, "holi que tal");
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = WebLauncher.userUsernameMap.get(user);
        WebLauncher.userUsernameMap.remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        if (message.startsWith("handle:")) {
            WebLauncher.socketConnections.put(message.substring(7), user);
            System.out.println("[WEBSOCKETS] El usuario " + message.substring(7) + " se ha identificado.");
            WebLauncher.sendMessage(message.substring(7), "Conectado al servicio de notificaciones.");
        } else {
            System.out.println("[WEBSOCKETS] Mensaje anormal recibido: " + message);
        }
        // WebLauncher.broadcastMessage(sender = WebLauncher.userUsernameMap.get(user), msg = message);
    }

}
