package com.example.user.drawinggame.Room.Drawing;

public class GuessPath {
    private String pastX;
    private String pastY;

    public void setPast(String pastX, String pastY){
        this.pastX = pastX;
        this.pastY = pastY;
    }

    public String getPastX(){
        return pastX;
    }

    public String getPastY(){
        return pastY;
    }
}