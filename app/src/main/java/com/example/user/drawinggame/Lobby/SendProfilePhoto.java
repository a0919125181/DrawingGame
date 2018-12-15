package com.example.user.drawinggame.Lobby;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendProfilePhoto extends Thread {
    private Profile profile;
    private String path = "data/user/0/com.example.user.drawinggame/app_imageDir/profilePhoto";

    SendProfilePhoto(Profile profile){
        this.profile = profile;
    }

    @Override
    public void run(){
        try {
            String address = "140.127.74.133";
            int port = 3301;
            Socket s = new Socket(address, port);

/*
            File f = new File(path);
            FileInputStream in = new FileInputStream(f);
            byte[] imageInByte = new byte[in.available()];
            in.read(imageInByte);

            String id = profile.getaccount();
            String photoSize = String.format("%010d", imageInByte.length);

            OutputStream out = s.getOutputStream();
            out.write(id.getBytes());
            out.write(photoSize.getBytes());
            out.write(imageInByte);
            out.flush();
            out.close();
*/

            String id = profile.getaccount();
            String photoSize = String.format("%010d", profile.getPhoto().length);

            byte[] sendArray = new byte[13 + profile.getPhoto().length];
            System.arraycopy(id.getBytes(), 0, sendArray, 0, 3);
            System.arraycopy(photoSize.getBytes(), 0, sendArray, 3, 10);
            System.arraycopy(profile.getPhoto(), 0, sendArray, 13, profile.getPhoto().length);

            OutputStream out = s.getOutputStream();
            out.write(sendArray);
            out.flush();
            out.close();

        } catch (IOException e) {
            Log.e("Error", e.getMessage());
        }
    }
}
