package com.lixinyu.cooperativecoding.model;

import javax.validation.constraints.NotNull;

public class Message {

    private int id;
    private String content;
    private String from;

    public Message() {
    }

    public Message(Message message){
        this.id = message.getId();
        this.content = message.getContent();
        this.from = message.getFrom();
    }

    public Message(int id, String content, String from) {
        this.id = id;
        this.content = content;
        this.from = from;
    }

    //Getters and Setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
