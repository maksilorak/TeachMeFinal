package com.example.nelson.proyectofinal.Model;

public class Friends {
    private String isYourFriend,ID_Friend,date;

    public Friends() {

    }

    public Friends(String isYourFriend, String ID_Friend, String date) {
        this.isYourFriend = isYourFriend;
        this.ID_Friend = ID_Friend;
        this.date = date;
    }

    public String getIsYourFriend() {
        return isYourFriend;
    }

    public void setIsYourFriend(String isYourFriend) {
        this.isYourFriend = isYourFriend;
    }

    public String getID_Friend() {
        return ID_Friend;
    }

    public void setID_Friend(String ID_Friend) {
        this.ID_Friend = ID_Friend;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
