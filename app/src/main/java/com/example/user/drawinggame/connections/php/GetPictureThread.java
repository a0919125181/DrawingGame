package com.example.user.drawinggame.connections.php;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;

import org.json.JSONException;
import org.json.JSONObject;

public class GetPictureThread extends ConnectThread {

    private Player player;
    private Fragment fragment;
    private ImageView imageView;


    public GetPictureThread(Player player, Fragment fragment, ImageView imageView) {
        super(player);
        this.player = player;
        this.fragment = fragment;
        this.imageView = imageView;
    }


    @Override
    protected void init_file() {
        setPhpFileName("getPicture");
    }

    @Override
    protected void init_object() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("userID", player.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setSendObject(postDataParams);
    }

    @Override
    protected void afterReceived() {
        // 接收處理
        String picURL = "http://140.127.74.133" + getStr_received();
        imageView.setImageDrawable(UI.LoadImageFromWebOperations(picURL));

//        fragment.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                String picURL = "http://140.127.74.133" + getStr_received();
//                imageView.setImageDrawable(UI.LoadImageFromWebOperations(picURL));
//                Log.e("ui thread", "change photo");
//            }
//        });

    }
}
