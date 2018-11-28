package com.example.user.drawinggame.Room.Audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AudioRecordRecord extends Thread {
    private Audio audio;

    private AudioRecord audioRecord;
    private DatagramSocket sendSocket;

    private byte[] buffer;
    private boolean isSocketOn; //語音通道state
    private boolean  isRecording; // 錄製state

    public AudioRecordRecord(Audio audio){
        this.audio = audio;

        audioRecord = audio.getAudioRecord();
        sendSocket = audio.getDatagramSocket();

        buffer = new byte[2500];
        isSocketOn = true;
        isRecording = true;
    }

    @Override
    public void run(){
        audioRecord.startRecording();

        while (isSocketOn) {
            while(isRecording) {
                audioRecord.read(buffer, 0, buffer.length); // 錄製內容放到buffer
                new AudioSend(audio, buffer); // 傳送
            }
        }

        if(!isSocketOn) {
            sendSocket.close(); // close UDP socket
            //錄製迴圈結束 關閉錄製
            if (audioRecord != null) {
                audioRecord.stop();
            }
        }
    }

    synchronized public void stopRecording(){
        try {
            isRecording = false;
            Log.e("Hello", isRecording+"");
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public void restartRecording(){
        isRecording = true;
        Log.e("Hello", isRecording+"");
        notify();
    }
}
