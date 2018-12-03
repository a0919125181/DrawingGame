package com.example.user.drawinggame.Room.Audio;

import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class AudioTrackReceive extends Thread {
    private boolean isReceiving;
    private DatagramSocket socket;
    AudioTrack track;
    AudioTrackPlay atp;

    private int senderID;

    public int getSenderID() {
        return senderID;
    }

    public AudioTrackReceive(DatagramSocket socket, AudioTrack track, AudioTrackPlay atp) {
        this.socket = socket;
        this.track = track;
        this.atp = atp;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[2503]; //message buffer
        isReceiving = true;


        while (isReceiving) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); //receive

                atp.putVoice(buffer);

                byte[] id = new byte[3];
                id[0] = packet.getData()[2500];
                id[1] = packet.getData()[2501];
                id[2] = packet.getData()[2502];
                senderID = Integer.parseInt(new String(id));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
