package com.example.user.drawinggame.connections.TCP;


import android.util.Log;

import com.example.user.drawinggame.Room.RoomFragment;
import com.example.user.drawinggame.utils.UI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class EnterRoomThread extends Thread {
    private int id;
    private RoomFragment fragment;

    private Socket EnterRequestSocket;
    private int roomPort_TCP;


    public EnterRoomThread(int id, RoomFragment fragment) {
        this.id = id;
        this.fragment = fragment;
    }

    @Override
    public void run() {
        requestConnection();
        roomConnect();
    }

    private void requestConnection() {
        byte[] portNumBuff = new byte[0];
        //第一次 要求連線
        try {
            EnterRequestSocket = new Socket("140.127.74.133", 3302);
            portNumBuff = new byte[4]; // buffer for room port
            InputStream receiveMsg = EnterRequestSocket.getInputStream();
            receiveMsg.read(portNumBuff, 0, 4); // 接收房間port
            roomPort_TCP = Integer.parseInt(new String(portNumBuff)); // 房間TCP的port
            EnterRequestSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Log.e("get", Arrays.toString(portNumBuff));
            e.printStackTrace();
        }
    }

    private void roomConnect() {
        try {
            // 第二次 房間連線
            Socket roomSocket = new Socket("140.127.74.133", roomPort_TCP);

            OutputStream out = roomSocket.getOutputStream(); // 傳送進房成功代碼
            out.write(("01" + id).getBytes());
            Log.e("enter room", "success");

            // 丟回RoomFragment
            Util.setPortSocket(fragment, roomPort_TCP, roomSocket);
            UI.fragmentSwitcher(fragment, false);

        } catch (IOException e) {
            Log.e("enter room", "fail");
            e.printStackTrace();
        }
    }
}
