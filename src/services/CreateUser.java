package services;

import ctrl.UserCtrl;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import util.Config;
import util.Key;
import util.Value;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class CreateUser extends HttpServlet {

    public CreateUser(){
        super();
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(Config.ENCODING);
        response.setCharacterEncoding(Config.ENCODING);
        response.setContentType(Config.CONTENTTYPE);

        StringBuffer jb = new StringBuffer();
        String line = null;
        JSONObject returnJson = new JSONObject();

        try {
            BufferedReader reader = request.getReader();
            
            while((line = reader.readLine()) != null) {
                jb.append(line);
            }
            
            JSONObject inputJson = (JSONObject) JSONValue.parse(jb.toString());
            
            returnJson = UserCtrl.createAccount(inputJson);
            
            response.setStatus(200);
        } catch(Exception e) {
            response.setStatus(403);
            e.printStackTrace();
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
        }

        response.getWriter().println(returnJson.toJSONString());
    }

}
