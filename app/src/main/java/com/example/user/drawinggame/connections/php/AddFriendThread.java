package com.example.user.drawinggame.connections.php;

import android.util.Log;

import com.example.user.drawinggame.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFriendThread extends ConnectThread {

    private int receiverID;
    private int senderID;

    private boolean isSuccess = false;

    public boolean isSuccess() {
        return isSuccess;
    }

    public AddFriendThread(int myID, int inviterID) {
        receiverID = myID;
        senderID = inviterID;
    }

    @Override
    protected void init_file() {
        setPhpFileName("addFriend");
    }

    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("receiverID", receiverID);
            postDataParams.put("senderID", senderID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setSendObject(postDataParams);

        Log.i("add friend", "sent");
    }

    @Override
    protected void afterReceived() {
        // 接收處理
        String sFromPHP = getStr_received();
        if (sFromPHP.equals("success")) {
            Log.e("add friend", "success");
            isSuccess = true;
        } else if (sFromPHP.equals("fail")) {
            Log.e("add friend", "fail");
            isSuccess = false;
        }
    }
}
