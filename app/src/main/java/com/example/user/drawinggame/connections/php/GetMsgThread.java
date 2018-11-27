package com.example.user.drawinggame.connections.php;

import android.util.Log;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.database_classes.Message;
import com.example.user.drawinggame.database_classes.Player;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


        String[] msg_arr = msg.split("##&#!");

        for (String s : msg_arr) {
            try {
                JSONObject msg_object = new JSONObject(s);
                Message message = new Message(
                        msg_object.getInt("messageID"),
                        msg_object.getInt("senderID"),
                        msg_object.getString("userName"),
                        player.getUserID(),
                        player.getUserName(),
                        msg_object.getString("text"),
                        new Date().toString(),
                        0,
                        msg_object.getInt("style"));
                MainActivity.appDatabase.messageDao().addMessage(message);
                MainActivity.appDatabase.messageDao().updateMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}
