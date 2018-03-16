/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import ctrl.EventCtrl;

import javax.naming.event.EventContext;
import javax.servlet.Servlet;
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
public class Event extends HttpServlet{
    public Event(){
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(Config.ENCODING);
        response.setCharacterEncoding(Config.ENCODING);
        response.setContentType(Config.CONTENTTYPE);
        JSONObject returnJson = new JSONObject();

        try{
            String[] args = request.getPathInfo().split("/");
            if (args.length == 3){
                String[] subArgs = args[2].split(",");
                returnJson = EventCtrl.getEventsInRange(Double.parseDouble(subArgs[0]),Double.parseDouble(subArgs[1]));
            }else if(args.length == 2){
                System.out.println(request.getPathInfo());
                returnJson = EventCtrl.getEvent(Long.parseLong(args[1]));
            }else {
                returnJson = EventCtrl.getEvents();
            }
            response.setStatus(200);
        }catch (Exception e){
            response.setStatus(403);
            e.printStackTrace();
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
        }
        response.getWriter().println(returnJson.toJSONString());
    }

    @Override
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
            
            returnJson = EventCtrl.createEvent(inputJson);
            
            response.setStatus(200);
        } catch(Exception e) {
            response.setStatus(403);
            e.printStackTrace();
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
        }

        response.getWriter().println(returnJson.toJSONString());
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
            if (inputJson.containsKey(Key.ACCOUNTID)){
                returnJson = EventCtrl.joinEvent(inputJson);
            }else if(inputJson.containsKey(Key.EVENTSTATUS)){
                returnJson = EventCtrl.updateEventStatus(inputJson);
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
