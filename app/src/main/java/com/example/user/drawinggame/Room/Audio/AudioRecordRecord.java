package com.example.user.drawinggame.Room.Audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AudioRecordRecord extends Thread {
    private AudioRecord audioRecord;
    private byte[] buffer;
    private boolean isRecording; //錄製state
    private int port; // UDP port number
    private String id;
    private InetAddress server; // server's IP
    private DatagramSocket sendSocket; // send socket

    public AudioRecordRecord(int port, int id){
        this.port = port;
        this.id = String.valueOf(id);
        try {
            server = InetAddress.getByName("140.127.74.133"); // trans to IP
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

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
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequence, channelInConfig, audioEncoding, bufferSize);

        // 緩衝陣列
        buffer = new byte[2500];
    }

    @Override
    public void run(){
        audioRecord.startRecording();// 開始錄製
        isRecording = true;// 設定錄製標記為true

        while (isRecording) {
            // 錄製內容放到buffer  result代表儲存長度
            int result = audioRecord.read(buffer, 0, buffer.length);
            send(buffer); // 傳送
        }

        if(!isRecording) {
            sendSocket.close(); // close UDP socket
            //錄製迴圈結束 關閉錄製
            if (audioRecord != null) {
                audioRecord.stop();
            }
        }
    }

    private void send(byte[] buffer) {
        try {
            byte[] sendByteArray = new byte[2503];
            byte[] idByteArray = id.getBytes();

            for(int i=0; i<2500 ;i++){
                sendByteArray[i] = buffer[i];
            }
            sendByteArray[2500] = idByteArray[0];
            sendByteArray[2501] = idByteArray[1];
            sendByteArray[2502] = idByteArray[2];

            DatagramPacket sendPacket = new DatagramPacket(sendByteArray, sendByteArray.length, server, port); // set into DatagramPacket and point to the server
            sendSocket.send(sendPacket); // send
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DatagramSocket getSendSocket(){
        return sendSocket;
    }

    final void sendSocketClose(){
        isRecording = false;
    }
}
