package com.example.clientapp.model;

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MessageObject {

    private int id;
    private String sender;
    private String receiver;
    private String message;
    private boolean isSeen;
    private boolean isDelivered;
    private String timeStamp;


    public MessageObject() {
    }

    public MessageObject(int id, String sender, String receiver, String message, boolean isSeen, boolean isDelivered) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
        this.isDelivered = isDelivered;
        this.timeStamp = filterDate(LocalDateTime.now().toString());
    }

    // filter the string date
    public String filterDate (String rawString){

        Log.d("MessageObject" , "timestamp before changed: " + rawString);
        // initialize the new string
        char [] filterString = new char[rawString.length()];


        // iterate through each character in the string
        for (int i = 0 ; i < rawString.length(); i++){

            // check if the character is T then replace it with T
            if (rawString.charAt(i) == 'T'){
                filterString[i] = ' ';
                continue;
            }

            // check if the character is :
            if(rawString.charAt(i) == '.'){
                return String.valueOf(filterString).trim();
            }

            filterString[i] = rawString.charAt(i);
        }

        return null;
    }

    public MessageObject(int id, String sender, String receiver, String message, boolean isSeen) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
    }

    // is new message
    public boolean isNewestMessage(){

        String currentTime = filterDate(LocalDateTime.now().toString());
        int currentTimeInt = convertInt(currentTime);

        Log.d("MessageObject", "currentTime: " + currentTime);
        Log.d("MessageObject", "message object currentTime: " + this.timeStamp);

        // check if the date is current
        if (!this.timeStamp.substring(0, 11).equals(currentTime.substring(0, 11))){
            return false;
        }

        // check if the time is current
        if (convertInt(timeStamp) >= currentTimeInt - 5){
            return true;
        }
        return false;

    }

    // convert time to integer
    private int convertInt(String currentTime) {
        String withoutDate = currentTime.substring(11, currentTime.length());
        Log.d("MessageObject", "without date:" + withoutDate);
        int hour = Integer.parseInt(withoutDate.substring(0,2));
        int min = Integer.parseInt(withoutDate.substring(3,5));
        int second = Integer.parseInt(withoutDate.substring(6,8));

        Log.d("MessageObject", "time second: " + hour +" " + min + " " + second);

        return hour * 3600 + min * 60 + second;
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("sender", sender);
        result.put("receiver", receiver);
        result.put("message", message);
        result.put("isSeen", isSeen);
        result.put("isDelivered" , isDelivered);
        result.put("timeStamp" , timeStamp);
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
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
