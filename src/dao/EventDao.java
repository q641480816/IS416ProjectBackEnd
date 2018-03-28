/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import model.Event;
import util.Key;
import util.Value;


/**
 *
 * @author Jeffrey Pan
 */
public class EventDao {    

    public static HashMap<Long, Long> IN_EVENT_USERS;
    public static HashMap<Long, Event> EVENT_LIST;
    private static double EARTH_RADIUS = 6378.137;
    private static final String DISTANCE = "distance";
    
    //0) date, 1)lat, 2)lat
    private static HashMap<Long, ArrayList<String>> SHAKE_LIST;

    public static Collection<Event> getAllEvents(){
        return getEventCount() == 0 ? new ArrayList<Event>() : EVENT_LIST.values();
    }

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
            IN_EVENT_USERS = new HashMap<>();
        }

        EVENT_LIST.put(account_id, new_event);
        IN_EVENT_USERS.put(account_id, new_event.getId());
        return new_event;
    }

    public static Event updateStatus(long event_id, int status){
        Event e = EVENT_LIST.get(event_id);
        e.setStatus(status);
        EVENT_LIST.put(e.getId(),e);
        return e;
    }
    
    public static Event joinEvent(long event_id, long account_id){
        try{
            if (EVENT_LIST.containsKey(event_id)){
                Event e = EVENT_LIST.get(event_id);
                e.addParticipant(account_id);
                IN_EVENT_USERS.put(account_id, event_id);
                EVENT_LIST.put(e.getId(),e);
                return e;
            }else {
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }
    
    public static boolean closeEvent(long event_id){
        if (EVENT_LIST.containsKey(event_id)){
            Event e = EVENT_LIST.remove(event_id);
            for(long uId : e.getParticipants()){
                IN_EVENT_USERS.remove(uId);
            }
            return true;
        }else {
            return false;
        }
    }

    public static List<Event> getEventsInRange(double latitude, double longitude){
        if(EVENT_LIST == null){
            EVENT_LIST = new HashMap<>();
        }

        List<Event> out = EVENT_LIST.values().stream()
                .parallel()
                .filter(d ->  Integer.MAX_VALUE == d.getSizeLimit() && getDistance(d.getLatitude(),d.getLongitude(),latitude,longitude) < 1000)
                .collect(Collectors.toList());

        return out;
    }

    private static double getRadian(double degree) {
        return degree * Math.PI / 180.0;
    }

    private static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        double a = radLat1 - radLat2;// 两点纬度差
        double b = getRadian(lng1) - getRadian(lng2);// 两点的�?度差
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s * 1000;
    }
    
    public static boolean leaveEvent(long event_id, long account_id){
        try{
            if (EVENT_LIST.containsKey(event_id)){
                Event e = EVENT_LIST.get(event_id);
                e.removeParticipant(account_id);
                EVENT_LIST.put(e.getId(),e);
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }
    
    public static Event shakeJoinEvent(long account_id, double lat, double lng) throws ParseException{
        //first user
        if (SHAKE_LIST == null || SHAKE_LIST.size() == 0){
            SHAKE_LIST = new HashMap<>();
                       
            ArrayList<String> userDetails = new ArrayList<>();
            Date time_stamp = new Date();
            userDetails.add(time_stamp.toString());
            userDetails.add("" + lat);
            userDetails.add("" + lng);
            
            SHAKE_LIST.put(account_id, userDetails);
        }
        
        //add if cannot find user in the hashmap
        if(!SHAKE_LIST.containsKey(account_id)){
            ArrayList<String> userDetails = new ArrayList<>();
            Date time_stamp = new Date();
            userDetails.add(time_stamp.toString());
            userDetails.add("" + lat);
            userDetails.add("" + lng);

            SHAKE_LIST.put(account_id, userDetails);
        }
                        
        for(int i = 0; i < SHAKE_LIST.size(); i++){
            Iterator it = SHAKE_LIST.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                ArrayList<String> retrieved_details = SHAKE_LIST.get(pair.getKey());
                
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date user_date = format.parse(retrieved_details.get(0));
                Date now_date = new Date();
                    
                if((now_date.getTime()  - user_date.getTime() ) / 1000 % 60 > 5) {
                    //expired, remove this account from hashmap
                    SHAKE_LIST.remove((Long)pair.getKey());
                }
                else{
                    //check if within radius against other users
                    if((Long)pair.getKey() != account_id){
                        if(getDistance(lat, lng, Double.parseDouble(retrieved_details.get(1)), Double.parseDouble(retrieved_details.get(2))) > 2000){
                            //match!
                            //create event 
                            ArrayList<Long> list_of_participants = new ArrayList<>();
                            list_of_participants.add(account_id);
                            list_of_participants.add((Long)pair.getKey());
                            Event new_event = createNewEvent(account_id, new Event(account_id, lat, lng, "", new Date(), Value.EVENT_STATUS_STARTED, "",  new ArrayList<>(),2));
                            return new_event;
                        }
                        return null;
                    }                    
                }
                
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
        
        return null;
    }

    public static long getUserStatus(long id){
        if (IN_EVENT_USERS == null){
            IN_EVENT_USERS = new HashMap<>();
            return -1;
        }else {
            return IN_EVENT_USERS.get(id);
        }
    }
}
