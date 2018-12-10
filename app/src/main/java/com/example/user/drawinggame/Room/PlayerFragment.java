package com.example.user.drawinggame.Room;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.drawinggame.R;
import com.example.user.drawinggame.connections.php.SendMsgThread;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    private ImageView imageViewPlayer;

    public ImageView getImageViewPlayer() {
        return imageViewPlayer;
    }

    private TextView textViewPlayerName;

    private Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private Player mPlayer;

    public PlayerFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public PlayerFragment(Player player, Player mPlayer) {
        this.player = player;
        this.mPlayer = mPlayer;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        imageViewPlayer = (ImageView) view.findViewById(R.id.imageViewPlayer);
        new UI.DownloadImageTask(imageViewPlayer).execute(player.getPicURL());

        textViewPlayerName = (TextView) view.findViewById(R.id.textViewPlayerName);
        textViewPlayerName.setText(player.getUserName());


        imageViewPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewFriendInfo = LayoutInflater.from(getActivity()).inflate(R.layout.show_friend_info, null);

                ImageView imageViewFriend = viewFriendInfo.findViewById(R.id.imageViewFriend);
                new UI.DownloadImageTask(imageViewFriend).execute(player.getPicURL());

                ImageView imageViewAddFriend = viewFriendInfo.findViewById(R.id.imageViewAddFriend);
                imageViewAddFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SendMsgThread smt = new SendMsgThread(mPlayer, "好友++", player.getUserID(), 1);
                        smt.start();

                        while (!smt.isDone()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (smt.isSuccess()) {
                                Toast.makeText(getContext(), "交友邀請已送出", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "邀請失敗", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                TextView textViewID = viewFriendInfo.findViewById(R.id.textViewID);
                TextView textViewName = viewFriendInfo.findViewById(R.id.textViewName);
                TextView textViewIntro = viewFriendInfo.findViewById(R.id.textViewIntro);
                TextView textViewAge = viewFriendInfo.findViewById(R.id.textViewAge);
                TextView textViewGender = viewFriendInfo.findViewById(R.id.textViewGender);
                TextView textViewLevel = viewFriendInfo.findViewById(R.id.textViewLevel);

                textViewID.setText("ID: " + String.valueOf(player.getUserID()));
                textViewName.setText("名字: " + player.getUserName());
                textViewIntro.setText("自我介紹: " + player.getIntro());
                textViewAge.setText("年紀: " + String.valueOf(player.getAge()));
                textViewGender.setText(player.getGender() == 1 ? "性別: 男" : "性別: 女");
                textViewLevel.setText("等級: " + String.valueOf(player.getLevel()));

                new AlertDialog.Builder(getContext())
                        .setView(viewFriendInfo)
                        .create()
                        .show();
            }
        });

        return view;
    }

}
