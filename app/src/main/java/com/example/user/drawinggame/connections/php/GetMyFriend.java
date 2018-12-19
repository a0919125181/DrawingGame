package com.example.user.drawinggame.connections.php;

import android.content.Context;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.database_classes.Friend;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;

import org.json.JSONException;
import org.json.JSONObject;

public class GetMyFriend extends ConnectThread {

    private Context context;

    private int id;

    public GetMyFriend(Context context, int id) {
        this.context = context;
        this.id = id;
    }

    @Override
    protected void init_file() {
        setPhpFileName("searchFriend");
    }

    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("userID", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setSendObject(postDataParams);
    }

    @Override
    protected void afterReceived() {
        // 接收處理
        String msg = getStr_received();

        String[] friend_arr = msg.split("%##&#!");

        if (!friend_arr[0].equals("")) {
            for (String s : friend_arr) {
                try {
                    JSONObject msg_object = new JSONObject(s);
                    int friendID = msg_object.getInt("friendID");

                    // 更新資料
                    Player player = new Player(friendID);
                    SearchThread st = new SearchThread(player);
                    st.start();
                    while (!st.isDone) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Friend friend = new Friend(player);
                    MainActivity.appDatabase.friendDao().addFriend(friend);
                    MainActivity.appDatabase.friendDao().updateFriend(friend);

                    // 更新照片
                    new UI.SaveFriendImageTask(context, "friends_photo", String.valueOf(friend.getUserID())).execute(friend.getPicURL());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
