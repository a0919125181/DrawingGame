package com.example.user.drawinggame.database_classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "friends")
public class Friend {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int userID;

    @ColumnInfo(name = "name")
    private String userName;

    @ColumnInfo(name = "intro")
    private String intro;

    @ColumnInfo(name = "age")
    private int age;

    @ColumnInfo(name = "gender")
    private int gender;

    @ColumnInfo(name = "lv")
    private int level;

    @ColumnInfo(name = "pic")
    private String picURL;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public Friend() {

    }

    public Friend(Player player) {
        this.userID = player.getUserID();
        this.userName = player.getUserName();
        this.intro = player.getIntro();
        this.age = player.getAge();
        this.gender = player.getGender();
        this.level = player.getLevel();
        this.picURL = player.getPicURL();
    }
}
