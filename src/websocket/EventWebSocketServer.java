package websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import util.Key;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EventWebSocketServer extends WebSocketServer {
    public static HashMap<Long,WebSocket> pool;
    public static EventWebSocketServer socketServer;

    public static void startServer(){
        pool = new HashMap<>();
        try {
            socketServer = new EventWebSocketServer(9997);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        socketServer.start();
    }

    public EventWebSocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress("35.198.245.25",port));
    }

    public EventWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        webSocket.send("status:connected");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Set set = pool.keySet();
        for(Object id: set){
            if (id instanceof Long){
                if (pool.get(id).equals(conn)){
                    pool.remove(id);
                    break;
                }
            }
        }
        System.out.println(pool.size());
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        try {
            String[] paras = s.split(":");
            switch (paras[0]) {
                case Key.SOCKETREGISTER:
                    pool.put(Long.parseLong(paras[1]), webSocket);
                    break;
                case Key.SOCKETUNREGISTER:
                    pool.remove(Long.parseLong(paras[1]));
                    break;
                default:
                    webSocket.send("not recognized");
            }
            System.out.println(pool.size());
        }catch (Exception e){
            webSocket.send(e.toString());
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println("on error");
        e.printStackTrace();
    }

    @Override
    public void start() {
        super.start();
    }

    public static void sendMessage(List<Long> ids, String message){
        for (long id : ids){
            if (pool.containsKey(id)){
                System.out.println("onSend");
                pool.get(id).send(message);
            }
        }
    }
}
