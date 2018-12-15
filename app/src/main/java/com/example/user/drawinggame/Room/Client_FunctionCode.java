package com.example.user.drawinggame.Room;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Client_FunctionCode {

    private String functionCode;
    private Socket roomSocket;
    private String userID;
    private String dataStr;

    private String userID_temp;
    private String width;
    private String height;
    private String x;
    private String y;
    private int penSize;
    private int penColor;
    private String state;

    private String say;


    public Client_FunctionCode(String functionCode, Socket roomSocket) {
        this.functionCode = functionCode;
        this.roomSocket = roomSocket;

        sendFunction();
    }

    public Client_FunctionCode(String functionCode, Socket roomSocket, String userID, String dataStr) {
        this.functionCode = functionCode;
        this.roomSocket = roomSocket;
        this.userID = userID;
        this.dataStr = dataStr;

        sendFunction();
    }

    public Client_FunctionCode(String functionCode, Socket roomSocket, String say) {
        this.functionCode = functionCode;
        this.roomSocket = roomSocket;
        this.say = say;
        sendFunction();
    }

    @SuppressLint("DefaultLocale")
    private synchronized void sendFunction() {
        switch (functionCode) {
            case "020": // 取消準備
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OutputStream out = roomSocket.getOutputStream();
                            out.write("020".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case "021": // 準備
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OutputStream out = roomSocket.getOutputStream();
                            out.write("021".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case "05":
                String[] tempData = dataStr.split(" ");
                userID_temp = userID;

                width = String.format("%04d", Integer.parseInt(tempData[0]));
                height = String.format("%04d", Integer.parseInt(tempData[1]));
                x = String.format("%04d", Integer.parseInt(tempData[2]));
                y = String.format("%04d", Integer.parseInt(tempData[3]));
                penSize = Integer.parseInt(tempData[4]);
                if (Integer.parseInt(tempData[5]) == Color.BLACK) {
                    penColor = 1;
                } else {
                    penColor = 0;
                }
                state = tempData[6];

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OutputStream out = roomSocket.getOutputStream();
                            out.write(("05" + userID_temp + width + height + x + y + penSize + penColor + state).getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case "09":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OutputStream out = roomSocket.getOutputStream();
                            out.write("09".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case "10":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // send message in chatting room
                        try {
                            OutputStream out = roomSocket.getOutputStream();
                            out.write("10".getBytes());

                            byte[] len_array = new byte[3];
                            int len = say.getBytes().length;
                            String lenStr = String.format("%03d", len);
                            out.write(lenStr.getBytes());
                            Log.e("len", lenStr);
                            out.write(say.getBytes(Charset.forName("UTF-8")));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case "13":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // send clear view
                        try {
                            OutputStream out = roomSocket.getOutputStream();
                            out.write("13".getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case "15":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // send answer
                        try {
                            OutputStream out = roomSocket.getOutputStream();
                            out.write("15".getBytes());

                            int len = say.getBytes().length;
                            String lenStr = String.format("%02d", len);
                            out.write(lenStr.getBytes());
                            Log.e("len", lenStr);

                            out.write(say.getBytes(Charset.forName("UTF-8")));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

        }
    }
}
