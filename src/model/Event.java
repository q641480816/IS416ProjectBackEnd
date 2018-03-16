/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Date;

import ctrl.UserCtrl;
import dao.UserDao;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.Config;
import util.Key;

/**
 *
 * @author Jeffrey Pan
 */
public class Event {
    private long id;
    private double latitude;
    private double longitude;
    private Date initTime;
    private int status;
    private String type;
    private ArrayList<Long> participants;
    
    public Event() {
        super();
    }
   
    public Event(long id, double latitude, double longitude, Date initTime, int status, String type, ArrayList<Long> participants) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.initTime = initTime;
        this.status = status;
        this.type = type;
        this.participants = participants;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getInitTime() {
        return initTime;
    }

    public void setInitTime(Date initTime) {
        this.initTime = initTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Long> getParticipants() {
        return participants;
    }

    public void addParticipant(long id){
        ArrayList<Long> ps = getParticipants();
        ps.add(id);
        setParticipants(ps);
    }

    public void setParticipants(ArrayList<Long> participants) {
        this.participants = participants;
    }
    
    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        obj.put(Key.ID, this.id);
        obj.put(Key.LATITUDE, this.latitude);
        obj.put(Key.LONGITUDE, this.longitude);
        obj.put(Key.INITTIME, Config.SDF.format(this.initTime));
        obj.put(Key.EVENTSTATUS, this.status);
        obj.put(Key.TYPE, this.type);
        obj.put(Key.OWNER, UserDao.getAccountById(this.id).toJson());
        JSONArray participants = new JSONArray();
        ArrayList<User> users = UserDao.getUsersByIds(this.participants);
        for (User u : users){
            participants.add(u.toJson());
        }
        obj.put(Key.PARTICIPANTS, participants);
        return obj;
    }
    
    
}
