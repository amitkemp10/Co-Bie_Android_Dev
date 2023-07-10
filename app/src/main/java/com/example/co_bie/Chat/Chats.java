package com.example.co_bie.Chat;

public class Chats {
    String sender, sender_name, receiver, message;

    public Chats(String sender, String receiver, String message, String sender_name) {
        this.sender = sender;
        this.sender_name = sender_name;
        this.receiver = receiver;
        this.message = message;
    }

    public Chats() {

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    @Override
    public String toString() {
        return "Chats{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
