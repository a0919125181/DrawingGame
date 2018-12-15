package com.example.user.drawinggame.Lobby;

import java.io.Serializable;

public class Profile implements Serializable{
    String id;
    byte[] photo;

    public Profile(String id) {
        this.id = id;
    }

    public String getaccount() {
        return id;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public byte[] getPhoto() {
        return photo;
    }
}
