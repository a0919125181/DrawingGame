package com.example.user.drawinggame.Room.Audio;

import android.media.AudioTrack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class AudioTrackReceive extends Thread {
    private boolean isReceiving;
    private DatagramSocket socket;
    AudioTrack track;
    AudioTrackPlay atp;

    public AudioTrackReceive(DatagramSocket socket, AudioTrack track, AudioTrackPlay atp){
        this.socket = socket;
        this.track = track;
        this.atp = atp;
    }

    @Override
    public void run(){
        byte[] buffer = new byte[2503]; //message buffer
        isReceiving = true;

        while(isReceiving) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); //receive

                atp.putVoice(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
