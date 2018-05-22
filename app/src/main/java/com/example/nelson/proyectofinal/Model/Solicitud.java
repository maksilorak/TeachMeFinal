package com.example.nelson.proyectofinal.Model;

public class Solicitud {
    private String from, to , status_request;

    public Solicitud() {
    }

    public Solicitud(String from, String to, String status_request) {
        this.from = from;
        this.to = to;
        this.status_request = status_request;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStatus_request() {
        return status_request;
    }

    public void setStatus_request(String status_request) {
        this.status_request = status_request;
    }
}
