/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import javax.websocket.server.ServerEndpoint;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
/**
 *
 * @author Jeffrey Pan
 */
@ApplicationScoped
@ServerEndpoint("/actions")
public class WebSocketServer {
    
    @Inject
    private SessionHandler sessionHandler;
    
    @OnOpen
    public void open(Session session) {
        sessionHandler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
        public void handleMessage(String message, Session session) {
            try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            if ("add".equals(jsonMessage.getString("action"))) {
                //add new user to the server session
                //sessionHandler.addParticipants(user);
            }

            if ("remove".equals(jsonMessage.getString("action"))) {
                //remove user from the server session
                //long id = (long) jsonMessage.getInt("account_id");
                //User user = sessionHandler.getParticipantById(id);
                //sessionHandler.removeParticipant(user);
            }
        }
    }
}
