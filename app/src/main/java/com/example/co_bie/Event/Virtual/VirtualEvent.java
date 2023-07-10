package com.example.co_bie.Event.Virtual;


import com.example.co_bie.Event.Date;
import com.example.co_bie.Event.Duration;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.Time;
import com.example.co_bie.Event.Utils;
import com.example.co_bie.Hobby.Hobby;
import com.example.co_bie.LoginAndRegistration.User;

import java.util.ArrayList;
import java.util.HashMap;

public class VirtualEvent extends Event {

    private Utils.Platform eventPlatform;
    private String meetingLink;

    public VirtualEvent(String eventName, Date eventDate, Time eventTime, Duration eventDuration, String eventDescription,
                        HashMap<String,User> participants, Hobby hobby, String managerUid, String event_img, Utils.Platform eventPlatform, String meetingLink) {
        super(eventName, eventDate, eventTime, eventDuration, eventDescription, participants, hobby, managerUid, event_img);
        this.eventPlatform = eventPlatform;
        this.meetingLink = meetingLink;
    }

    public VirtualEvent(String eventName, Date eventDate, Time eventTime, Duration eventDuration, String eventDescription,
                        HashMap<String,User> participants, Hobby hobby, String managerUid, String event_img, Utils.Platform eventPlatform, String meetingLink, String eventId) {
        super(eventName, eventDate, eventTime, eventDuration, eventDescription, participants, hobby, managerUid, event_img, eventId);
        this.eventPlatform = eventPlatform;
        this.meetingLink = meetingLink;
    }

    public VirtualEvent() {}

    public void setEventPlatform(Utils.Platform eventPlatform) {
        this.eventPlatform = eventPlatform;
    }

    public Utils.Platform getEventPlatform() {
        return eventPlatform;
    }

    @Override
    public Utils.EventType getEventType() {
        return Utils.EventType.VIRTUAL;
    }

    public String getMeetingLink() {
        if(meetingLink != null)
            return meetingLink;
        return "No Link";
    }
}
