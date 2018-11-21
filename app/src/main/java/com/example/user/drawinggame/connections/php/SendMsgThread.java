package com.example.user.drawinggame.connections.php;

import android.util.Log;

import com.example.user.drawinggame.database_classes.Player;

import org.json.JSONException;
import org.json.JSONObject;

public class SendMsgThread extends ConnectThread {
    private Player player;
    private String msg;
    private int receiverID;
    private int msgType;      // 0傳送訊息 1加好友

    public SendMsgThread(Player player, String msg, int receiverID, int type) {
        super(player);
        this.player = player;
        this.msg = msg;
        this.receiverID = receiverID;
        this.msgType = type;
    }

    @Override
    protected void init_file() {
        setPhpFileName("message");
    }

    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("text", msg);
            postDataParams.put("receiverID", receiverID);
            postDataParams.put("senderID", player.getUserID());
            postDataParams.put("style", msgType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("msg params", postDataParams.toString());

        setSendObject(postDataParams);
    }

    @Override
    protected void afterReceived() {
        // 接收處理
    }

}
