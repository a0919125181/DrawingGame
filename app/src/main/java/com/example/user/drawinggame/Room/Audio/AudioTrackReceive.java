package com.example.user.drawinggame.Room.Audio;

import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class AudioTrackReceive extends Thread {
    private DatagramSocket socket;
    private AudioTrackPlay atp;

    private int senderID;
    boolean isReceiving;

    public int getSenderID() {
        return senderID;
    }

    public AudioTrackReceive(Audio audio, AudioTrackPlay atp) {
        this.socket = audio.getDatagramSocket();
        this.atp = atp;
        isReceiving = true;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[2503]; //message buffer

        while (isReceiving) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); //receive
                atp.putVoice(buffer);

                byte[] id = new byte[3];
                System.arraycopy(packet.getData(),2500, id, 0, 3 );

                senderID = Integer.parseInt(new String(id));
//                Log.e("id", String.valueOf(senderID));
            } catch (SocketException e) {
                isReceiving = false;
            } catch (IOException e) {
                Log.e("Error", e.getMessage());
            }

        }
    }

    public void closeReceiving(){
        isReceiving = false;
    }
}
