package com.example.co_bie.Event;

import com.example.co_bie.Event.MyEvent.Review;
import com.example.co_bie.Hobby.Hobby;
import com.example.co_bie.LoginAndRegistration.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class Event {

    private String eventId;
    private String eventName;
    private Date eventDate;
    private Time eventTime;
    private Duration eventDuration;
    private String eventDescription;
    private HashMap<String, User> participants;
    private Hobby hobby;
    private String managerUid;
    private String event_img;
    private ArrayList<Review> reviews;

    public Event(String eventName, Date eventDate, Time eventTime, Duration eventDuration, String eventDescription, HashMap<String, User> participants, Hobby hobby, String managerUid, String event_img) {
        this.eventName = eventName;
        this.eventId = String.valueOf(UUID.randomUUID());
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventDuration = eventDuration;
        this.eventDescription = eventDescription;
        this.participants = participants;
        this.hobby = hobby;
        this.managerUid = managerUid;
        this.event_img = event_img;
        reviews = new ArrayList<>();
    }

    public Event(String eventName, Date eventDate, Time eventTime, Duration eventDuration, String eventDescription, HashMap<String, User> participants, Hobby hobby, String managerUid, String event_img, String eventId) {
        this.eventName = eventName;
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventDuration = eventDuration;
        this.eventDescription = eventDescription;
        this.participants = participants;
        this.hobby = hobby;
        this.managerUid = managerUid;
        this.event_img = event_img;
        reviews = new ArrayList<>();
    }

    public Event() {

    }

    public abstract Utils.EventType getEventType();

    public String getID() {
        return eventId;
    }

    public void setID(String eventId) {
        this.eventId = eventId;
    }

    // Getter for eventName
    public String getEventName() {
        return eventName;
    }

    // Setter for eventName
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    // Getter for eventDate
    public Date getEventDate() {
        return eventDate;
    }

    // Setter for eventDate
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    // Getter for eventTime
    public Time getEventTime() {
        return eventTime;
    }

    // Setter for eventTime
    public void setEventTime(Time eventTime) {
        this.eventTime = eventTime;
    }

    // Getter for eventDuration
    public Duration getEventDuration() {
        return eventDuration;
    }

    // Setter for eventDuration
    public void setEventDuration(Duration eventDuration) {
        this.eventDuration = eventDuration;
    }

    // Getter for eventDescription
    public String getEventDescription() {
        return eventDescription;
    }

    // Setter for eventDescription
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    // Getter for participants
    public HashMap<String, User> getParticipants() {
        return participants;
    }

    // Setter for participants
    public void setParticipants(HashMap<String, User> participants) {
        this.participants = participants;
    }

    // Getter for hobby
    public Hobby getHobby() {
        return hobby;
    }

    // Setter for hobby
    public void setHobby(Hobby hobby) {
        this.hobby = hobby;
    }

    public String getManagerUid() {
        return managerUid;
    }

    public void setManagerUid(String managerUid) {
        this.managerUid = managerUid;
    }

    public String getEvent_img() {
        return event_img;
    }

    public void setEvent_img(String event_img) {
        this.event_img = event_img;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    // Setter for participants
    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public boolean isPassed(){
        return eventDate.isPassed();
    }

}
