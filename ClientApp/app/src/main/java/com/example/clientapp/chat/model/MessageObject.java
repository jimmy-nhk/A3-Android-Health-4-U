package com.example.clientapp.chat.model;

import java.util.HashMap;
import java.util.Map;

public class MessageObject {

    private int id;
    private String sender;
    private String receiver;
    private String message;

    public MessageObject() {
    }

    public MessageObject(int id, String sender, String receiver, String message) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public MessageObject(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("sender", sender);
        result.put("receiver", receiver);
        result.put("message", message);

        return result;
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
