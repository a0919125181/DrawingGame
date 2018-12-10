package com.example.user.drawinggame.Room.Audio;

import android.media.AudioRecord;
import com.example.user.drawinggame.Room.RoomFragment;
import java.net.DatagramSocket;

public class AudioRecordRecord extends Thread {
    private RoomFragment rf;

    private Audio audio;
    private AudioRecord audioRecord;

    private byte[] buffer;
    private boolean isSocketOn; //語音通道state
    private boolean  isRecording; // 錄製state

    public AudioRecordRecord(Audio audio, RoomFragment rf){
        this.audio = audio;
        this.rf = rf;

        this.audioRecord = audio.getAudioRecord();

        buffer = new byte[2500];
        isSocketOn = true;
        isRecording = true;
    }

    @Override
    public void run(){
        audioRecord.startRecording();
        isSocketOn = rf.getUdpConnectionState();
        isSocketOn = true;
        while (isSocketOn) {
            while(isRecording) {
                audioRecord.read(buffer, 0, buffer.length); // 錄製內容放到buffer
                new AudioSend(audio, buffer); // 傳送
            }
        }

        if(!isSocketOn) {
            //錄製迴圈結束 關閉錄製
            if (audioRecord != null) {
                audioRecord.stop();
            }
        }
    }

    synchronized public void stopRecording(){
        try {
            isRecording = false;
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public void restartRecording(){
        isRecording = true;
        notify();
    }
}
