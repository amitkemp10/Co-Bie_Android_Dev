package com.example.co_bie.LoginAndRegistration;

import com.example.co_bie.Hobby.Hobby;

import java.util.List;

public class User {
    private String email, full_name, username, password, birth_date, gender, profile_img, status;
    private List<Hobby> hobbiesList;
    private List<String> reported_by;

    public User(String email, String full_name, String username, String password, String birth_date, String gender, String profile_img, List<Hobby> hobbiesList, String status, List<String> reported_by) {
        this.email = email;
        this.full_name = full_name;
        this.username = username;
        this.password = password;
        this.birth_date = birth_date;
        this.gender = gender;
        this.profile_img = profile_img;
        this.hobbiesList = hobbiesList;
        this.status = status;
        this.reported_by = reported_by;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public List<Hobby> getHobbiesList() {
        return hobbiesList;
    }

    public void setHobbiesList(List<Hobby> hobbiesList) {
        this.hobbiesList = hobbiesList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getReported_by() {
        return reported_by;
    }

    public void setReported_by(List<String> reported_by) {
        this.reported_by = reported_by;
    }
}


