package com.example.user.drawinggame.Lobby.Friend;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.user.drawinggame.Lobby.Message.MessageAdapter;
import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.database_classes.Message;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendInviteFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<Message> messageList;
    private ListView listViewFriend;


    public FriendInviteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_invite, container, false);

        messageList = MainActivity.appDatabase.messageDao().getMessagesByType(1);



        listViewFriend = (ListView) view.findViewById(R.id.listViewFriend);
        MessageAdapter adapter = new MessageAdapter(getContext(), messageList);
        listViewFriend.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // do item click
    }


}
