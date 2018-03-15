/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import model.Event;


/**
 *
 * @author Jeffrey Pan
 */
public class EventDao {    
    
    private static HashMap<Long, Event> EVENT_LIST;
    private static double EARTH_RADIUS = 6378.137;
    private static final String DISTANCE = "distance";
    
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

    public static List<Event> getEventsInRange(double latitude, double longitude){
        if(EVENT_LIST == null){
            EVENT_LIST = new HashMap<>();
        }

        List<Event> out = EVENT_LIST.values().stream()
                .parallel()
                .filter(d -> {
                    return getDistance(d.getLatitude(),d.getLongitude(),latitude,longitude) < 1000;
                }).collect(Collectors.toList());

        return out;
    }

    private static double getRadian(double degree) {
        return degree * Math.PI / 180.0;
    }

    private static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        double a = radLat1 - radLat2;// 两点纬度差
        double b = getRadian(lng1) - getRadian(lng2);// 两点的经度差
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s * 1000;
    }
}
