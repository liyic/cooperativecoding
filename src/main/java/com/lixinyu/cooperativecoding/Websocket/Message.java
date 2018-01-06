package com.lixinyu.cooperativecoding.Websocket;

public class Message {

    private int id;
    private String content;

    public Message() {
    }

    public Message(int id, String content) {
        this.id = id;
        this.content = content;
    }

    //Getter and Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

}
