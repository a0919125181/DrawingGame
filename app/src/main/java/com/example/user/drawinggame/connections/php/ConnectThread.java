package com.example.user.drawinggame.connections.php;

import android.util.Log;

import com.example.user.drawinggame.database_classes.Player;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public abstract class ConnectThread extends Thread {


    private String phpFileName;

    private Player player;

    private JSONObject sendObject;

    private String str_received;

    public ConnectThread(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            init_file();
            URL url = new URL("http://140.127.74.133/drawgame/php/" + phpFileName + ".php");

            init_object();

            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            try {
                writer.write(Util.getPostDataString(sendObject));
            } catch (Exception e) {
                e.printStackTrace();
            }
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder("");
                // StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();

                Log.e(phpFileName + " get from php", sb.toString());
                str_received = sb.toString();
                // 接收處理
                afterReceived();

            } else {
                Log.e("false : ", String.valueOf(responseCode));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected abstract void init_file();

    protected abstract void init_object();

    protected abstract void afterReceived();

    public String getPhpFileName() {
        return phpFileName;
    }

    public void setPhpFileName(String phpFileName) {
        this.phpFileName = phpFileName;
    }

    public JSONObject getSendObject() {
        return sendObject;
    }

    public void setSendObject(JSONObject sendObject) {
        this.sendObject = sendObject;
    }

    public String getStr_received() {
        return str_received;
    }

    public void setStr_received(String str_received) {
        this.str_received = str_received;
    }
}
