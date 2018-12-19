package com.example.user.drawinggame.Lobby.Friend;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.connections.php.FriendGetPictureThread;
import com.example.user.drawinggame.connections.php.GetMyFriend;
import com.example.user.drawinggame.connections.php.SearchThread;
import com.example.user.drawinggame.connections.php.SendMsgThread;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment implements View.OnClickListener {

    private FragmentManager fragmentManagerFriend;

    private MyFriendFragment myFriendFragment;
    private FriendInviteFragment friendInviteFragment;
    private List<Fragment> fragmentList = new ArrayList<>();

    private Player player;

    private RadioGroup radioGroup;
    private RadioButton radioButtonID;
    private RadioButton radioButtonName;

    private Button buttonSearch;
    private EditText editTextSearchID;
    private EditText editTextSearchName;

    private Player playerSearch;


    private String picURL;

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public FriendFragment() {
        // Required empty public constructor
    }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        player = MainActivity.appDatabase.playerDao().getPlayerBySerialID(Build.SERIAL);

        // 更新朋友資料
        new GetMyFriend(getContext(), player.getUserID()).start();

        fragmentManagerFriend = getFragmentManager();
        myFriendFragment = new MyFriendFragment();
        fragmentList.add(myFriendFragment);
        friendInviteFragment = new FriendInviteFragment(player);
        fragmentList.add(friendInviteFragment);


        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioButtonID = (RadioButton) view.findViewById(R.id.radioButtonID);
        radioButtonName = (RadioButton) view.findViewById(R.id.radioButtonName);
        buttonSearch = (Button) view.findViewById(R.id.buttonSearch);
        editTextSearchID = (EditText) view.findViewById(R.id.editTextSearchID);
        editTextSearchName = (EditText) view.findViewById(R.id.editTextSearchName);

        radioGroup.setOnCheckedChangeListener(onCheckedChangeListenerSearch());
        radioButtonID.setChecked(true);
        buttonSearch.setOnClickListener(this);


        Button buttonMyFriend = (Button) view.findViewById(R.id.buttonMyFriend);
        buttonMyFriend.setOnClickListener(this);

        Button buttonFriendInvite = (Button) view.findViewById(R.id.buttonFriendInvite);
        buttonFriendInvite.setOnClickListener(this);

        return view;
    }

    private void rightFragmentSwitcher(Fragment fragmentClicked) {
        if (fragmentClicked.isVisible()) {
            fragmentManagerFriend.beginTransaction().remove(fragmentClicked).commit();
        } else {

            for (Fragment f : fragmentList) {
                fragmentManagerFriend.beginTransaction().remove(f).commit();
            }
            fragmentManagerFriend.beginTransaction()
                    .replace(R.id.friend_container, fragmentClicked)
                    .commit();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonMyFriend:
                rightFragmentSwitcher(myFriendFragment);
                break;

            case R.id.buttonFriendInvite:
                rightFragmentSwitcher(friendInviteFragment);
                break;

            case R.id.buttonSearch:

                if (editTextSearchID.getVisibility() == View.VISIBLE && !editTextSearchID.getText().toString().equals("")) {
                    Log.e("search", "id");

                    int searchID = Integer.parseInt(String.valueOf(editTextSearchID.getText()));
                    playerSearch = new Player(searchID);
                    new SearchThread(playerSearch).start();
                    new FriendGetPictureThread(playerSearch, FriendFragment.this).start();

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    View viewFriendInfo = LayoutInflater.from(getActivity()).inflate(R.layout.show_friend_info, null);

                    final ImageView imageViewFriend = viewFriendInfo.findViewById(R.id.imageViewFriend);
                    TextView textViewID = viewFriendInfo.findViewById(R.id.textViewID);
                    TextView textViewName = viewFriendInfo.findViewById(R.id.textViewName);
                    TextView textViewIntro = viewFriendInfo.findViewById(R.id.textViewIntro);
                    TextView textViewAge = viewFriendInfo.findViewById(R.id.textViewAge);
                    TextView textViewGender = viewFriendInfo.findViewById(R.id.textViewGender);
                    TextView textViewLevel = viewFriendInfo.findViewById(R.id.textViewLevel);

                    textViewID.setText("ID: " + String.valueOf(playerSearch.getUserID()));
                    textViewName.setText("名字: " + playerSearch.getUserName());
                    textViewIntro.setText("自我介紹: " + playerSearch.getIntro());
                    textViewAge.setText("年紀: " + String.valueOf(playerSearch.getAge()));
                    textViewGender.setText(playerSearch.getGender() == 1 ? "性別: 男" : "性別: 女");
                    textViewLevel.setText("等級: " + String.valueOf(playerSearch.getLevel()));
                    Log.e("info", "uploaded");

                    if (picURL != null) {
                        new UI.DownloadImageTask(imageViewFriend).execute(picURL);
                    }


                    if (playerSearch.getUserName() != null) {
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setView(viewFriendInfo)
                                .create();

                        Log.e("show", "dialog");
                        UI.showImmersiveModeDialog(dialog, true);

                        ImageView imageViewAddFriend = viewFriendInfo.findViewById(R.id.imageViewAddFriend);
                        imageViewAddFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SendMsgThread smt = new SendMsgThread(player, "加好友", playerSearch.getUserID(), 1);
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

                        editTextSearchID.setText("");
                    } else {
                        Toast.makeText(getContext(), "請重新輸入正確ID", Toast.LENGTH_SHORT).show();
                    }


                } else if (editTextSearchName.getVisibility() == View.VISIBLE && !editTextSearchName.getText().toString().equals("")) {
                    Log.e("search", "name");
                    editTextSearchName.setText("");

                    Toast.makeText(getContext(), "還沒做好", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListenerSearch() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioButtonID:
                        editTextSearchID.setVisibility(View.VISIBLE);
                        editTextSearchName.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioButtonName:
                        editTextSearchName.setVisibility(View.VISIBLE);
                        editTextSearchID.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };
    }


}
