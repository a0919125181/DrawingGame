package com.example.user.drawinggame.connections.php;

import android.util.Log;

import com.example.user.drawinggame.database_classes.Player;

import org.json.JSONException;
import org.json.JSONObject;

public class GetMsgThread extends ConnectThread {

    private Player player;

    public GetMsgThread(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    protected void init_file() {
        setPhpFileName("getMessage");
    }

    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("receiverID", player.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setSendObject(postDataParams);
    }

    @Override
    protected void afterReceived() {
        // 接收處理
        String msg = getStr_received();
        Log.e("get msg", msg);
    }
}
