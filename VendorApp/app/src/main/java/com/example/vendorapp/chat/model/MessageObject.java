package com.example.vendorapp.chat.model;

import java.util.HashMap;
import java.util.Map;

public class MessageObject {

    private int id;
    private String sender;
    private String receiver;
    private String message;
    private boolean isSeen;
    private boolean isDelivered;

    public MessageObject() {
    }

    public MessageObject(int id, String sender, String receiver, String message, boolean isSeen, boolean isDelivered) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
        this.isDelivered = isDelivered;
    }

    public MessageObject(int id, String sender, String receiver, String message, boolean isSeen) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
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

    public boolean getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean seen) {
        isSeen = seen;
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
        result.put("isSeen", isSeen);
        result.put("isDelivered" , isDelivered);
        return result;
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", message='" + message + '\'' +
                ", isSeen=" + isSeen +
                ", isDelivered=" + isDelivered +
                '}';
    }
}