package com.example.user.drawinggame.Room.Audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AudioConnect {
    private Audio audio;

    public AudioConnect(int port, String id){
        InetAddress server = null; // server's IP
        try {
            server = InetAddress.getByName("140.127.74.133"); // trans to IP
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        DatagramSocket sendSocket = null;
        try {
            sendSocket = new DatagramSocket(); // establish UDP socket
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // 每秒樣本數
        int frequence = 44100;

        // 取樣通道
        // channelInConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int channelInConfig = AudioFormat.CHANNEL_IN_DEFAULT;

        // 定義音訊編碼（16位）
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

        // 獲取緩衝大小
        int bufferSize = AudioRecord.getMinBufferSize(frequence, channelInConfig, audioEncoding);

        // 創AudioRecord物件
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequence, channelInConfig, audioEncoding, bufferSize);

        audio = new Audio();
        audio.setAudioRecord(audioRecord);
        audio.setDatagramSocket(sendSocket);
        audio.setId(id);
        audio.setInetAddress(server);
        audio.setPort(port);
    }

    public Audio getAudio(){
        return audio;
    }
}
