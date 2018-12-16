package com.example.user.drawinggame.Room;

import android.graphics.Color;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.user.drawinggame.R;
import com.example.user.drawinggame.Room.Drawing.GuessView;
import com.example.user.drawinggame.connections.TCP.Util;
import com.example.user.drawinggame.connections.php.SearchThread;
import com.example.user.drawinggame.database_classes.Player;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class Server_FunctionCode {

    private String functionCode;
    private InputStream receiveFromServer;
    private RoomFragment fragment;

    private byte[] ID_array;
    private String ID;

    public Server_FunctionCode(String functionCode, InputStream receiveFromServer, RoomFragment fragment) {
        this.functionCode = functionCode;
        this.receiveFromServer = receiveFromServer;
        this.fragment = fragment;

        getFunction();
    }

    //01
    private void audioUDP() {
        byte[] UDP_port_array = new byte[4];
        try {
            receiveFromServer.read(UDP_port_array, 0, 4); // 接收UDP port
        } catch (IOException e) {
            e.printStackTrace();
        }
        String UDP_port = new String(UDP_port_array);
        Util.setVoiceCallPort_UDP(fragment, UDP_port);
        fragment.setUdpPort(UDP_port); // 回傳UDP port
        Log.e("udpPort", UDP_port);
    }

    private void setReady() {
        ID_array = new byte[3];
        byte[] readyCancel_array = new byte[1];
        try {
            receiveFromServer.read(ID_array, 0, 3);             // 接收ID
            receiveFromServer.read(readyCancel_array, 0, 1);    // 接收準備 || 取消
        } catch (IOException e) {
            e.printStackTrace();
        }

        ID = new String(ID_array);
        String readyCancel = new String(readyCancel_array);
        final int id = Integer.parseInt(ID);

        for (final PlayerFragment pf : fragment.playerFragmentList) {
            if (pf.getPlayer().getUserID() == id) {

                final String status;
                if (readyCancel.equals("0")) {
                    status = "取消準備";
                    Log.i(ID, status);
                } else {
                    status = "已準備";
                    Log.i(ID, status);
                }


                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textViewChat = new TextView(fragment.getContext());
                        textViewChat.setText(pf.getPlayer().getUserName() + " " + status);
                        textViewChat.setTextColor(Color.DKGRAY);
                        fragment.linearLayoutChat.addView(textViewChat);

                        final ScrollView scrollViewChat = fragment.getScrollViewChat();
                        scrollViewChat.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });

                        if (pf.getImageViewPlayer() != null) {
                            if (status == "已準備") {
                                pf.getImageViewPlayer().setBackgroundResource(R.drawable.button_brush);
                            } else if (status == "取消準備") {
                                pf.getImageViewPlayer().setBackgroundResource(0);
                            }
                        }

                    }
                });

            }
        }

    }

    // 03
    private void playerSequence() {
        try {
            ID_array = new byte[15];
            receiveFromServer.read(ID_array, 0, 15); // 接收ID
            ID = new String(ID_array);
            Log.e("順序", ID);

            int firstID = Integer.parseInt(ID.substring(0, 3));
            int secondID = Integer.parseInt(ID.substring(3, 6));
            int thirdID = Integer.parseInt(ID.substring(6, 9));
            int fourthID = Integer.parseInt(ID.substring(9, 12));
            int fifthID = Integer.parseInt(ID.substring(12, 15));


            // 排順序
            List<PlayerFragment> playerFragmentList = fragment.playerFragmentList;
            List<Player> playerSequenceList = fragment.playerSequenceList;

            addSequenceList(playerFragmentList, playerSequenceList, firstID);
            addSequenceList(playerFragmentList, playerSequenceList, secondID);
            addSequenceList(playerFragmentList, playerSequenceList, thirdID);
            addSequenceList(playerFragmentList, playerSequenceList, fourthID);
            addSequenceList(playerFragmentList, playerSequenceList, fifthID);

            Message msg = new Message();
            msg.what = 3;
            fragment.handler_room.sendMessage(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addSequenceList(List<PlayerFragment> pfl, List<Player> psl, int id) {
        Log.e("seq", String.valueOf(id));
        for (PlayerFragment pf : pfl) {
            if (id == 0) {
                break;
            }
            if (pf.getPlayer().getUserID() == id) {
                psl.add(pf.getPlayer());
            }
        }
        if (fragment.player.getUserID() == id) {
            psl.add(fragment.player);
        }
    }


    // 功能碼04
    private void readQuestion() {
        ID_array = new byte[3];
        try {
            receiveFromServer.read(ID_array, 0, 3); // 接收ID
        } catch (IOException e) {
            e.printStackTrace();
        }
        ID = new String(ID_array);
        Log.e(ID, "看題目");


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        final int id = Integer.parseInt(ID);
        if (id == fragment.player.getUserID()) {
            Message msg = new Message();
            msg.what = 5;
            fragment.handler_room.sendMessage(msg);
        } else {
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (PlayerFragment pf : fragment.playerFragmentList) {
                        if (pf.getPlayer().getUserID() == id) {
                            fragment.processFragment.setTitle(pf.getPlayer().getUserName() + "\n看題目");
                            Message msg = new Message();
                            msg.what = 7;
                            fragment.handler_room.sendMessage(msg);
                        }
                    }
                }
            });
        }
    }

    //05
    private void painting() {
        ID_array = new byte[3];
        byte[] screenWidth_array = new byte[4];
        byte[] screenHeight_array = new byte[4];
        byte[] drawPointX_array = new byte[4];
        byte[] drawPointY_array = new byte[4];
        byte[] thickThin_array = new byte[2];
        byte[] color_array = new byte[1];
        byte[] state_array = new byte[1];

        try {
            receiveFromServer.read(ID_array, 0, 3);             // 接收ID
            receiveFromServer.read(screenWidth_array, 0, 4);    // 接收對方View寬
            receiveFromServer.read(screenHeight_array, 0, 4);   // 接收對方View高
            receiveFromServer.read(drawPointX_array, 0, 4);     // 接收X座標
            receiveFromServer.read(drawPointY_array, 0, 4);     // 接收Y座標
            receiveFromServer.read(thickThin_array, 0, 2);      // 接收粗細
            receiveFromServer.read(color_array, 0, 1);          // 接收畫筆顏色
            receiveFromServer.read(state_array, 0, 1);          // 接收state Down Move Up
        } catch (IOException e) {
            e.printStackTrace();
        }

        ID = new String(ID_array);
        String screenWidth = new String(screenWidth_array);
        String screenHeight = new String(screenHeight_array);
        String drawPointX = new String(drawPointX_array);
        String drawPointY = new String(drawPointY_array);
        String thickThin = new String(thickThin_array);
        String color = new String(color_array);
        String state = new String(state_array);

//        Log.i("對方View寬高", screenWidth + " , " + screenHeight);
//        Log.i("接收座標", drawPointX + " , " + drawPointY + " 粗細 " + thickThin + " 畫筆顏色 " + color);
//        Log.i("狀態", state);


        try {
            GuessView gv = fragment.guessFragment.getGuessView();
            gv.convertSize(screenWidth, screenHeight);
            if (state.equals("D")) {
                fragment.guessFragment.getGuessPath().setPast(drawPointX, drawPointY);
            } else if (state.equals("M")) {
                gv.setPathPen(fragment.guessFragment.getGuessPath().getPastX(), fragment.guessFragment.getGuessPath().getPastY(), drawPointX, drawPointY, thickThin, color);
                fragment.guessFragment.getGuessPath().setPast(drawPointX, drawPointY);
            } else {
                gv.setPathPen(fragment.guessFragment.getGuessPath().getPastX(), fragment.guessFragment.getGuessPath().getPastY(), drawPointX, drawPointY, thickThin, color);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    //06
    private void playerEnter() {
        ID_array = new byte[3];
        try {
            receiveFromServer.read(ID_array, 0, 3); //接收ID
        } catch (IOException e) {
            e.printStackTrace();
        }
        ID = new String(ID_array);
        Log.i(ID, "加入房間");

        Player player_enter = new Player();
        player_enter.setUserID(Integer.parseInt(ID));
        new SearchThread(player_enter).start();

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.e("player enter", "name: " + player_enter.getUserName());
        final PlayerFragment pf = new PlayerFragment(player_enter, fragment.player);
        fragment.playerFragmentList.add(pf);
//        final String enterName = pf.getPlayer().getUserName();

        // UI
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < fragment.playerFragmentList.size(); i++) {
                    ConstraintLayout layout = fragment.playerLayoutList.get(i);
                    if (layout.getVisibility() == View.INVISIBLE) {
                        layout.setVisibility(View.VISIBLE);
                        fragment.fragmentManagerRoom.beginTransaction().replace(layout.getId(), pf).commit();

                        break;
                    }
                }

//                TextView textViewChat = new TextView(fragment.getContext());
//                textViewChat.setText(enterName + " 進入房間");
//                textViewChat.setTextColor(Color.MAGENTA);
//                fragment.linearLayoutChat.addView(textViewChat);
//
//                final ScrollView scrollViewChat = fragment.getScrollViewChat();
//                scrollViewChat.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN);
//                    }
//                });
            }
        });
    }

    // 07
    private void receiveQuestion() {
        try {
            byte[] question_len = new byte[2];
            receiveFromServer.read(question_len, 0, 2); // 訊息bytes 大小
            int len = Integer.parseInt(new String(question_len));

            byte[] question_array = new byte[len];
            receiveFromServer.read(question_array, 0, len); // 接收題目
            String question = new String(question_array);
            Log.i("收到題目", question);

            fragment.setQuestion(question);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 08
    private void playerTurn() {
        ID_array = new byte[3];
        try {
            receiveFromServer.read(ID_array, 0, 3); // 接收ID
        } catch (IOException e) {
            e.printStackTrace();
        }
        ID = new String(ID_array);
        Log.e("換", ID);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 切換
        int id = Integer.parseInt(ID);

        List<Player> psl = fragment.playerSequenceList;

        Player drawPlayer = new Player();
        int draw_pos = 0;
        int my_pos = 0;

        for (int i = 0; i < psl.size(); i++) {
            if (psl.get(i).getUserID() == id) {
                draw_pos = i;
                drawPlayer = psl.get(i);
            }
            if (fragment.player.getUserID() == psl.get(i).getUserID()) {
                my_pos = i;
            }
        }


        if (fragment.player.getUserID() == id) {
            Message msg = new Message();
            msg.what = 1;
            fragment.handler_room.sendMessage(msg);
        } else if (my_pos <= draw_pos + 1) {
            Message msg = new Message();
            msg.what = 2;
            fragment.handler_room.sendMessage(msg);
        } else {
            fragment.processFragment.setTitle(drawPlayer.getUserName() + "\n正在畫");
            Message msg = new Message();
            msg.what = 8;
            fragment.handler_room.sendMessage(msg);
        }

    }


    private void playerLeave() {
        ID_array = new byte[3];
        try {
            receiveFromServer.read(ID_array, 0, 3); // 接收ID
        } catch (IOException e) {
            e.printStackTrace();
        }
        ID = new String(ID_array);
        Log.e(ID, "斷線");

        final int leaveID = Integer.parseInt(ID);

        final List<PlayerFragment> pfl = fragment.playerFragmentList;
        final List<ConstraintLayout> layoutList = fragment.playerLayoutList;

        // UI
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String leaveName = "有人";

                for (int i = 0; i < layoutList.size(); i++) {
                    ConstraintLayout layout = layoutList.get(i);
                    if (pfl.get(i).getPlayer().getUserID() == leaveID) {
                        PlayerFragment pf = pfl.get(i);
                        leaveName = pf.getPlayer().getUserName();
                        pfl.remove(pf);

                        layout.setVisibility(View.INVISIBLE);
                        fragment.fragmentManagerRoom.beginTransaction().remove(pf).commit();

                        break;
                    }
                }

                TextView textViewChat = new TextView(fragment.getContext());
                textViewChat.setText(leaveName + " 離開房間");
                textViewChat.setTextColor(Color.MAGENTA);
                fragment.linearLayoutChat.addView(textViewChat);

                final ScrollView scrollViewChat = fragment.getScrollViewChat();
                scrollViewChat.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

    }

    private void chatting() {
        byte[] msg_array = new byte[3];
        try {
            receiveFromServer.read(msg_array, 0, 3); // 訊息bytes 大小

            int len = Integer.parseInt(new String(msg_array));

            byte[] say_array = new byte[len];

            receiveFromServer.read(say_array, 0, len);

            final String say = new String(say_array);

            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = new TextView(fragment.getContext());
                    tv.setText(tv.getText() + say);
                    fragment.linearLayoutChat.addView(tv);

                    final ScrollView sv = fragment.getScrollViewChat();
                    sv.post(new Runnable() {
                        @Override
                        public void run() {
                            sv.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 11
    private void guess() {
        Log.e("guess", "answer");
        byte[] guess_array = new byte[3];
        try {
            receiveFromServer.read(guess_array, 0, 3); // 訊息bytes 大小
        } catch (IOException e) {
            e.printStackTrace();
        }


        Message msg = new Message();
        msg.what = 6;
        fragment.handler_room.sendMessage(msg);

    }

    //12
    private void checkUdpConnection() {
        Log.e("UDP", "connect success");
        fragment.setUdpConnectionState();
    }

    //13
    private void clearGuessView() {
        try {
            GuessView gv = fragment.guessFragment.getGuessView();
            gv.drawWholeWhite();
        } catch (NullPointerException e) {
            Log.e("Warning", "沒畫板");
        }

    }

    // 15
    private void ansCorrect() {
        Log.e("function", "15");
        try {
            ID_array = new byte[3];
            byte[] correct_array = new byte[1];

            byte[] len_array = new byte[2];


            receiveFromServer.read(ID_array, 0, 3);         // 接收ID
            receiveFromServer.read(correct_array, 0, 1);    // 接收答對 || 答錯
            receiveFromServer.read(len_array, 0, 2);        // 題目長度

            int len = Integer.parseInt(new String(len_array));
            byte[] ans = new byte[len];
            receiveFromServer.read(ans, 0, len);                // 題目
            final String guess = new String(ans);


            int id = Integer.parseInt(new String(ID_array));
            int iCorrect = Integer.parseInt(new String(correct_array));


            List<Player> playerSequenceList = fragment.playerSequenceList;
            for (Player p : playerSequenceList) {
                final Player player = p;
                if (p.getUserID() == id) {

                    if (iCorrect == 1) {
                        fragment.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textViewChat = new TextView(fragment.getContext());
                                textViewChat.setText(player.getUserName() + " 答對!");
                                textViewChat.setTextColor(Color.parseColor("#FF8800"));
                                fragment.linearLayoutChat.addView(textViewChat);

                                final ScrollView scrollViewChat = fragment.getScrollViewChat();
                                scrollViewChat.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN);
                                    }
                                });
                            }
                        });
                    } else if (iCorrect == 0) {
                        fragment.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textViewChat = new TextView(fragment.getContext());
                                textViewChat.setText(player.getUserName() + " 答錯! (" + guess + ")");
                                textViewChat.setTextColor(Color.parseColor("#BBBB00"));
                                fragment.linearLayoutChat.addView(textViewChat);

                                final ScrollView scrollViewChat = fragment.getScrollViewChat();
                                scrollViewChat.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN);
                                    }
                                });
                            }
                        });
                    }

                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //20
    private void endGame() {
        Message msg = new Message();
        msg.what = 4;
        fragment.handler_room.sendMessage(msg);
    }

    private void getFunction() {
        switch (functionCode) {
            case "01":
                audioUDP();
                break;
            case "02":
                setReady();
                break;
            case "03":
                playerSequence();
                break;
            case "04":
                readQuestion();
                break;
            case "05":
                painting();
                break;
            case "06":
                playerEnter();
                break;
            case "07":
                receiveQuestion();
                break;
            case "08":
                playerTurn();
                break;
            case "09":
                playerLeave();
                break;
            case "10":
                chatting();
                break;
            case "11":
                guess();
                break;
            case "12":
                checkUdpConnection();
                break;
            case "13":
                clearGuessView();
                break;
            case "15":
                ansCorrect();
                break;
            case "20":
                endGame();
                break;
        }
    }


}
