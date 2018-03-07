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
    
    private static HashMap<Long, ArrayList<Event>> listOfEvents;
    
    public static int getEventCount(){
        if(listOfEvents.isEmpty() || listOfEvents == null){
            return 0;
        }
        
        return listOfEvents.size();
    }
    
    public static int createNewEvent(long account_id, Event new_event){
        try{
            if(listOfEvents.get(account_id) == null){
                ArrayList<Event> account_events = new ArrayList<>();
                listOfEvents.put(account_id, account_events);
            }
            else{
                ArrayList<Event> account_events = listOfEvents.get(account_id);
                account_events.add(new_event);
                listOfEvents.put(account_id, account_events);
            }
        
            return 1;
        }
        catch(Exception e){
            return -1;
        }
    }
    
    public static ArrayList<Event> getEventCreatedByUser(long account_id){
        return listOfEvents.get(account_id);
    }
    
    public static ArrayList<Event> getAllUserEvents(long account_id){
        ArrayList<Event> all_user_events = new ArrayList<>();
        
        Iterator it = listOfEvents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            
            ArrayList<Event> list_of_events_created = listOfEvents.get((Long) pair.getKey());
                        
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
        }
        
        return all_user_events;
    }
}
