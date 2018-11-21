package com.example.user.drawinggame.Lobby;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.connections.php.GetPictureThread;
import com.example.user.drawinggame.connections.php.SearchThread;
import com.example.user.drawinggame.connections.php.SendMsgThread;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment implements View.OnClickListener {

    private Player player;

    private RadioGroup radioGroup;
    private RadioButton radioButtonID;
    private RadioButton radioButtonName;

    private Button buttonSearch;
    private EditText editTextSearchID;
    private EditText editTextSearchName;

    private Player playerSearch;
    private ImageView imageViewFriend;


    public FriendFragment() {
        // Required empty public constructor
    }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        player = MainActivity.appDatabase.playerDao().getPlayerBySerialID(Build.SERIAL);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioButtonID = (RadioButton) view.findViewById(R.id.radioButtonID);
        radioButtonName = (RadioButton) view.findViewById(R.id.radioButtonName);
        buttonSearch = (Button) view.findViewById(R.id.buttonSearch);
        editTextSearchID = (EditText) view.findViewById(R.id.editTextSearchID);
        editTextSearchName = (EditText) view.findViewById(R.id.editTextSearchName);

        radioGroup.setOnCheckedChangeListener(onCheckedChangeListenerSearch());
        radioButtonID.setChecked(true);
        buttonSearch.setOnClickListener(this);



        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSearch:

                if (editTextSearchID.getVisibility() == View.VISIBLE && !editTextSearchID.getText().toString().equals("")) {
                    Log.e("search", "id");

                    int searchID = Integer.parseInt(String.valueOf(editTextSearchID.getText()));
                    playerSearch = new Player(searchID);
                    new SearchThread(playerSearch).start();

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    View viewFriendInfo = LayoutInflater.from(getActivity()).inflate(R.layout.show_friend_info, null);

                    TextView textViewID = viewFriendInfo.findViewById(R.id.textViewID);
                    textViewID.setText("ID: " + String.valueOf(playerSearch.getUserID()));

                    TextView textViewName = viewFriendInfo.findViewById(R.id.textViewName);
                    textViewName.setText("名字: " + playerSearch.getUserName());

                    TextView textViewIntro = viewFriendInfo.findViewById(R.id.textViewIntro);
                    textViewIntro.setText("自我介紹: " + playerSearch.getIntro());

                    TextView textViewAge = viewFriendInfo.findViewById(R.id.textViewAge);
                    textViewAge.setText("年紀: " + String.valueOf(playerSearch.getAge()));

                    TextView textViewGender = viewFriendInfo.findViewById(R.id.textViewGender);
                    textViewGender.setText(playerSearch.getGender() == 1 ? "性別: 男" : "性別: 女");

                    TextView textViewLevel = viewFriendInfo.findViewById(R.id.textViewLevel);
                    textViewLevel.setText("等級: " + String.valueOf(playerSearch.getLevel()));

                    Log.e("info", "uploaded");

                    imageViewFriend = viewFriendInfo.findViewById(R.id.imageViewFriend);
                    new GetPictureThread(playerSearch, FriendFragment.this, imageViewFriend).start();


                    if (playerSearch.getUserName() != null) {
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setView(viewFriendInfo)
                                .create();

                        Log.e("show", "dialog");
                        UI.showImmersiveModeDialog(dialog, true);

                        Button buttonAddFriend = viewFriendInfo.findViewById(R.id.buttonAddFriend);
                        buttonAddFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new SendMsgThread(player, "加好友", playerSearch.getUserID(), 1).start();
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
