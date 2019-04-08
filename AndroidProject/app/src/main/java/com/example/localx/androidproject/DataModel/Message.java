package com.example.localx.androidproject.DataModel;

public class Message {
    private String user;
    private String text;
    private int messageColor;

    public Message() {

    }

    public Message(String user, String text) {
        this.user = user;
        this.text = text;
    }

    public Message(String user, String text, int messageColor) {
        this.user = user;
        this.text = text;
        this.messageColor = messageColor;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMessageColor() {
        return messageColor;
    }

    public void setMessageColor(int messageColor) {
        this.messageColor = messageColor;
    }
}
