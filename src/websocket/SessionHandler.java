/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websocket;

import java.io.IOException;
import java.util.ArrayList;
import javax.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import model.User;

/**
 *
 * @author Jeffrey Pan
 */

@ApplicationScoped
public class SessionHandler {
    private final Set<Session> sessions = new HashSet<>();
    private final ArrayList<User> list_of_users = new ArrayList<>();
    
    public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
        for (User user : list_of_users) {
            JsonObject addMessage = createAddMessage(user);
            sendToSession(session, addMessage);
        }
    }
    
    public List<User> getParticipants() {
        return list_of_users;
    }

    public void addParticipants(User new_user) {
        list_of_users.add(new_user);
        JsonObject addMessage = createAddMessage(new_user);
        sendToAllConnectedSessions(addMessage);
    }

    public void removeParticipant(User to_remove_user) {
        for(int i = 0; i< list_of_users.size(); i++){
            if(list_of_users.get(i).getAccountId() == to_remove_user.getAccountId()){
                list_of_users.remove(i);
                JsonProvider provider = JsonProvider.provider();
                JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", "remove")
                    .add("account_id", to_remove_user.getAccountId())
                    .build();
                sendToAllConnectedSessions(removeMessage);
                break;
            }
        }
    }

    private User getParticipantById(Long user_account_id) {
        for(int i = 0; i< list_of_users.size(); i++){
            if(list_of_users.get(i).getAccountId() == user_account_id){
                return list_of_users.get(i);
            }
        }
        
        return null;
    }

     private JsonObject createAddMessage(User user) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "add")
                .add("id", user.getAccountId())
                .add("name", user.getNickName())
                .build();
        
        return addMessage;
    }
    
    private void sendToAllConnectedSessions(JsonObject message) {
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(SessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
