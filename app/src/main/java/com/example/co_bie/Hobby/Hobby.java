package com.example.co_bie.Hobby;

public class Hobby {
    private String hobby_name;
    private int img_hobby;

    public Hobby(String hobbie_name, int img_hobbie) {
        this.hobby_name = hobbie_name;
        this.img_hobby = img_hobbie;
    }

    public Hobby() {

    }

    public String getHobby_name() {
        return hobby_name;
    }

    public void setHobby_name(String hobbie_name) {
        this.hobby_name = hobbie_name;
    }

    public int getImg_hobby() {
        return img_hobby;
    }

    public void setImg_hobby(int img_hobby) {
        this.img_hobby = img_hobby;
    }
}
