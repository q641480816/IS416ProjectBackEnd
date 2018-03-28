/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Jeffrey Pan
 */
@ApplicationScoped
@ServerEndpoint("/eventSocket/{acId}")
public class WebSocketServer {

    private static int onlineCount = 0;
    private static Map<Long, WebSocketServer> clients = new ConcurrentHashMap<Long, WebSocketServer>();
    private Session session;
    private long id;

    @OnOpen
    public void onOpen(@PathParam("acId") long id, Session session) throws IOException {

        this.id = id;
        this.session = session;

        addOnlineCount();
        clients.put(id, this);
        System.out.println("Connected");
    }

    @OnClose
    public void onClose() throws IOException {
        clients.remove(id);
        subOnlineCount();
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        sendMessageTo(message, id);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessageTo(String message, long To) throws IOException {
        // session.getBasicRemote().sendText(message);
        //session.getAsyncRemote().sendText(message);
        for (WebSocketServer item : clients.values()) {
            if (item.id  == To )
                item.session.getAsyncRemote().sendText(message);
        }
    }

    public void sendMessageAll(String message) throws IOException {
        for (WebSocketServer item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }



    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static synchronized Map<Long, WebSocketServer> getClients() {
        return clients;
    }
}
