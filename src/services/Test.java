package services;

import util.Config;
import util.Key;
import util.Value;
import org.json.simple.JSONObject;
import websocket.EventWebSocketServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Test extends HttpServlet {

    public Test(){
        super();
    }

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(Config.ENCODING);
        response.setCharacterEncoding(Config.ENCODING);
        response.setContentType(Config.CONTENTTYPE);

        JSONObject returnJson = new JSONObject();
        try {
            JSONObject obj = new JSONObject();
            obj.put(Key.MESSAGE,"Test respond successfully");
            EventWebSocketServer.startServer();
            returnJson.put(Key.STATUS, Value.SUCCESS);
            returnJson.put(Key.DATA, obj);
            response.setStatus(200);
        } catch(Exception e) {
            e.printStackTrace();
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
            response.setStatus(500);
        }

        response.getWriter().println(returnJson.toJSONString());
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(Config.ENCODING);
        response.setCharacterEncoding(Config.ENCODING);
        response.setContentType(Config.CONTENTTYPE);
    }

    @SuppressWarnings("unchecked")
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(Config.ENCODING);
        response.setCharacterEncoding(Config.ENCODING);
        response.setContentType(Config.CONTENTTYPE);
    }

    @SuppressWarnings("unchecked")
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(Config.ENCODING);
        response.setCharacterEncoding(Config.ENCODING);
        response.setContentType(Config.CONTENTTYPE);
    }
}
