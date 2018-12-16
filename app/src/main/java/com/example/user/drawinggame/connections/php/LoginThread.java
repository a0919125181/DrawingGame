package com.example.user.drawinggame.connections.php;

import android.util.Log;

import com.example.user.drawinggame.Lobby.LobbyFragment;
import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginThread extends ConnectThread {
    Player player;

    private boolean isDone = false;

    public boolean isDone() {
        return isDone;
    }

    public LoginThread(Player player) {
        this.player = player;
    }

    @Override
    protected void init_file() {
        setPhpFileName("login");
    }


    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("account", player.getAccount());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("login params", postDataParams.toString());

        setSendObject(postDataParams);
    }

    @Override
    protected void afterReceived() {
        // 接收處理
        try {
            JSONObject object = new JSONObject(getStr_received());
            player.setUserID(object.getInt("userID"));
            player.setUserName(object.getString("userName"));
            player.setIntro(object.getString("introduction"));
            player.setAge(object.getInt("age"));
            player.setGender(object.getInt("sex"));
            player.setLevel(object.getInt("lv"));
            player.setExp(object.getInt("ex"));
            player.setMoney(object.getInt("money"));
            player.setDay(object.getInt("day"));
            MainActivity.appDatabase.playerDao().addPlayer(player);
            MainActivity.appDatabase.playerDao().updatePlayer(player);

            isDone = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
