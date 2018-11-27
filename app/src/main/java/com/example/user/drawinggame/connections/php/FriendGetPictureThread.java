package com.example.user.drawinggame.connections.php;

import com.example.user.drawinggame.Lobby.Friend.FriendFragment;
import com.example.user.drawinggame.database_classes.Player;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendGetPictureThread extends ConnectThread {

    private Player player;
    private FriendFragment friendFragment;


    public FriendGetPictureThread(Player player,  FriendFragment friendFragment) {
        super(player);
        this.player = player;
        this.friendFragment = friendFragment;
    }


    @Override
    protected void init_file() {
        setPhpFileName("getPicture");
    }

    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("userID", player.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setSendObject(postDataParams);
    }

    @Override
    protected synchronized void afterReceived() {
        // 接收處理
        String picURL = "http://140.127.74.133" + getStr_received();
        friendFragment.setPicURL(picURL);
    }
}
