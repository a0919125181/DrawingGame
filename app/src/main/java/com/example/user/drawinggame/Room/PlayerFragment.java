package com.example.user.drawinggame.Room;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.drawinggame.R;
import com.example.user.drawinggame.database_classes.Player;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    private ImageView imageViewPlayer;
    private TextView textViewPlayerName;

    private Player player;

    public PlayerFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public PlayerFragment(Player player) {
        this.player = player;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        imageViewPlayer = (ImageView) view.findViewById(R.id.imageViewPlayer);
        textViewPlayerName = (TextView) view.findViewById(R.id.textViewPlayerName);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!textViewPlayerName.getText().equals(player.getUserName())) {
                    textViewPlayerName.setText(player.getUserName());
                }
            }
        }).start();

        imageViewPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewFriendInfo = LayoutInflater.from(getActivity()).inflate(R.layout.show_friend_info, null);

                TextView textViewID = viewFriendInfo.findViewById(R.id.textViewID);
                textViewID.setText("ID: " + String.valueOf(player.getUserID()));

                TextView textViewName = viewFriendInfo.findViewById(R.id.textViewName);
                textViewName.setText("名字: " + player.getUserName());

                TextView textViewIntro = viewFriendInfo.findViewById(R.id.textViewIntro);
                textViewIntro.setText("自我介紹: " + player.getIntro());

                TextView textViewAge = viewFriendInfo.findViewById(R.id.textViewAge);
                textViewAge.setText("年紀: " + String.valueOf(player.getAge()));

                TextView textViewGender = viewFriendInfo.findViewById(R.id.textViewGender);
                textViewGender.setText(player.getGender() == 1 ? "性別: 男" : "性別: 女");

                TextView textViewLevel = viewFriendInfo.findViewById(R.id.textViewLevel);
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
