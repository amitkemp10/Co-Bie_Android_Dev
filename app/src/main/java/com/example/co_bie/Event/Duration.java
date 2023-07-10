package com.example.co_bie.Event;

public class Duration {

    private int hours;
    private int minutes;

    public Duration(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public Duration() {

    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return String.format("%dh %02dm", hours, minutes);
    }
}
