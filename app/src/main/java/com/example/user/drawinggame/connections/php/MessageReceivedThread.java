package com.example.user.drawinggame.connections.php;

import android.util.Log;

import com.example.user.drawinggame.database_classes.Message;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageReceivedThread extends ConnectThread {

    private Message message;

    public MessageReceivedThread(Message message) {
        this.message = message;
    }

    @Override
    protected void init_file() {
        setPhpFileName("messageReceived");
    }

    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("messageID", message.getMessageID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setSendObject(postDataParams);

        Log.i("message received", "sent");
    }

    @Override
    protected void afterReceived() {
        // 接收處理
    }
}
