package com.example.user.drawinggame.connections.TCP;

import android.util.Log;

import com.example.user.drawinggame.Lobby.LobbyFragment;
import com.example.user.drawinggame.Room.RoomFragment;
import com.example.user.drawinggame.Room.Server_FunctionCode;
import com.example.user.drawinggame.utils.UI;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ReceiveFromServer_TCP extends Thread {
    private Socket s;
    private RoomFragment fragment;
    private String receivedCode;

    public Server_FunctionCode sfc;

    public boolean exit = true;

    public String getReceivedCode() {
        return receivedCode;
    }

    public ReceiveFromServer_TCP(Socket s, RoomFragment fragment) {
        this.s = s;
        this.fragment = fragment;
    }

    @Override
    public void run() {
        while (exit) {
            try {
                // 處理所有從server來的訊息
                InputStream receiveFromServer = s.getInputStream();
                byte[] functionCode = new byte[2];
                receiveFromServer.read(functionCode, 0, 2); // receive function code
                receivedCode = new String(functionCode);
                Log.i("接收功能代碼", receivedCode);
                sfc = new Server_FunctionCode(receivedCode, receiveFromServer, fragment); // 啟動對應function
            } catch (SocketException e) {
                UI.fragmentSwitcher(new LobbyFragment(), false);
                e.printStackTrace();
            } catch (IOException e) {
                UI.fragmentSwitcher(new LobbyFragment(), false);
                e.printStackTrace();
            }

        }
    }


}
