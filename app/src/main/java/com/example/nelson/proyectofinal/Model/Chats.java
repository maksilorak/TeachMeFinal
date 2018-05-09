package com.example.nelson.proyectofinal.Model;

public class Chats {

    private String messageUid, messageText, messageUserName, messageDate, messageTime, messageProfileImage;

    public Chats(String messageUid, String messageText, String messageUserName, String messageDate, String messageTime, String messageProfileImage) {
        this.messageUid = messageUid;
        this.messageText = messageText;
        this.messageUserName = messageUserName;
        this.messageDate = messageDate;
        this.messageTime = messageTime;
        this.messageProfileImage = messageProfileImage;
    }

    public Chats() {
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUserName() {
        return messageUserName;
    }

    public void setMessageUserName(String messageUserName) {
        this.messageUserName = messageUserName;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageProfileImage() {
        return messageProfileImage;
    }

    public void setMessageProfileImage(String messageProfileImage) {
        this.messageProfileImage = messageProfileImage;
    }
}
