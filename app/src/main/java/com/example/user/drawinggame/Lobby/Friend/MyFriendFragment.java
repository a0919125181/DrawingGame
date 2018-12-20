package com.example.user.drawinggame.Lobby.Friend;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.connections.php.SearchThread;
import com.example.user.drawinggame.database_classes.Friend;
import com.example.user.drawinggame.database_classes.Message;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFriendFragment extends Fragment implements AdapterView.OnItemClickListener {

    private Player mPlayer;

    private List<Message> messageFriendList;

    private ListView listViewMyFriend;

    private MyFriendAdapter myFriendAdapter;

    private final String sDefaultPath = "data/user/0/com.example.user.drawinggame/app_";
    private final String sFriendPhotoPath = "friends_photo";

    public MyFriendFragment() {
        // Required empty public constructor
    }


    @SuppressLint("HardwareIds")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_friend, container, false);

        mPlayer = MainActivity.appDatabase.playerDao().getPlayerBySerialID(Build.SERIAL);

        messageFriendList = MainActivity.appDatabase.messageDao().getMessagesByType(2);

        for (Message msg : messageFriendList) {

            Player player = new Player(msg.getSenderID());
            SearchThread st = new SearchThread(player);
            st.start();

            while (!st.isDone) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.i("new friend", player.getUserName());
            Friend friend = new Friend(player);
            MainActivity.appDatabase.friendDao().addFriend(friend);
            MainActivity.appDatabase.messageDao().deleteMessage(msg);

            new UI.SaveFriendImageTask(getContext(), sFriendPhotoPath, String.valueOf(friend.getUserID())).execute(friend.getPicURL());

            // 新增訊息

        }


        List<Friend> friendList = MainActivity.appDatabase.friendDao().getAllFriends();
        Log.i("msg size", String.valueOf(messageFriendList.size()));


        List<Friend[]> myFriendsList = new ArrayList<>();
        for (int i = 0; i < friendList.size(); i += 3) {
            Friend[] friends = new Friend[3];
            try {
                friends[0] = friendList.get(i);
                friends[1] = friendList.get(i + 1);
                friends[2] = friendList.get(i + 2);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            myFriendsList.add(friends);
        }


        listViewMyFriend = view.findViewById(R.id.listViewMyFriend);
        myFriendAdapter = new MyFriendAdapter(getContext(), mPlayer, myFriendsList);
        listViewMyFriend.setAdapter(myFriendAdapter);
        listViewMyFriend.setOnItemClickListener(this);


        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
