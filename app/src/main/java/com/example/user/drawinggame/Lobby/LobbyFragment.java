package com.example.user.drawinggame.Lobby;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
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
import com.example.user.drawinggame.connections.TCP.EnterRoomThread;
import com.example.user.drawinggame.connections.php.EditThread;
import com.example.user.drawinggame.connections.php.GetMsgThread;
import com.example.user.drawinggame.connections.php.LobbyGetPictureThread;
import com.example.user.drawinggame.connections.php.LoginThread;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.Listeners;
import com.example.user.drawinggame.utils.UI;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static java.lang.Thread.sleep;


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
    private boolean enter = true;

    public boolean isEnter() {
        return enter;
    }

    // 左邊
    private ImageView imageViewMessage;
    private ImageView imageViewFriend;
    private ImageView imageViewBag;
    private ImageView imageViewInfo;
    private ImageView imageViewSetting;
    private List<ImageView> leftImageViewList = new ArrayList<>();

    // 個人資料
    private TextView textViewID;
    private Button buttonEdit;
    private Button buttonDone;
    private TextView textViewPlayerName;
    private EditText editTextName;
    private TextView textViewPlayerGender;
    private TextView textViewPlayerAge;
    private TextView textViewPlayerIntro;

    private String mProfileImagePath = "data/user/0/com.example.user.drawinggame/app_imageDir";
    private String picURL;

    private ImageView infoPhoto;

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getPicURL() {
        return picURL;
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

        // 更新資料
        LoginThread lt = new LoginThread(player);
        lt.start();

        while (!lt.isDone()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


//        this.tempFile = new File("/sdcard/a.jpg"); // 這句一定要在onCreate()里面調用


        // 右上
        imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        imageViewPhoto.setOnClickListener(imageViewPhotoListener());

        new LobbyGetPictureThread(player, LobbyFragment.this).start();

        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try { //內存找不到 就從PHP撈
            new UI().loadImageFromStorage(imageViewPhoto, mProfileImagePath, "profilePhoto");
            Log.e("Get from", "storage");
        } catch (FileNotFoundException e) {
            Log.e("Get from", "PHP");
            new LobbyGetPictureThread(player, LobbyFragment.this).start();
            new UI.SaveImageTask(getContext(), imageViewPhoto, mProfileImagePath, "myPhoto").execute(getPicURL());
            try {
                new UI().loadImageFromStorage(imageViewPhoto, mProfileImagePath, "myPhoto");
            } catch (FileNotFoundException ee) {
                Log.e("Error", "都找不到照片");
            }
        }

        textViewName = view.findViewById(R.id.textViewName);
        textViewName.setText(player.getUserName());

        progressBarExp = view.findViewById(R.id.progressBarExp);
        // 算等級經驗
        calLvExp();

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
        imageViewMessage = view.findViewById(R.id.imageViewMessage);
        imageViewMessage.setOnClickListener(this);
        leftImageViewList.add(imageViewMessage);

        imageViewFriend = view.findViewById(R.id.imageViewFriend);
        imageViewFriend.setOnClickListener(this);
        leftImageViewList.add(imageViewFriend);

        imageViewBag = view.findViewById(R.id.imageViewBag);
        imageViewBag.setOnClickListener(this);
        leftImageViewList.add(imageViewBag);

        imageViewInfo = view.findViewById(R.id.imageViewInfo);
        imageViewInfo.setOnClickListener(this);
        leftImageViewList.add(imageViewInfo);

        imageViewSetting = view.findViewById(R.id.imageViewSetting);
        imageViewSetting.setOnClickListener(this);
        leftImageViewList.add(imageViewSetting);


        // 接收訊息
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    new GetMsgThread(player).start();
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        return view;
    }


    // 算等級經驗
    private void calLvExp() {
        int mLv = player.getLevel();
        int mExp = player.getExp();

        Log.e("my Lv: " + mLv, "my exp: " + mExp);

        for (int i = 1; i < 11; i++) {
            int maxExp = (int) (Math.pow((i - 1), (1.33)) + 20);
            Log.e("Lv: " + i, "max exp: " + (maxExp));
        }

        int maxExp = (int) (Math.pow((mLv - 1), (1.33)) + 20);
        int level = (int) Math.pow((mExp - 20), (3.0 / 5.0)) + 1;

        Log.e("my Lv: " + mLv, "max exp: " + maxExp);

        Log.e("progress", String.valueOf((100 * mExp / maxExp)));
        progressBarExp.setProgress((100 * mExp / maxExp));
    }

    @Override
    public void onResume() {
        super.onResume();

        try { //內存找不到 就從PHP撈
            new UI().loadImageFromStorage(imageViewPhoto, mProfileImagePath, "profilePhoto");
            Log.e("Get from", "storage");
        } catch (FileNotFoundException e) {
            Log.e("Get from", "PHP");
            new LobbyGetPictureThread(player, LobbyFragment.this).start();
            new UI.SaveImageTask(getContext(), imageViewPhoto, mProfileImagePath, "myPhoto").execute(getPicURL());
            try {
                new UI().loadImageFromStorage(imageViewPhoto, mProfileImagePath, "myPhoto");
            } catch (FileNotFoundException ee) {
                Log.e("Error", "都找不到照片");
            }
        }
    }

    private View.OnClickListener imageViewPhotoListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewShowInfo = LayoutInflater.from(getActivity()).inflate(R.layout.show_player_info, null);

                // 取得圖片控制項ImageView
                infoPhoto = viewShowInfo.findViewById(R.id.infoPhoto);
                infoPhoto.setEnabled(false);
                infoPhoto.setOnClickListener(LobbyFragment.this);

                try { //內存找不到 就從PHP撈
                    new UI().loadImageFromStorage(infoPhoto, mProfileImagePath, "profilePhoto");
                    Log.e("Get from", "storage");
                } catch (FileNotFoundException e) {
                    Log.e("Get from", "PHP");
                    new LobbyGetPictureThread(player, LobbyFragment.this).start();
                    new UI.SaveImageTask(getContext(), infoPhoto, mProfileImagePath, "myPhoto").execute(getPicURL());
                    new UI.SaveImageTask(getContext(), imageViewPhoto, mProfileImagePath, "myPhoto").execute(getPicURL());
                    try {
                        new UI().loadImageFromStorage(infoPhoto, mProfileImagePath, "myPhoto");
                    } catch (FileNotFoundException ee) {
                        Log.e("Error", "都找不到照片");
                    }
                }
//                new LobbyGetPictureThread(player, LobbyFragment.this).start();
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                new UI.DownloadImageTask(infoPhoto).execute(getPicURL());
//                UI.loadImageFromStorage(infoPhoto, mImagePath, "myPhoto");

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
                        //create room connect
                        //UI.fragmentSwitcher(new RoomFragment(), false);
                    }
                });

                UI.showImmersiveModeDialog(alertDialogCreateRoom, true);
                alertDialogCreateRoom.getWindow().setLayout(UI.width / 2, UI.height / 3 * 2);

            }
        };
    }


    private View.OnClickListener buttonEnterRoomListener() {
        return new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                RoomFragment roomFragment = new RoomFragment();

                if (enter) {
                    new EnterRoomThread(player.getUserID(), roomFragment).start();
                }
                enter = false;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        enter = false;
                    }

                }).start();
            }
        };
    }

    private void updateProfilePhoto(ImageView imageView) {
        //  內存找不到 就從PHP撈
        try {
            UI.loadImageFromStorage(imageView, mProfileImagePath, "profilePhoto");
        } catch (FileNotFoundException e) {
            getPicFromPHPServer(imageView);
        }
    }

    private void getPicFromPHPServer(ImageView imageView) {
        LobbyGetPictureThread lgpt = new LobbyGetPictureThread(player, LobbyFragment.this);
        lgpt.start();

        while (!lgpt.isDone()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new UI.SaveImageTask(getContext(), imageView, mProfileImagePath, "profilePhoto").execute(picURL);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.infoPhoto:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK); // 自選圖片
                pickPhoto.setType("image/*"); // 開啟Pictures畫面Type設定為image
                pickPhoto.putExtra("crop", "true"); // 叫出裁剪頁面.
                pickPhoto.putExtra("aspectX", 1);
                pickPhoto.putExtra("aspectY", 1); // x:y=1:1
                pickPhoto.putExtra("outputFormat", "PNG"); // 返回格式
//                pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(pickPhoto, 1);

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

                Bitmap bm = ((BitmapDrawable) infoPhoto.getDrawable()).getBitmap();
                mProfileImagePath = new UI().saveToInternalStorage(bm, this.getContext(), "profilePhoto");

                Profile profile = new Profile(String.valueOf(player.getUserID())); // 建profile物件
                ByteArrayOutputStream stream = new ByteArrayOutputStream(); //三行將bitmap轉byte[]
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                profile.setPhoto(imageInByte);
                new SendProfilePhoto(profile).start(); // 傳送

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try { //內存找不到 就從PHP撈
                    new UI().loadImageFromStorage(infoPhoto, mProfileImagePath, "profilePhoto");
                    new UI().loadImageFromStorage(imageViewPhoto, mProfileImagePath, "profilePhoto");
                    Log.e("Get from", "storage");
                } catch (FileNotFoundException e) {
                    Log.e("Get from", "PHP");
                    new LobbyGetPictureThread(player, LobbyFragment.this).start();
                    new UI.SaveImageTask(getContext(), infoPhoto, mProfileImagePath, "myPhoto").execute(getPicURL());
                    new UI.SaveImageTask(getContext(), imageViewPhoto, mProfileImagePath, "myPhoto").execute(getPicURL());
                    try {
                        new UI().loadImageFromStorage(infoPhoto, mProfileImagePath, "myPhoto");
                    } catch (FileNotFoundException ee) {
                        Log.e("Error", "都找不到照片");
                    }
                }

//                Log.e("path", mProfileImagePath);
//                new UI.SaveImageTask(getContext(), imageViewPhoto, mImagePath, "myPhoto").execute(getPicURL());
//                FileOutputStream outStream = openFileOutput("photo.jpg", Context.MODE_PRIVATE);
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

            case R.id.imageViewMessage:
                setLeftImageViewListBackground(imageViewMessage);
                imageViewMessage.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pf_green));
                showFragment(fragmentMessage);
                break;

            case R.id.imageViewFriend:
                setLeftImageViewListBackground(imageViewFriend);
                imageViewFriend.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pf_green));
                showFragment(fragmentFriend);
                break;

            case R.id.imageViewBag:
                setLeftImageViewListBackground(imageViewBag);
                imageViewBag.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pf_green));
                showFragment(fragmentBag);
                break;

            case R.id.imageViewInfo:
                setLeftImageViewListBackground(imageViewInfo);
                imageViewInfo.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pf_green));
                showFragment(fragmentInfo);
                break;

            case R.id.imageViewSetting:
                setLeftImageViewListBackground(imageViewSetting);
                showFragment(fragmentSetting);
                break;

        }
    }

    private void setLeftImageViewListBackground(ImageView imageViewClicked) {
        for (ImageView iv : leftImageViewList) {
            iv.setBackgroundResource(0);
        }
        imageViewClicked.setBackgroundResource(R.drawable.bg_pf_green);
    }


    // 滑動主畫面
    private void showFragment(Fragment fragmentClicked) {
        // https://www.codexpedia.com/android/android-slide-animations/
        // https://stackoverflow.com/questions/8876126/swap-fragment-in-an-activity-via-animation
        if (fragmentClicked.isVisible()) {
            buttonCreateRoom.setVisibility(View.VISIBLE);
            buttonEnterRoom.setVisibility(View.VISIBLE);
            fragmentManagerLobby.beginTransaction().remove(fragmentClicked).commit();
            for (ImageView iv : leftImageViewList) {
                iv.setBackgroundResource(0);
            }
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
        if (resultCode == RESULT_OK) { //當使用者按下確定
            final Bitmap selectedImage = data.getExtras().getParcelable("data");
            infoPhoto.setImageBitmap(selectedImage);// 設定到ImageView
//            try {
//                Uri imageUri = data.getData();
//                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
//                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                infoPhoto.setImageBitmap(selectedImag、返回e);// 設定到ImageView
//            } catch (FileNotFoundException e) {
//                Toast.makeText(getContext(), "找不到檔案", Toast.LENGTH_LONG).show();
//            }
        } else { //當使用者按下取消
            Toast.makeText(getContext(), "沒有選擇照片", Toast.LENGTH_LONG).show();
        }
    }


}
