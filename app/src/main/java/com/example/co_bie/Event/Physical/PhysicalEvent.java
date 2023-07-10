package com.example.co_bie.Event.Physical;

import com.example.co_bie.Event.Date;
import com.example.co_bie.Event.Duration;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.Time;
import com.example.co_bie.Event.Utils;
import com.example.co_bie.Hobby.Hobby;
import com.example.co_bie.LoginAndRegistration.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PhysicalEvent extends Event {
    private Location location;

    public PhysicalEvent(String eventName, Date eventDate, Time eventTime, Duration eventDuration, String eventDescription, HashMap<String,User> participants, Hobby hobby, String managerUid, String event_img, Location location) {
        super(eventName, eventDate, eventTime, eventDuration, eventDescription, participants, hobby, managerUid, event_img);
        this.location = location;
    }

    public PhysicalEvent(String eventName, Date eventDate, Time eventTime, Duration eventDuration, String eventDescription, HashMap<String,User> participants, Hobby hobby, String managerUid, String event_img, Location location, String eventId) {
        super(eventName, eventDate, eventTime, eventDuration, eventDescription, participants, hobby, managerUid, event_img, eventId);
        this.location = location;
    }

    public PhysicalEvent() {
    }

    //TODO: Add Location member

    @Override
    public Utils.EventType getEventType() {
        return Utils.EventType.PHYSICAL;
    }

    // Getter for location
    public Location getLocation() {
        return location;
    }

    // Setter for location
    public void setLocation(Location location) {
        this.location = location;
    }

}