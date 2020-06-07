package com.example.chatsystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Chat implements Comparable<Chat>{
    private String sender;
    private String receiver;
    private String message;
     private Date dateTime;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());


    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Chat(String sender, String receiver, String message,String date ) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        try {
            this.dateTime = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Chat() {

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

    @Override
    public int compareTo(Chat chat) {
        return getDateTime().compareTo(chat.getDateTime());
    }
}
