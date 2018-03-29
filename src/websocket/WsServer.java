package websocket;

import util.Key;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@ServerEndpoint("/eventSocket")
public class WsServer {

    public static HashMap<Long,Session> POOL = new HashMap<>();
    @OnOpen
    public void onOpen(Session session){
        try {
            session.getBasicRemote().sendText("status:connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session){
        Set set = POOL.keySet();
        for(Object id: set){
            if (id instanceof Long){
                if (POOL.get(id).equals(session)){
                    POOL.remove(id);
                    break;
                }
            }
        }
        System.out.println(POOL.size());
    }

    @OnMessage
    public String onMessage(String message, Session session){
        try {
            String[] paras = message.split(":");
            switch (paras[0]) {
                case Key.SOCKETREGISTER:
                    POOL.put(Long.parseLong(paras[1]), session);
                    break;
                case Key.SOCKETUNREGISTER:
                    POOL.remove(Long.parseLong(paras[1]));
                    break;
                default:
                    session.getBasicRemote().sendText("not recognized");
            }
            System.out.println(POOL.size());
        }catch (Exception e){
            e.printStackTrace();
        }
        return message;
    }

    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }

    public static void sendMessage(List<Long> ids, String message){
        for (long id : ids){
            if (POOL.containsKey(id)){
                System.out.println("onSend");
                try {
                    POOL.get(id).getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
