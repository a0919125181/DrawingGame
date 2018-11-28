package com.example.user.drawinggame.Room;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.user.drawinggame.Lobby.LobbyFragment;
import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.Room.Audio.Audio;
import com.example.user.drawinggame.Room.Audio.AudioConnect;
import com.example.user.drawinggame.Room.Audio.AudioRecordRecord;
import com.example.user.drawinggame.Room.Audio.AudioTrackPlay;
import com.example.user.drawinggame.Room.Audio.AudioTrackReceive;
import com.example.user.drawinggame.Room.Audio.AudioTrackSet;
import com.example.user.drawinggame.Room.Drawing.DrawFragment;
import com.example.user.drawinggame.Room.Drawing.GuessFragment;
import com.example.user.drawinggame.Room.Drawing.GuessView;
import com.example.user.drawinggame.Room.Drawing.PaintAPP.FingerDrawFragment;
import com.example.user.drawinggame.connections.TCP.ReceiveFromServer_TCP;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RoomFragment extends Fragment implements View.OnClickListener {

    // connections
    public int RoomPort_TCP;
    public int VoiceCallPort_UDP;
    public Socket roomSocket;
    private ReceiveFromServer_TCP conn_server_tcp;

    FragmentManager fragmentManagerRoom;
    Player player;


    // chatting area commands
    LinearLayout linearLayoutChat;
    private ScrollView scrollViewChat;

    public ScrollView getScrollViewChat() {
        return scrollViewChat;
    }

    private EditText editTextChat;

    // game controllers
    private TextView textViewCountDown;
    private Button buttonStart;
    private Button buttonReady;
    private Button buttonLeave;

    //Audio
    Audio audio;
    AudioRecordRecord arr;
    AudioTrackReceive atr;
    AudioTrack track;
    private Button buttonMic;
    boolean flag = true;

    RelativeLayout drawing_container;
    GuessFragment guessFragment;

    // 玩家資訊
    ConstraintLayout constraintLayoutPlayer1;
    ConstraintLayout constraintLayoutPlayer2;
    ConstraintLayout constraintLayoutPlayer3;
    ConstraintLayout constraintLayoutPlayer4;
    ArrayList<ConstraintLayout> playerLayoutList = new ArrayList<>();

    // players
    ArrayList<PlayerFragment> playerFragmentList = new ArrayList<>();
    String playerLeaveID;
    LinkedList<Player> playerSequenceList = new LinkedList<>();


    ProcessFragment processFragment = new ProcessFragment();


    private TextView textViewChat;

    public TextView getTextViewChat() {
        return textViewChat;
    }

    @SuppressLint("HandlerLeak")
    public Handler handler_room = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    fragmentManagerRoom
                            .beginTransaction()
                            .replace(R.id.drawing_container, new DrawFragment(roomSocket))
                            .commit();
                    break;
                case 2:
                    fragmentManagerRoom
                            .beginTransaction()
                            .replace(R.id.drawing_container, guessFragment = new GuessFragment())
                            .commit();

                    guessFragment.setGuessView(new GuessView(getContext()));
                    break;
                case 3:
                    fragmentManagerRoom
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_in)
                            .replace(R.id.process_container, processFragment)
                            .commit();

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processFragment.getTextViewTitle().setText("");
                            processFragmentSwitcher(processFragment);
                        }
                    }, 1500);

                    break;

                case 4:
                    fragmentManagerRoom
                            .beginTransaction()
                            .replace(R.id.drawing_container, new FingerDrawFragment())
                            .commit();

                    buttonReady.setText("準備");
                    break;

                case 5:
                    textViewChat = new TextView(getContext());
                    textViewChat.setText("題目: " + conn_server_tcp.sfc.getQuestion());
                    textViewChat.setTextColor(Color.BLUE);
                    linearLayoutChat.addView(textViewChat);

                    scrollViewChat.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    break;

                case 6:
                    for (PlayerFragment pf : playerFragmentList) {
                    }

                    textViewChat = new TextView(getContext());
                    textViewChat.setText("");
                    textViewChat.setTextColor(Color.BLUE);
                    linearLayoutChat.addView(textViewChat);

                    scrollViewChat.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    break;
                case 7:


                    break;
            }
        }
    };


    public RoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        conn_server_tcp = new ReceiveFromServer_TCP(roomSocket, this);
        conn_server_tcp.start(); // 處理進房後的所有接收

        fragmentManagerRoom = getFragmentManager();
        fragmentManagerRoom.beginTransaction().replace(R.id.drawing_container, new FingerDrawFragment()).commit();

        player = MainActivity.appDatabase.playerDao().getPlayerBySerialID(Build.SERIAL);
//        playerFragmentList.add(new PlayerFragment(player));

        // chatting area
        linearLayoutChat = (LinearLayout) view.findViewById(R.id.linearLayoutChat);
        scrollViewChat = (ScrollView) view.findViewById(R.id.scrollViewChat);
        editTextChat = (EditText) view.findViewById(R.id.editTextChat);

        Button buttonSubmit = (Button) view.findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);


        // 右下
        textViewCountDown = (TextView) view.findViewById(R.id.textViewCountDown);

        buttonStart = (Button) view.findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(this);

        buttonReady = (Button) view.findViewById(R.id.buttonReady);
        buttonReady.setOnClickListener(this);

        buttonLeave = (Button) view.findViewById(R.id.buttonLeave);
        buttonLeave.setOnClickListener(this);

        drawing_container = (RelativeLayout) view.findViewById(R.id.drawing_container);


        constraintLayoutPlayer1 = view.findViewById(R.id.constraintLayoutPlayer1);
        playerLayoutList.add(constraintLayoutPlayer1);
        constraintLayoutPlayer2 = view.findViewById(R.id.constraintLayoutPlayer2);
        playerLayoutList.add(constraintLayoutPlayer2);
        constraintLayoutPlayer3 = view.findViewById(R.id.constraintLayoutPlayer3);
        playerLayoutList.add(constraintLayoutPlayer3);
        constraintLayoutPlayer4 = view.findViewById(R.id.constraintLayoutPlayer4);
        playerLayoutList.add(constraintLayoutPlayer4);


        // Audio
        AudioConnect ac = new AudioConnect(VoiceCallPort_UDP, String.valueOf(player.getUserID()));
        audio = ac.getAudio();

        //AudioTrack設置
        new AudioTrackSet(RoomFragment.this);

        //播放語音 放到另外一個thread
        AudioTrackPlay atp = new AudioTrackPlay(track);
        atp.start();

        //接收語音
        atr = new AudioTrackReceive(audio.getDatagramSocket(), track, atp);
        atr.start();



        buttonMic = (Button) view.findViewById(R.id.buttonMic);
        buttonMic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if (flag) {
                        // 錄製
                        arr = new AudioRecordRecord(audio);
                        arr.start();
                        flag = false;
                    } else {
                        // notify
                        arr.restartRecording();
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            arr.stopRecording();
                        }
                    }).start();
                }
                return true;
            }
        });


        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonSubmit:
                if (!editTextChat.getText().toString().equals("")) {
                    textViewChat = new TextView(getContext());
                    textViewChat.setText(player.getUserName() + ":\n" + editTextChat.getText());
                    textViewChat.setTextColor(Color.RED);
                    linearLayoutChat.addView(textViewChat);
                    editTextChat.setText("");

                    scrollViewChat.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                    String say = String.valueOf(textViewChat.getText());

                    new Client_FunctionCode("10", roomSocket, say);
                }
                break;
//            case R.id.buttonMic:
//                // 建立語音連線
//                arr = new AudioRecordRecord(VoiceCallPort_UDP, player.getUserID());
//                arr.start();
//
//                //AudioTrack設置
//                new AudioTrackSet(RoomFragment.this);
//
//                //播放語音 放到另外一個thread
//                AudioTrackPlay atp = new AudioTrackPlay(track);
//                atp.start();
//
//                //接收語音
//                atr = new AudioTrackReceive(arr.getSendSocket(), track, atp);
//                atr.start();
//                break;

            case R.id.buttonStart:
                break;

            case R.id.buttonReady:
                if (buttonReady.getText().equals("準備")) {
                    new Client_FunctionCode("021", roomSocket);
                    buttonReady.setText("取消");
                    new Client_FunctionCode("10", roomSocket, player.getUserName() + "已準備");

                } else if (buttonReady.getText().equals("取消")) {
                    new Client_FunctionCode("020", roomSocket);
                    buttonReady.setText("準備");
                    new Client_FunctionCode("10", roomSocket, player.getUserName() + "取消準備");
                }
                break;

            case R.id.buttonLeave:
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("離開房間")
                        .setPositiveButton("離開", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
//                                    new Client_FunctionCode("09", roomSocket);
                                    roomSocket.close();
                                    conn_server_tcp.exit = false;
                                    conn_server_tcp.join();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                UI.fragmentSwitcher(new LobbyFragment(), false);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                UI.showImmersiveModeDialog(alertDialog, true);
                alertDialog.getWindow().setLayout(UI.width / 3, UI.height / 25 * 9);
                break;


        }
    }

    public void setTrack(AudioTrack track) {
        this.track = track;
    }

    void processFragmentSwitcher(Fragment fragment) {
        if (processFragment.isVisible()) {
            fragmentManagerRoom.beginTransaction().remove(fragment).commit();
        } else {
            fragmentManagerRoom.beginTransaction().remove(fragment).commit();
            fragmentManagerRoom
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_in)
                    .replace(R.id.process_container, processFragment)
                    .commit();
        }
    }


}
