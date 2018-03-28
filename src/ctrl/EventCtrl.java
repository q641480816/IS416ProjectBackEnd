/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctrl;

import dao.EventDao;
import model.Event;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.Key;
import util.Message;
import util.Value;

import javax.json.Json;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Jeffrey Pan
 */
public class EventCtrl {

    public static JSONObject getEvents(){
        JSONObject returnJson = new JSONObject();
        try {
            JSONArray data = new JSONArray();
            for (Event e : EventDao.getAllEvents()){
                data.add(e.toJson());
            }
            returnJson.put(Key.STATUS, Value.SUCCESS);
            returnJson.put(Key.DATA, data);
        }catch (Exception e){
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
        }
        return returnJson;
    }

    public static JSONObject getEvent(long event_id){
        JSONObject returnJson = new JSONObject();
        try {
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

    public static JSONObject getEventsInRange(double la, double lo){
        JSONObject returnJson = new JSONObject();

        try {
            JSONArray jsonArray = new JSONArray();
            List<Event> data = EventDao.getEventsInRange(la,lo);
            for(Event e : data){
                jsonArray.add(e.toJson());
            }
            returnJson.put(Key.DATA, jsonArray);
            returnJson.put(Key.STATUS, Value.SUCCESS);
        }catch (Exception e){
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
        }
        return returnJson;
    }

    public static JSONObject createEvent(JSONObject inputJson) {
        JSONObject returnJson = new JSONObject();
        try {
            Long account_id = (Long) inputJson.get(Key.ACCOUNTID);
            double latitude = (double) inputJson.get(Key.LATITUDE);
            double longitude = (double) inputJson.get(Key.LONGITUDE);
            String location = (String)  inputJson.get(Key.LOCATION);
            Date init_time = new Date();
            int event_status = Value.EVENT_STATUS_JOINING;
            String type = (String) inputJson.get(Key.TYPE);

            ArrayList<Long> list_of_participants = new ArrayList<>();
            list_of_participants.add(account_id);
            
            Event new_event = new Event(account_id, latitude, longitude, location, init_time, event_status, type, list_of_participants);
            
            if(EventDao.createNewEvent(account_id, new_event) != null){
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
            long id = (long) inputJson.get(Key.ID);
            long account_id = (long) inputJson.get(Key.ACCOUNTID);
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
    
    public static JSONObject updateEventStatus(JSONObject inputJson){
        JSONObject returnJson = new JSONObject();
        try {
            long id = (long) inputJson.get(Key.ID);
            long status = (long) inputJson.get(Key.EVENTSTATUS);
            if (status == Value.EVENT_STATUS_STOPPED){
                if(EventDao.closeEvent(id)){
                    returnJson.put(Key.STATUS, Value.SUCCESS);
                }else {
                    returnJson.put(Key.STATUS, Value.FAIL);
                }
            }
        }catch (Exception e){
            returnJson.put(Key.STATUS, Value.EXCEPTION);
            returnJson.put(Key.EXCEPTION, e.getMessage());
        }
        return returnJson;
    }
    
    public static JSONObject leaveEvent(JSONObject inputJson) {
        JSONObject returnJson = new JSONObject();
        try {
            long id = (long) inputJson.get(Key.ID);
            long account_id = (long) inputJson.get(Key.ACCOUNTID);
            boolean leave_status = EventDao.leaveEvent(id, account_id);
            if(leave_status == true){
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
    
    public static JSONObject shakeJoinEvent(JSONObject inputJson) {
        JSONObject returnJson = new JSONObject();
        try {
            long account_id = (long) inputJson.get(Key.ACCOUNTID);
            double latitude = (double) inputJson.get(Key.LATITUDE);
            double longitude = (double) inputJson.get(Key.LONGITUDE);
            String location = (String) inputJson.get(Key.LOCATION);
            boolean lastCall = (boolean) inputJson.get(Key.LASTCALL);


            Event shake_event = EventDao.shakeJoinEvent(account_id, latitude, longitude,location);
            if (lastCall && shake_event.getParticipants().size() < 2){
                EventDao.closeEvent(shake_event.getId());
            }
            if(shake_event != null){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Key.ID, shake_event.getId());
                jsonObject.put(Key.EVENTSTATUS, shake_event.getParticipants().size() == 2);
                returnJson.put(Key.DATA, jsonObject);
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
