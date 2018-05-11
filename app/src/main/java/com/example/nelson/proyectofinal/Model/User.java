package com.example.nelson.proyectofinal.Model;


public class User{
    private String country, dob,fullname,gender,status,username,relationshipstatus,profileimage, email,uid,device_token;


    public User(String country, String dob, String fullname, String gender, String status, String username, String relationshipstatus, String profileimage, String email, String uid, String device_token) {
        this.country = country;
        this.dob = dob;
        this.fullname = fullname;
        this.gender = gender;
        this.status = status;
        this.username = username;
        this.relationshipstatus = relationshipstatus;
        this.profileimage = profileimage;
        this.email = email;
        this.uid = uid;
        this.device_token = device_token;
    }

    public User() {
    }



    public String getToken() {
        return device_token;
    }

    public void setToken(String device_token) {
        this.device_token = device_token;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRelationshipstatus() {
        return relationshipstatus;
    }

    public void setRelationshipstatus(String relationshipstatus) {
        this.relationshipstatus = relationshipstatus;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
