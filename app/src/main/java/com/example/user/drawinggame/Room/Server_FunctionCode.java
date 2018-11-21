package com.example.user.drawinggame.Room;

import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.user.drawinggame.R;
import com.example.user.drawinggame.Room.Drawing.DrawFragment;
import com.example.user.drawinggame.Room.Drawing.GuessFragment;
import com.example.user.drawinggame.Room.Drawing.GuessView;
import com.example.user.drawinggame.connections.php.ConnectThread;
import com.example.user.drawinggame.connections.php.SearchThread;
import com.example.user.drawinggame.connections.TCP.Util;
import com.example.user.drawinggame.database_classes.Player;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class Server_FunctionCode {

    private String functionCode;
    private InputStream receiveFromServer;
    private RoomFragment fragment;


    private byte[] ID_array;
    private String ID;

    public Server_FunctionCode(String functionCode, InputStream receiveFromServer, RoomFragment fragment) {
        this.functionCode = functionCode;
        this.receiveFromServer = receiveFromServer;
        this.fragment = fragment;

        getFunction();
    }

    private void audioUDP() {
        byte[] UDP_port_array = new byte[4];
        try {
            receiveFromServer.read(UDP_port_array, 0, 4); // 接收UDP port
        } catch (IOException e) {
            e.printStackTrace();
        }
        String UDP_port = new String(UDP_port_array);
        Util.setVoiceCallPort_UDP(fragment, UDP_port); // 回傳UDP port
        Log.i("UDP_port_array", UDP_port);
    }

    private void setReady() {
        ID_array = new byte[3];
        byte[] readyCancel_array = new byte[1];
        try {
            receiveFromServer.read(ID_array, 0, 3);             // 接收ID
            receiveFromServer.read(readyCancel_array, 0, 1);    // 接收準備 || 取消
        } catch (IOException e) {
            e.printStackTrace();
        }

        ID = new String(ID_array);
        String readyCancel = new String(readyCancel_array);
        if (readyCancel.equals("0"))
            Log.i(ID, "取消準備");
        else
            Log.i(ID, "準備");
    }

    private void playerSequence() {
        try {
            ID_array = new byte[15];
            receiveFromServer.read(ID_array, 0, 15); // 接收ID
            ID = new String(ID_array);
            Log.i("順序", ID);

            Message msg = new Message();
            msg.what = 3;
            fragment.handler_room.sendMessage(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playerTurn() {
        ID_array = new byte[3];
        try {
            receiveFromServer.read(ID_array, 0, 3); // 接收ID
        } catch (IOException e) {
            e.printStackTrace();
        }
        ID = new String(ID_array);
        Log.e("換", ID);


        if (ID.equals(String.valueOf(fragment.player.getUserID()))) {
            Message msg = new Message();
            msg.what = 1;
            fragment.handler_room.sendMessage(msg);
        } else {
            Message msg = new Message();
            msg.what = 2;
            fragment.handler_room.sendMessage(msg);
        }

    }

    private void painting() {
        ID_array = new byte[3];
        byte[] screenWidth_array = new byte[4];
        byte[] screenHeight_array = new byte[4];
        byte[] drawPointX_array = new byte[4];
        byte[] drawPointY_array = new byte[4];
        byte[] thickThin_array = new byte[2];
        byte[] color_array = new byte[1];

        try {
            receiveFromServer.read(ID_array, 0, 3);             // 接收ID
            receiveFromServer.read(screenWidth_array, 0, 4);    // 接收對方View寬
            receiveFromServer.read(screenHeight_array, 0, 4);   // 接收對方View高
            receiveFromServer.read(drawPointX_array, 0, 4);     // 接收X座標
            receiveFromServer.read(drawPointY_array, 0, 4);     // 接收Y座標
            receiveFromServer.read(thickThin_array, 0, 2);      // 接收粗細
            receiveFromServer.read(color_array, 0, 1);         // 接收畫筆顏色
        } catch (IOException e) {
            e.printStackTrace();
        }

        ID = new String(ID_array);
        String screenWidth = new String(screenWidth_array);
        String screenHeight = new String(screenHeight_array);
        String drawPointX = new String(drawPointX_array);
        String drawPointY = new String(drawPointY_array);
        String thickThin = new String(thickThin_array);
        String color = new String(color_array);

        Log.i("對方View寬高", screenWidth + " , " + screenHeight);
        Log.i("接收座標", drawPointX + " , " + drawPointY + " 粗細 " + thickThin + " 畫筆顏色 " + color);

        //
        GuessView gv = fragment.guessFragment.getGuessView();

        gv.convertSize(screenWidth, screenHeight);
        gv.setPathPen(drawPointX, drawPointY, thickThin, color);
    }


    private void playerEnter() {
        ID_array = new byte[3];
        try {
            receiveFromServer.read(ID_array, 0, 3); //接收ID
        } catch (IOException e) {
            e.printStackTrace();
        }
        ID = new String(ID_array);
        Log.i(ID, "加入房間");

        Player player_enter = new Player();
        player_enter.setUserID(Integer.parseInt(ID));
        new SearchThread(player_enter).start();

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.e("player enter", "name: " + player_enter.getUserName());
        final PlayerFragment pf = new PlayerFragment(player_enter);
        fragment.playerFragmentList.add(pf);

        // UI
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < fragment.playerFragmentList.size(); i++) {
                    ConstraintLayout layout = fragment.playerLayoutList.get(i);
                    if (layout.getVisibility() == View.INVISIBLE) {
                        layout.setVisibility(View.VISIBLE);
                        fragment.fragmentManagerRoom.beginTransaction().replace(layout.getId(), pf).commit();
                        break;
                    }
                }
            }
        });
    }

    private void getQuestion() {
        byte[] question_array = new byte[6];
        try {
            receiveFromServer.read(question_array, 0, 6); // 接收題目
        } catch (IOException e) {
            e.printStackTrace();
        }
        String question = new String(question_array);
        Log.i("收到題目", question);
    }

    private void playerLeave() {
        ID_array = new byte[3];
        try {
            receiveFromServer.read(ID_array, 0, 3); // 接收ID
        } catch (IOException e) {
            e.printStackTrace();
        }
        ID = new String(ID_array);
        Log.e(ID, "斷線");

        final PlayerFragment pf = new PlayerFragment(new Player(ID));
        fragment.playerFragmentList.remove(pf);


        // UI
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < fragment.playerFragmentList.size(); i++) {
                    ConstraintLayout layout = fragment.playerLayoutList.get(i);
                    if (layout.getVisibility() == View.VISIBLE) {
                        layout.setVisibility(View.INVISIBLE);
                        fragment.fragmentManagerRoom.beginTransaction().remove(pf).commit();
                        break;
                    }
                }
            }
        });

    }

    private void chatting() {
        byte[] msg_array = new byte[3];
        try {
            receiveFromServer.read(msg_array, 0, 3); // 訊息bytes 大小

            int len = Integer.parseInt(new String(msg_array));

            byte[] say_array = new byte[len];

            receiveFromServer.read(say_array, 0, len);

            final String say = new String(say_array);

            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = new TextView(fragment.getContext());
                    tv.setText(tv.getText() + say);
                    fragment.linearLayoutChat.addView(tv);

                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getFunction() {
        switch (functionCode) {
            case "01":
                audioUDP();
                break;
            case "02":
                setReady();
                break;
            case "03":
                playerSequence();
                break;
            case "04":
                playerTurn();
                break;
            case "05":
                painting();
                break;
            case "06":
                playerEnter();
                break;
            case "07":
                getQuestion();
                break;
            case "09":
                playerLeave();
                break;
            case "10":
                chatting();
                break;
        }
    }
}
