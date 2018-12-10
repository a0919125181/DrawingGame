package com.example.user.drawinggame.Room.Audio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

class AudioSend {
    AudioSend(Audio audio, byte[] buffer){
        byte[] sendByteArray = new byte[2503];
        byte[] idByteArray = audio.getId().getBytes();

        System.arraycopy(buffer, 0, sendByteArray, 0, 2500);
        System.arraycopy(idByteArray, 0, sendByteArray, 2500, 3);

        try {
            DatagramPacket sendPacket = new DatagramPacket(sendByteArray, sendByteArray.length, audio.getInetAddress(), audio.getPort()); // set into DatagramPacket and point to the server
            DatagramSocket socket = audio.getDatagramSocket();
            socket.send(sendPacket); // send
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
