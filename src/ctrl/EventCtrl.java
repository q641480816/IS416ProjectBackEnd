/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctrl;

import dao.EventDao;
import model.Event;
import org.json.simple.JSONObject;
import util.Key;
import util.Message;
import util.Value;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Jeffrey Pan
 */
public class EventCtrl {

    public static JSONObject getEvent(JSONObject inputJson){
        JSONObject returnJson = new JSONObject();
        try {
            Long event_id = (Long) inputJson.get(Key.ID);
            Event e = EventDao.getEventById(event_id);
            if (e != null){
                returnJson.put(Key.STATUS, Value.SUCCESS);
                returnJson.put(Key.DATA, e.toJson());
            }else {
                returnJson.put(Key.STATUS, Value.FAIL);
                returnJson.put(Key.MESSAGE, "NO EVENT");
            }
        }catch (Exception e){
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
        }
        return returnJson;
    }

    public static JSONObject createEvent(JSONObject inputJson) {
        JSONObject returnJson = new JSONObject();
        try {
            Long event_id = (Long) inputJson.get(Key.ACCOUNTID);
            double latitude = (double) inputJson.get(Key.LATITUDE);
            double longitude = (double) inputJson.get(Key.LONGITUDE);
            Date init_time = (Date) inputJson.get(Key.INITTIME);
            int event_status = 0;
            String type = (String) inputJson.get(Key.TYPE);

            ArrayList<Long> list_of_participants = new ArrayList<>();
            list_of_participants.add((Long) inputJson.get(Key.ACCOUNTID));
            
            Event new_event = new Event(event_id, latitude, longitude, init_time, event_status, type, list_of_participants);
            
            if(EventDao.createNewEvent(event_id, new_event) != null){
                JSONObject content = new JSONObject();
                content.put(Key.EVENT, new_event.toJson());
                                
                returnJson.put(Key.STATUS, Value.SUCCESS);
                returnJson.put(Key.DATA, content); 
            }else{
                returnJson.put(Key.STATUS, Value.FAIL);
                returnJson.put(Key.MESSAGE, Message.USER_EXIST);
            }
        }catch (Exception e){
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
        }
        return returnJson;
    }
    
    public static JSONObject joinEvent(JSONObject inputJson) {
        JSONObject returnJson = new JSONObject();
        try {
            int id = (int) inputJson.get(Key.ID);            
            int account_id = (int) inputJson.get(Key.ACCOUNTID);
            Event event = EventDao.joinEvent(id, account_id);
            if(event != null){
                returnJson.put(Key.DATA, event.toJson());
                returnJson.put(Key.STATUS, Value.SUCCESS);
            }else{
                returnJson.put(Key.STATUS, Value.FAIL);
            }
        }catch (Exception e){
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
        }
        return returnJson;
    }
}
