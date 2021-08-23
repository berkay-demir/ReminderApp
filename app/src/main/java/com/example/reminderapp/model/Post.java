package com.example.reminderapp.model;

public class Post {

    public String reminder;
    public String reminderDate;
    public String reminderTime;
    public String name;

    public Post(String reminder, String reminderDate, String reminderTime,String name) {
        this.reminder = reminder;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
        this.name = name + " Oluşturdu";
    }
}
