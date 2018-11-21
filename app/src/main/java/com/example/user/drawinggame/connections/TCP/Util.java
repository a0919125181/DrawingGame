package com.example.user.drawinggame.connections.TCP;

import com.example.user.drawinggame.Room.RoomFragment;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class Util {

    static void setPortSocket(RoomFragment roomFragment, int RoomPort_TCP, Socket roomSocket) {
        roomFragment.RoomPort_TCP = RoomPort_TCP;
        roomFragment.roomSocket = roomSocket;
    }

    public static void setVoiceCallPort_UDP(RoomFragment roomFragment, String port){
        roomFragment.VoiceCallPort_UDP = Integer.parseInt(port);
    }
}
