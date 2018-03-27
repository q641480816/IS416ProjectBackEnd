/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import ctrl.UserCtrl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

import util.Config;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import util.Key;
import util.Value;
/**
 *
 * @author Jeffrey Pan
 */
public class Profile extends HttpServlet{
    public Profile(){
        super();
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            if (inputJson.containsKey(Key.UPDATEPROFILE)){
                returnJson = UserCtrl.updateProfile(inputJson);
            }
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
