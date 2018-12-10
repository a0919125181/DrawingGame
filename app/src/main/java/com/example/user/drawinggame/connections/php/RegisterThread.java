package com.example.user.drawinggame.connections.php;

import android.os.Build;
import android.util.Log;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.database_classes.Player;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterThread extends ConnectThread {
    private Player player;

    public RegisterThread(Player player) {
        this.player = player;
    }

    @Override
    protected void init_file() {
        setPhpFileName("register");
    }

    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("name", player.getUserName());
            postDataParams.put("account", player.getAccount());
            postDataParams.put("gender", player.getGender());
            postDataParams.put("age", player.getAge());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("register params", postDataParams.toString());

        setSendObject(postDataParams);
    }

    @Override
    protected void afterReceived() {
        String msg = getStr_received();
        // 接收處理
        if (msg.equals("success")) {
            Log.e("add player", "successfully");
            new LoginThread(player).start();
        } else if (msg.equals("fail")) {
            Log.e("check db", "account already exists");
            player = MainActivity.appDatabase.playerDao().getPlayerBySerialID(Build.SERIAL);
            // do login send account(serialID)
            new LoginThread(player).start();
        }
    }

}
