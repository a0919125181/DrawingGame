package com.example.user.drawinggame.database_classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "users")
public class Player {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int userID;

    @ColumnInfo(name = "name")
    private String userName;

    @ColumnInfo(name = "account")
    private String account;

    @ColumnInfo(name = "intro")
    private String intro;

    @ColumnInfo(name = "age")
    private int age;

    @ColumnInfo(name = "gender")
    private int gender;

    @ColumnInfo(name = "lv")
    private int level;

    @ColumnInfo(name = "exp")
    private int exp;

    @ColumnInfo(name = "money")
    private int money;

    @ColumnInfo(name = "day")
    private int day;

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public Player() {
    }

    @Ignore
    public Player(int userID) {
        this.userID = userID;
    }

    @Ignore
    public Player(String account) {
        this.account = account;
    }
}
