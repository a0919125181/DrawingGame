package com.example.user.drawinggame.Room;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioTrack;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.user.drawinggame.Room.Drawing.GuessPath;
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
    private TextView textViewStatus;
    private Button buttonPrompt;
    private Button buttonReady;
    private Button buttonLeave;

    public TextView getTextViewStatus() {
        return textViewStatus;
    }

    //Audio
    private Audio audio;
    private AudioConnect ac;
    private AudioRecordRecord arr;
    private AudioTrackReceive atr;
    private AudioTrack track;
    private AudioTrackPlay atp;
    private ImageView imageViewMic;
    boolean isConnectUdpServer = false;

    public AudioTrackReceive getAtr() {
        return atr;
    }

    //draw
    RelativeLayout drawing_container;
    GuessFragment guessFragment;
    GuessPath gp;

    // 玩家資訊
    ConstraintLayout constraintLayoutPlayer1;
    ConstraintLayout constraintLayoutPlayer2;
    ConstraintLayout constraintLayoutPlayer3;
    ConstraintLayout constraintLayoutPlayer4;
    ArrayList<ConstraintLayout> playerLayoutList = new ArrayList<>();

    // players
    ArrayList<PlayerFragment> playerFragmentList = new ArrayList<>();
    LinkedList<Player> playerSequenceList = new LinkedList<>();


    ProcessFragment processFragment = new ProcessFragment();
    private AnswerFragment answerFragment;

    private String question;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

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
                    processFragment.setTitle("開始畫");
                    processFragmentSwitcher(processFragment);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processFragmentSwitcher(processFragment);
                        }
                    }, 800);

                    fragmentManagerRoom
                            .beginTransaction()
                            .replace(R.id.drawing_container, new DrawFragment(roomSocket))
                            .commit();

                    textViewStatus.setText("趕快畫");

                    break;
                case 2:
                    processFragment.setTitle("看圖");
                    processFragmentSwitcher(processFragment);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processFragmentSwitcher(processFragment);
                        }
                    }, 800);

                    fragmentManagerRoom
                            .beginTransaction()
                            .replace(R.id.drawing_container, guessFragment = new GuessFragment())
                            .commit();

                    gp = new GuessPath();
                    guessFragment.setGuessView(new GuessView(getContext()));

                    textViewStatus.setText("仔細看");

                    break;
                case 3:
                    processFragment.setTitle("遊戲開始");
                    textViewStatus.setText("遊戲開始");

                    buttonReady.setEnabled(false);

                    for (PlayerFragment pf : playerFragmentList) {
                        pf.getImageViewPlayer().setBackgroundResource(0);
                    }

                    processFragmentSwitcher(processFragment);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processFragmentSwitcher(processFragment);
                        }
                    }, 1500);

                    break;

                case 4:

                    // 遊戲結束 功能代碼20
                    fragmentManagerRoom
                            .beginTransaction()
                            .replace(R.id.drawing_container, new FingerDrawFragment())
                            .commit();


                    processFragment.setTitle("遊戲結束");
                    processFragmentSwitcher(processFragment);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processFragmentSwitcher(processFragment);
                        }
                    }, 800);

                    UI.hideSystemUI(getActivity());


                    buttonReady.setText("準備");
                    buttonReady.setEnabled(true);
                    textViewStatus.setText("請準備");

                    textViewChat = new TextView(getContext());
                    textViewChat.setText("答案: " + getQuestion());
                    textViewChat.setTextColor(Color.BLUE);
                    linearLayoutChat.addView(textViewChat);

                    getScrollViewChat().post(new Runnable() {
                        @Override
                        public void run() {
                            scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                    playerSequenceList.clear();

                    break;

                case 5:
                    // 空白
                    fragmentManagerRoom
                            .beginTransaction()
                            .replace(R.id.drawing_container, new Fragment())
                            .commit();

                    textViewChat = new TextView(getContext());
                    textViewChat.setText("題目: " + getQuestion());
                    textViewChat.setTextColor(Color.BLUE);
                    linearLayoutChat.addView(textViewChat);

                    getScrollViewChat().post(new Runnable() {
                        @Override
                        public void run() {
                            scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                    processFragment.setTitle("題目: " + question);

                    processFragmentSwitcher(processFragment);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processFragmentSwitcher(processFragment);
                        }
                    }, 1500);
                    break;

                case 6:
                    // 猜題
                    processFragment.setTitle("猜題");
                    textViewStatus.setText("猜題");

                    processFragmentSwitcher(processFragment);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processFragmentSwitcher(processFragment);
                        }
                    }, 1500);

                    if (player.getUserID() != playerSequenceList.getFirst().getUserID()) {
                        textViewChat = new TextView(getContext());
                        textViewChat.setText("請輸入答案: ");
                        textViewChat.setTextColor(Color.parseColor("#006400"));
                        linearLayoutChat.addView(textViewChat);

                        fragmentManagerRoom
                                .beginTransaction()
                                .replace(R.id.drawing_container, answerFragment = new AnswerFragment(RoomFragment.this))
                                .commit();
                    } else {
                        fragmentManagerRoom
                                .beginTransaction()
                                .replace(R.id.drawing_container, new Fragment())
                                .commit();
                    }


                    break;
                case 7:
                    // username 看題目
                    processFragmentSwitcher(processFragment);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processFragmentSwitcher(processFragment);
                        }
                    }, 1500);

                    // 空白
                    fragmentManagerRoom
                            .beginTransaction()
                            .replace(R.id.drawing_container, new Fragment())
                            .commit();

                    break;

                case 8:
                    // 等人畫

                    processFragmentSwitcher(processFragment);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            processFragmentSwitcher(processFragment);
                        }
                    }, 1500);

                    fragmentManagerRoom
                            .beginTransaction()
                            .replace(R.id.drawing_container, new Fragment())
                            .commit();
                    break;

                case 9:
                    // talking
                    for (PlayerFragment pf : playerFragmentList) {
                        if (pf.getPlayer().getUserID() == atp.getSenderID()) {
                            pf.getImageViewPlayer().setBackgroundResource(R.drawable.bg_pf_yello);
                        }
                    }
                    break;

                case 10:
                    // stop talking
                    for (PlayerFragment pf : playerFragmentList) {
                        pf.getImageViewPlayer().setBackgroundResource(0);
                    }
                    break;
            }
        }
    };


    public RoomFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        player = MainActivity.appDatabase.playerDao().getPlayerBySerialID(Build.SERIAL);

        conn_server_tcp = new ReceiveFromServer_TCP(roomSocket, this);
        conn_server_tcp.start(); // 處理進房後的所有接收

        fragmentManagerRoom = getFragmentManager();
        fragmentManagerRoom.beginTransaction().replace(R.id.drawing_container, new FingerDrawFragment()).commit();


        // chatting area
        linearLayoutChat = (LinearLayout) view.findViewById(R.id.linearLayoutChat);
        scrollViewChat = (ScrollView) view.findViewById(R.id.scrollViewChat);
        editTextChat = (EditText) view.findViewById(R.id.editTextChat);

        ImageView imageViewSubmit = (ImageView) view.findViewById(R.id.imageViewSubmit);
        imageViewSubmit.setOnClickListener(this);


        // 右下
        textViewStatus = (TextView) view.findViewById(R.id.textViewStatus);

        buttonPrompt = (Button) view.findViewById(R.id.buttonPrompt);
        buttonPrompt.setOnClickListener(this);

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


        imageViewMic = view.findViewById(R.id.imageViewMic);
        imageViewMic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isConnectUdpServer) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                arr.restartRecording(); // notify
                            }
                        }).start();
                        imageViewMic.setBackgroundResource(R.drawable.bg_pf_yello);
                    } else {
                        arr = new AudioRecordRecord(audio, RoomFragment.this); // 錄製
                        arr.start();
                        imageViewMic.setBackgroundResource(R.drawable.bg_pf_yello);

                        // who is talking
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                while (true) {
//                                    if (atr != null) {
//                                        if (atp.isPlaying()) {
//                                            if (atp.getSenderID() != 0) {
//                                                Log.e("play id", String.valueOf(atp.getSenderID()));
//                                                Message msg = new Message();
//                                                msg.what = 9;
//                                                handler_room.sendMessage(msg);
//                                            }
//                                        } else {
//                                            Message msg = new Message();
//                                            msg.what = 10;
//                                            handler_room.sendMessage(msg);
//                                        }
//                                    }
//                                }
//                            }
//                        }).start();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            arr.stopRecording();
                        }
                    }).start();
                    imageViewMic.setBackgroundResource(0);
                }
                return true;
            }
        });

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imageViewSubmit:
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

            case R.id.imageViewMic:
                break;

            case R.id.buttonPrompt:
                String port = "TCP: " + RoomPort_TCP + "\nUDP: " + VoiceCallPort_UDP;
                Toast.makeText(getContext(), port, Toast.LENGTH_LONG).show();

                break;

            case R.id.buttonReady:
                if (buttonReady.getText().equals("準備")) {
                    new Client_FunctionCode("021", roomSocket);
                    buttonReady.setText("取消");
                    textViewStatus.setText("等待其他人\n準備");

                } else if (buttonReady.getText().equals("取消")) {
                    new Client_FunctionCode("020", roomSocket);
                    buttonReady.setText("準備");
                    textViewStatus.setText("請準備");

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
                                    atr.closeReceiving();
                                    roomSocket.close();
                                    conn_server_tcp.exit = false;
                                    conn_server_tcp.join();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }

                                UI.fragmentSwitcher(new LobbyFragment(), false);
                                onDestroy();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                UI.showImmersiveModeDialog(alertDialog, true);
                alertDialog.getWindow().setLayout(UI.width / 3, UI.height / 25 * 9);


                break;

        }
    }

    public void setUdpPort(String port) {
        // set Audio
        ac = new AudioConnect(Integer.parseInt(port), String.valueOf(player.getUserID()));
        audio = ac.getAudio();

        //AudioTrack設置
        track = (new AudioTrackSet()).getTrack();
        track.play();

        //播放語音 放到另外一個thread
        atp = new AudioTrackPlay(track, RoomFragment.this);
        atp.start();

        //接收語音
        atr = new AudioTrackReceive(audio, atp);
        atr.start();
    }

    public void setTrack(AudioTrack track) {
        this.track = track;
    }

    void setUdpConnectionState() {
        isConnectUdpServer = true;
    }

    public boolean getUdpConnectionState() {
        return isConnectUdpServer;
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            audio.getDatagramSocket().close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
