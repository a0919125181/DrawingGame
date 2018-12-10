package com.example.user.drawinggame.Room.Audio;

import android.media.AudioTrack;
import com.example.user.drawinggame.Room.RoomFragment;
import java.util.concurrent.CopyOnWriteArrayList;

public class AudioTrackPlay extends Thread {
    private AudioTrack track;
    private RoomFragment roomFragment;
    private CopyOnWriteArrayList<byte[]> voiceQueue = new CopyOnWriteArrayList<>();
    private boolean isPlaying;

    public AudioTrackPlay(AudioTrack track, RoomFragment roomFragment) {
        this.track = track;
        this.roomFragment = roomFragment;

        isPlaying = true;
    }

    private int senderID;

    public int getSenderID() {
        return senderID;
    }

    @Override
    public void run() {
        while (isPlaying) {
            playVoice();
        }
    }

    private synchronized void playVoice() {
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
        System.arraycopy(tempArray, 0, voiceByteArray, 0, 2500);
        voiceQueue.add(voiceByteArray);

        senderID = roomFragment.getAtr().getSenderID();
        notify();
    }

}
