package com.example.user.drawinggame.Room.Audio;

import android.media.AudioTrack;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class AudioTrackPlay extends Thread {
    AudioTrack track;
    private CopyOnWriteArrayList<byte[]> voiceQueue = new CopyOnWriteArrayList<>();

    public AudioTrackPlay(AudioTrack track) {
        this.track = track;
    }

    @Override
    public void run() {
        while (true) {
            playVoice();
        }
    }

    synchronized void playVoice() {
        if (voiceQueue.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //播放語音
        track.write(voiceQueue.get(0), 0, voiceQueue.get(0).length);

        voiceQueue.remove(0);
    }

    synchronized void putVoice(byte[] tempArray) {
        byte[] voiceByteArray = new byte[2500];
        for(int i=0; i<2500 ;i++){
            voiceByteArray[i] = tempArray[i];
        }

        voiceQueue.add(voiceByteArray);
        notify();
    }

}
