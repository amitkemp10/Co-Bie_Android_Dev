package com.example.co_bie;

public class MessageList {

    private String id, name, gender, profileImg, lastMessage;
    private int unseenMessages;

    public MessageList(String id, String name, String gender, String profileImg, String lastMessage, int unseenMessages) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.profileImg = profileImg;
        this.lastMessage = lastMessage;
        this.unseenMessages = unseenMessages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
    }
}
