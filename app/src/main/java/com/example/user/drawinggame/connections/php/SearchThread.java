package com.example.user.drawinggame.connections.php;

import android.util.Log;

import com.example.user.drawinggame.database_classes.Player;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchThread extends ConnectThread {

    public boolean isDone = false;

    private Player searched_player;

    public SearchThread(Player player) {
        this.searched_player = player;
    }

    @Override
    protected void init_file() {
        setPhpFileName("search");
    }


    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("userID", searched_player.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("search params", postDataParams.toString());

        setSendObject(postDataParams);
    }

    @Override
    protected void afterReceived() {
        try {
            JSONObject object = new JSONObject(getStr_received());
            searched_player.setUserName(object.getString("userName"));
            searched_player.setIntro(object.getString("introduction"));
            searched_player.setAge(object.getInt("age"));
            searched_player.setGender(object.getInt("sex"));
            searched_player.setLevel(object.getInt("lv"));
            searched_player.setPicURL("http://140.127.74.133/drawgame/picture/" + object.getString("userPhoto"));

            Log.e("searched player name", searched_player.getUserName());
            isDone = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
