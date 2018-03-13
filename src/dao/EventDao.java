/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import model.Event;


/**
 *
 * @author Jeffrey Pan
 */
public class EventDao {    
    
    private static HashMap<Long, Event> EVENT_LIST;
    
    public static int getEventCount(){
        if(EVENT_LIST == null || EVENT_LIST.isEmpty()){
            return 0;
        }
        
        return EVENT_LIST.size();
    }

    public static Event getEventById(long id){
        return EVENT_LIST.get(id);
    }
    
    public static Event createNewEvent(long account_id, Event new_event){
            if(EVENT_LIST == null){
                EVENT_LIST = new HashMap<>();
            }

            EVENT_LIST.put(account_id, new_event);
            return new_event;
    }

    public static Event updateStatus(long event_id, int status){
        Event e = EVENT_LIST.get(event_id);
        e.setStatus(status);
        EVENT_LIST.put(e.getId(),e);
        return e;
    }
    
    public static Event joinEvent(int event_id, long account_id){
        try{
            if (EVENT_LIST.containsKey(event_id)){
                Event e = EVENT_LIST.get(event_id);
                e.addParticipant(account_id);
                EVENT_LIST.put(e.getId(),e);
                return e;
            }else {
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }

    public static boolean closeEvent(int event_id){
        if (EVENT_LIST.containsKey(event_id)){
            EVENT_LIST.remove(event_id);
            return true;
        }else {
            return false;
        }
    }
}
