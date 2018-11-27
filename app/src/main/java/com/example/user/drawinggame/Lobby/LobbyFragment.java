package com.example.user.drawinggame.Lobby;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.drawinggame.Lobby.Friend.FriendFragment;
import com.example.user.drawinggame.Lobby.Message.MessageFragment;
import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.Room.RoomFragment;
import com.example.user.drawinggame.connections.php.EditThread;
import com.example.user.drawinggame.connections.TCP.EnterRoomThread;
import com.example.user.drawinggame.connections.php.GetMsgThread;
import com.example.user.drawinggame.connections.php.LobbyGetPictureThread;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.Listeners;
import com.example.user.drawinggame.utils.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class LobbyFragment extends Fragment implements View.OnClickListener {

    private FragmentManager fragmentManagerLobby;

    private Player player;

    // 右上
    private ImageView imageViewPhoto;
    private TextView textViewName;
    private ProgressBar progressBarExp;
    private TextView textViewLevel;
    private TextView textViewMoney;

    // 中間
    private Button buttonCreateRoom;
    private Button buttonEnterRoom;

    // 左邊
    private Button buttonMessage;
    private Button buttonFriend;
    private Button buttonBag;
    private Button buttonInfo;
    private Button buttonSetting;

    // 個人資料
    private TextView textViewID;
    private Button buttonEdit;
    private Button buttonDone;
    private TextView textViewPlayerName;
    private EditText editTextName;
    private TextView textViewPlayerGender;
    private TextView textViewPlayerAge;
    private TextView textViewPlayerIntro;

    private ImageView infoPhoto;
    private File tempFile;
    private String picURL;

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    // edit intro xml
    private EditText editTextIntro;
    private Button buttonFinish;


    //
    private Fragment fragmentMessage = new MessageFragment();
    private Fragment fragmentFriend = new FriendFragment();
    private Fragment fragmentBag = new BagFragment();
    private Fragment fragmentInfo = new InfoFragment();
    private Fragment fragmentSetting = new SettingFragment();
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();


    public LobbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lobby, container, false);

        fragmentManagerLobby = getFragmentManager();


        fragmentList.add(fragmentMessage);
        fragmentList.add(fragmentFriend);
        fragmentList.add(fragmentBag);
        fragmentList.add(fragmentInfo);
        fragmentList.add(fragmentSetting);

        // 獲取資料
        player = MainActivity.appDatabase.playerDao().getPlayerBySerialID(Build.SERIAL);
        this.tempFile = new File("/sdcard/a.jpg"); // 這句一定要在onCreate()里面調用

        // 右上
        imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        imageViewPhoto.setOnClickListener(imageViewPhotoListener());

        new LobbyGetPictureThread(player, LobbyFragment.this).start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new UI.DownloadImageTask(imageViewPhoto).execute(getPicURL());

        textViewName = view.findViewById(R.id.textViewName);
        textViewName.setText(player.getUserName());

        progressBarExp = view.findViewById(R.id.progressBarExp);
        progressBarExp.setProgress(player.getExp());

        textViewLevel = view.findViewById(R.id.textViewLevel);
        textViewLevel.setText(String.valueOf(player.getLevel()));

        textViewMoney = view.findViewById(R.id.textViewMoney);
        textViewMoney.setText(String.valueOf(player.getMoney()));

        // 中間

        buttonCreateRoom = view.findViewById(R.id.buttonCreateRoom);
        buttonCreateRoom.setOnClickListener(buttonCreateRoomListener());

        buttonEnterRoom = view.findViewById(R.id.buttonEnterRoom);
        buttonEnterRoom.setOnClickListener(buttonEnterRoomListener());


        // 左邊
        buttonMessage = view.findViewById(R.id.buttonMessage);
        buttonMessage.setOnClickListener(this);

        buttonFriend = view.findViewById(R.id.buttonFriend);
        buttonFriend.setOnClickListener(this);

        buttonBag = view.findViewById(R.id.buttonBag);
        buttonBag.setOnClickListener(this);

        buttonInfo = view.findViewById(R.id.buttonInfo);
        buttonInfo.setOnClickListener(this);

        buttonSetting = view.findViewById(R.id.buttonSetting);
        buttonSetting.setOnClickListener(this);


        // 接收訊息
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    new GetMsgThread(player).start();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new UI.DownloadImageTask(imageViewPhoto).execute(getPicURL());
    }

    private View.OnClickListener imageViewPhotoListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewShowInfo = LayoutInflater.from(getActivity()).inflate(R.layout.show_player_info, null);

                // 取得圖片控制項ImageView
                infoPhoto = (ImageView) viewShowInfo.findViewById(R.id.infoPhoto);
                infoPhoto.setEnabled(false);
                infoPhoto.setOnClickListener(LobbyFragment.this);
                new LobbyGetPictureThread(player, LobbyFragment.this).start();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                new UI.DownloadImageTask(infoPhoto).execute(getPicURL());

                textViewID = (TextView) viewShowInfo.findViewById(R.id.textViewID);
                textViewID.setText("ID: " + player.getUserID());

                // 個人資料編輯按鈕
                buttonEdit = (Button) viewShowInfo.findViewById(R.id.buttonEdit);
                buttonEdit.setOnClickListener(LobbyFragment.this);

                // 個人資料完成按鈕
                buttonDone = (Button) viewShowInfo.findViewById(R.id.buttonDone);
                buttonDone.setOnClickListener(LobbyFragment.this);

                textViewPlayerName = (TextView) viewShowInfo.findViewById(R.id.textViewPlayerName);
                textViewPlayerName.setText(player.getUserName());

                editTextName = (EditText) viewShowInfo.findViewById(R.id.editTextName);
                editTextName.setText(player.getUserName());

                textViewPlayerGender = (TextView) viewShowInfo.findViewById(R.id.textViewPlayerGender);
                textViewPlayerGender.setOnClickListener(Listeners.textViewPickerGenderListener(getActivity(), textViewPlayerGender));
                if (player.getGender() == 1) {
                    textViewPlayerGender.setText("男");
                } else if (player.getGender() == 0) {
                    textViewPlayerGender.setText("女");
                }

                textViewPlayerAge = (TextView) viewShowInfo.findViewById(R.id.textViewPlayerAge);
                textViewPlayerAge.setOnClickListener(Listeners.textViewPickerAgeListener(getActivity(), textViewPlayerAge));
                textViewPlayerAge.setText(String.valueOf(player.getAge()));

                textViewPlayerIntro = (TextView) viewShowInfo.findViewById(R.id.textViewPlayerIntro);
                textViewPlayerIntro.setOnClickListener(LobbyFragment.this);
                textViewPlayerIntro.setText(player.getIntro());

                AlertDialog alertDialogShowInfo = new AlertDialog.Builder(getActivity())
                        .setTitle("個人資料")
                        .setView(viewShowInfo)
                        .create();


                Log.e("show", "dialog");
                UI.showImmersiveModeDialog(alertDialogShowInfo, true);
            }
        };
    }


    private View.OnClickListener buttonCreateRoomListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View viewCreateRoom = LayoutInflater.from(getContext()).inflate(R.layout.create_room, null);
                final AlertDialog alertDialogCreateRoom = new AlertDialog.Builder(getContext())
                        .setView(viewCreateRoom)
                        .create();

                Button buttonCreate = viewCreateRoom.findViewById(R.id.buttonCreate);
                buttonCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogCreateRoom.dismiss();
                        // create room connect
//                        UI.fragmentSwitcher(new RoomFragment(), false);
                    }
                });


                UI.showImmersiveModeDialog(alertDialogCreateRoom, true);
                alertDialogCreateRoom.getWindow().setLayout(UI.width / 2, UI.height / 3 * 2);
            }
        };
    }


    private View.OnClickListener buttonEnterRoomListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoomFragment roomFragment = new RoomFragment();
                new EnterRoomThread(player.getUserID(), roomFragment).start();
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.infoPhoto:


                /**
                 // 自選圖片
                 Intent intent = new Intent(Intent.ACTION_PICK);
                 // 開啟Pictures畫面Type設定為image
                 intent.setType("image/*");
                 // 使用Intent.ACTION_GET_CONTENT這個Action
                 // 會開啟選取圖檔視窗讓您選取手機內圖檔
                 intent.setAction(Intent.ACTION_GET_CONTENT);
                 intent.putExtra("crop", "true");                // crop = true 有這句才能叫出裁剪頁面.
                 intent.putExtra("aspectX", 1);                  // 这兩項為裁剪框的比例.
                 intent.putExtra("aspectY", 1);                  // x:y=1:1
                 intent.putExtra("output", Uri.fromFile(tempFile));
                 intent.putExtra("outputFormat", "JPEG");        // 返回格式

                 // 取得相片後返回本畫面
                 startActivityForResult(Intent.createChooser(intent, "選擇圖片"), 1);
                 */

                break;

            case R.id.buttonEdit:
                buttonDone.setVisibility(View.VISIBLE);
                infoPhoto.setEnabled(true);
                textViewPlayerName.setVisibility(View.INVISIBLE);
                editTextName.setVisibility(View.VISIBLE);
                textViewPlayerGender.setEnabled(true);
                textViewPlayerAge.setEnabled(true);
                textViewPlayerIntro.setEnabled(true);
                break;

            case R.id.buttonDone:
                infoPhoto.setEnabled(false);
                buttonDone.setVisibility(View.INVISIBLE);
                textViewPlayerName.setText(editTextName.getText());
                textViewPlayerName.setVisibility(View.VISIBLE);
                editTextName.setVisibility(View.INVISIBLE);
                textViewPlayerGender.setEnabled(false);
                textViewPlayerAge.setEnabled(false);
                textViewPlayerIntro.setEnabled(false);

                player.setUserName(String.valueOf(editTextName.getText()));
                if (String.valueOf(textViewPlayerGender.getText()).equals("男")) {
                    player.setGender(1);
                } else if (String.valueOf(textViewPlayerGender.getText()).equals("女")) {
                    player.setGender(0);
                }
                player.setAge(Integer.parseInt(String.valueOf(textViewPlayerAge.getText())));
                player.setIntro(String.valueOf(textViewPlayerIntro.getText()));
                textViewName.setText(player.getUserName());

                new EditThread(player).start();
                break;

            case R.id.textViewPlayerIntro:
                final View viewIntro = LayoutInflater.from(getActivity()).inflate(R.layout.edit_intro, null);
                final AlertDialog alertDialogEditIntro = new AlertDialog.Builder(getActivity())
                        .setTitle("修改個人資料")
                        .setView(viewIntro)
                        .create();

                editTextIntro = viewIntro.findViewById(R.id.editTextIntro);
                editTextIntro.setText(textViewPlayerIntro.getText());

                buttonFinish = viewIntro.findViewById(R.id.buttonFinish);
                buttonFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String intro = String.valueOf(editTextIntro.getText());
                        textViewPlayerIntro.setText(intro);
                        alertDialogEditIntro.dismiss();
                    }
                });

                Log.e("show", "dialog");
                UI.showImmersiveModeDialog(alertDialogEditIntro, true);
                break;

            case R.id.buttonMessage:
                showFragment(fragmentMessage);
                break;

            case R.id.buttonFriend:
                showFragment(fragmentFriend);
                break;

            case R.id.buttonBag:
                showFragment(fragmentBag);
                break;

            case R.id.buttonInfo:
                showFragment(fragmentInfo);
                break;

            case R.id.buttonSetting:
                showFragment(fragmentSetting);
                break;

        }
    }


    // 滑動主畫面
    private void showFragment(Fragment fragmentClicked) {
        // https://www.codexpedia.com/android/android-slide-animations/
        // https://stackoverflow.com/questions/8876126/swap-fragment-in-an-activity-via-animation
        if (fragmentClicked.isVisible()) {
            buttonCreateRoom.setVisibility(View.VISIBLE);
            buttonEnterRoom.setVisibility(View.VISIBLE);
            fragmentManagerLobby.beginTransaction().remove(fragmentClicked).commit();
        } else {
            for (Fragment f : fragmentList) {
                fragmentManagerLobby.beginTransaction().remove(f).commit();
            }
            fragmentManagerLobby.beginTransaction()
                    .setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_bottom)
                    .replace(R.id.frameLayoutSlide, fragmentClicked)
                    .commit();

            buttonCreateRoom.setVisibility(View.INVISIBLE);
            buttonEnterRoom.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //當使用者按下確定後
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                Log.e("imageUri", String.valueOf(imageUri));
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                // 設定到ImageView
                infoPhoto.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }


}
