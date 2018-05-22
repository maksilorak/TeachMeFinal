package com.example.nelson.proyectofinal.Model;

public class Requests {
    private String status_request,ID, friendsAlready;

    public Requests() {
    }

    public Requests(String status_request, String ID, String friendsAlready) {
        this.status_request = status_request;
        this.ID = ID;
        this.friendsAlready = friendsAlready;
    }

    public String getStatus_request() {
        return status_request;
    }

    public void setStatus_request(String status_request) {
        this.status_request = status_request;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFriendsAlready() {
        return friendsAlready;
    }

    public void setFriendsAlready(String friendsAlready) {
        this.friendsAlready = friendsAlready;
    }
}
