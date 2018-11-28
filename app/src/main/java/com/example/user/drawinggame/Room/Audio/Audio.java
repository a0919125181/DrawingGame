package com.example.user.drawinggame.Room.Audio;

import android.media.AudioRecord;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Audio {
    private InetAddress server;
    private DatagramSocket sendSocket;
    private AudioRecord audioRecord;
    private String id;
    private int port;

    void setInetAddress(InetAddress server){
        this.server = server;
    }

    void setDatagramSocket(DatagramSocket sendSocket){
        this.sendSocket = sendSocket;
    }

    void setAudioRecord(AudioRecord audioRecord){
        this.audioRecord = audioRecord;
    }

    void setId(String id){
        this.id = id;
    }

    void setPort(int port){
        this.port = port;
    }

    InetAddress getInetAddress(){
        return server;
    }

    public DatagramSocket getDatagramSocket(){
        return sendSocket;
    }

    AudioRecord getAudioRecord(){
        return audioRecord;
    }

    String getId(){
        return id;
    }

    int getPort(){
        return port;
    }
}
