package com.example.user.drawinggame.connections.php;

import android.util.Log;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.database_classes.Player;

import org.json.JSONException;
import org.json.JSONObject;

public class EditThread extends ConnectThread {
    private Player player;

    public EditThread(Player player) {
        this.player = player;
    }

    @Override
    protected void init_file() {
        setPhpFileName("edit");
    }

    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("account", player.getAccount());
            postDataParams.put("userName", player.getUserName());
            postDataParams.put("introduction", player.getIntro());
            postDataParams.put("age", player.getAge());
            postDataParams.put("sex", player.getGender());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("edit params", postDataParams.toString());

        setSendObject(postDataParams);
    }

    @Override
    protected void afterReceived() {
        // 接收處理
        String sFromPHP = getStr_received();
        if (sFromPHP.equals("success")) {
            Log.e("edit player", "successfully");
            MainActivity.appDatabase.playerDao().updatePlayer(player);
            Log.e("edited", player.getIntro());
        } else if (sFromPHP.equals("fail")) {
            Log.e("edit player", "failed");
        }
    }
}
