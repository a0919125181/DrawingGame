package com.example.user.drawinggame.Lobby.Friend;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.database_classes.Friend;
import com.example.user.drawinggame.database_classes.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFriendFragment extends Fragment {


    TextView textView;

    public MyFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_friend, container, false);


        textView = view.findViewById(R.id.textView);
        String s = "My Friend";

        List<Friend> friendList = MainActivity.appDatabase.friendDao().getAllFriends();

        Log.i("size", String.valueOf(friendList.size()));

        for (Friend f : friendList) {
            s += ("/n" + f.getUserName());
        }

        textView.setText(s);

        return view;
    }

}
