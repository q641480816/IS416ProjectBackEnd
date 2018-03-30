/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.*;
import java.util.stream.Collectors;

import model.Event;
import util.Key;
import util.Value;
import websocket.WsServer;


/**
 *
 * @author Jeffrey Pan
 */
public class EventDao {    

    public static HashMap<Long, Long> IN_EVENT_USERS;
    public static HashMap<Long, Long> PENDING_IN_EVENT_USERS;
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

        WsServer.sendToAllExcept(IN_EVENT_USERS.keySet(), Key.SOCKETADDNEWEVENT + ":" + new_event.getId());
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

                //Socket update

                WsServer.sendMessageExcept(e.getParticipants(), account_id,Key.SOCKETUPDATE + ":DADAS");
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
            List<Long> uIds = e.getParticipants();
            for(long uId : uIds){
                IN_EVENT_USERS.remove(uId);
            }

            //update socket
            WsServer.sendMessageExcept(uIds, event_id ,Key.SOCKETCLOSE + ":DADAS");
            WsServer.sendToAllExcept(uIds.stream().collect(Collectors.toSet()), Key.SOCKETREMOVE + ":" + event_id);
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
                .filter(d ->  Integer.MAX_VALUE == d.getSizeLimit() && getDistance(d.getLatitude(),d.getLongitude(),latitude,longitude) < 10000)
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
                IN_EVENT_USERS.remove(account_id);

                //update
                WsServer.sendMessageExcept(e.getParticipants(), account_id, Key.SOCKETUPDATE + ":DADAS");
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }
    
    public static Event shakeJoinEvent(long account_id, double lat, double lng, String location, boolean lastCall) throws Exception{
        if (EVENT_LIST == null){
            EVENT_LIST = new HashMap<>();
        }
        if (PENDING_IN_EVENT_USERS == null){
            PENDING_IN_EVENT_USERS = new HashMap<>();
        }
        //check if new
        Event e;
        if (!EVENT_LIST.containsKey(account_id)){
            //new
            List<Event> out = EVENT_LIST.values().stream()
                    .parallel()
                    .filter(d ->  2 == d.getSizeLimit() && getDistance(d.getLatitude(),d.getLongitude(),lat,lng) < 2000)
                    .sorted((d1,d2)-> (int) (getDistance(d1.getLatitude(),d1.getLongitude(),lat,lng) - getDistance(d2.getLatitude(),d2.getLongitude(),lat,lng)))
                    .collect(Collectors.toList());
            if (out.size() == 0){
                //new event
                ArrayList<Long> ps = new ArrayList<>();
                ps.add(account_id);
                e = new Event(account_id, lat, lng, location, new Date(), Value.EVENT_STATUS_JOINING, "DATE", ps,2);
                EVENT_LIST.put(account_id,e);
            }else {
                e = out.get(0);
                e.addParticipant(account_id);
                EVENT_LIST.put(e.getId(), e);
            }
            PENDING_IN_EVENT_USERS.put(account_id, e.getId());
            if (e.getParticipants().size() == 2){
                List<Long> us = e.getParticipants();
                for (long u : us){
                    PENDING_IN_EVENT_USERS.remove(u);
                    IN_EVENT_USERS.put(u, e.getId());
                }
            }
        }else {
            e = EVENT_LIST.get(account_id);
        }

        if (lastCall && e.getParticipants().size() < 2){
            EVENT_LIST.remove(e.getId());
            List<Long> us = e.getParticipants();
            for (long u : us){
                PENDING_IN_EVENT_USERS.remove(u);
            }
        }

        System.out.println(PENDING_IN_EVENT_USERS);
        return e;
    }

    public static long getUserStatus(long id){
        if (IN_EVENT_USERS == null){
            IN_EVENT_USERS = new HashMap<>();
            return -1;
        }else {
            if (IN_EVENT_USERS.containsKey(id)) {
                return IN_EVENT_USERS.get(id);
            }else {
                return -1;
            }
        }
    }
}
