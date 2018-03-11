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
    
    public static Event createNewEvent(long account_id, Event new_event){
            if(EVENT_LIST == null){
                EVENT_LIST = new HashMap<>();
            }

            EVENT_LIST.put(account_id, new_event);
            return new_event;
    }
    
    public static ArrayList<Event> getAllUserEvents(long account_id){
        /*ArrayList<Event> all_user_events = new ArrayList<>();
        
        Iterator it = EVENT_LIST.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            
            ArrayList<Event> list_of_events_created = EVENT_LIST.get((Long) pair.getKey());
                        
            for(int i = 0; i < list_of_events_created.size(); i++){
                Event one_event = list_of_events_created.get(i);

                if((Long) pair.getKey() == account_id){
                    all_user_events.add(one_event);
                }
                else{
                     ArrayList<Long> one_event_participants = one_event.getParticipants();

                    for(int j = 0; j < one_event_participants.size(); j++){
                        if(one_event_participants.get(j) == account_id){
                            all_user_events.add(one_event);
                            break;
                        }
                    }
                }                   
            }           
                        
            it.remove(); // avoids a ConcurrentModificationException
        }*/
        
        return null;
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
