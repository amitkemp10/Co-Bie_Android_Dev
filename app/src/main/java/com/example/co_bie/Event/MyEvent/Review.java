package com.example.co_bie.Event.MyEvent;

import com.example.co_bie.LoginAndRegistration.User;

public class Review {
    private String type;
    private int rating;
    private String description;
    private String title;
    private User writer;

    public Review(String type, String title, String description, int rating, User writer) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.writer = writer;
    }

    public Review() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }
}
