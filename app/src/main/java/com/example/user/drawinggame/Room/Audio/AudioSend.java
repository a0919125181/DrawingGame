package com.example.user.drawinggame.Room.Audio;

import java.net.DatagramPacket;

public class AudioSend {
    AudioSend(Audio audio, byte[] buffer){
        try {
            byte[] sendByteArray = new byte[2503];
            byte[] idByteArray = audio.getId().getBytes();

            for(int i=0; i<2500 ;i++){
                sendByteArray[i] = buffer[i];
            }
            sendByteArray[2500] = idByteArray[0];
            sendByteArray[2501] = idByteArray[1];
            sendByteArray[2502] = idByteArray[2];

            DatagramPacket sendPacket = new DatagramPacket(sendByteArray, sendByteArray.length, audio.getInetAddress(), audio.getPort()); // set into DatagramPacket and point to the server
            audio.getDatagramSocket().send(sendPacket); // send
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
