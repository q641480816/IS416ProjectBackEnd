/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ctrl;

import dao.EventDao;
import java.util.ArrayList;
import java.util.Date;
import model.Event;
import org.json.simple.JSONObject;
import util.Key;
import util.Message;
import util.Value;

/**
 *
 * @author Jeffrey Pan
 */
public class EventCtrl {
    public static JSONObject createEvent(JSONObject inputJson) {
        JSONObject returnJson = new JSONObject();
        try {
            int new_id = EventDao.getEventCount() + 1;            
            double latitude = (double) inputJson.get(Key.LATITUDE);
            double longitude = (double) inputJson.get(Key.LONGITUDE);
            Date init_time = (Date) inputJson.get(Key.INITTIME);
            int event_status = (int) inputJson.get(Key.EVENTSTATUS);
            String type = (String) inputJson.get(Key.TYPE);

            ArrayList<Long> list_of_participants = new ArrayList<>();
            list_of_participants.add((Long) inputJson.get(Key.ACCOUNTID));
            
            Event new_event = new Event(new_id, latitude, longitude, init_time, event_status, type, list_of_participants);
            
            if(EventDao.createNewEvent((Long) inputJson.get(Key.ACCOUNTID), new_event) == 1){
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
            
            boolean join_success = EventDao.joinEvent(id, account_id);
                        
            if(join_success){
                JSONObject content = new JSONObject();
                
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
