package com.example.user.drawinggame.Login;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.drawinggame.Lobby.LobbyFragment;
import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.connections.php.LoginThread;
import com.example.user.drawinggame.connections.php.RegisterThread;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.Listeners;
import com.example.user.drawinggame.utils.UI;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private String serialID;
    private Player player;

    private Button buttonRegister;
    private Button buttonLogin;

    private EditText editTextName;
    private String name;
    private TextView textViewPickerGender;
    private int gender;
    private TextView textViewPickerAge;
    private int age;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // get serial id for account
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            serialID = Build.SERIAL;
        }
        player = new Player();
        player.setAccount(serialID);

        buttonRegister = view.findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(buttonRegisterListener());

        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(buttonLoginListener());

        if (MainActivity.appDatabase.playerDao().getPlayerBySerialID(serialID) != null) {
            buttonRegister.setVisibility(View.INVISIBLE);
        } else {
            buttonLogin.setVisibility(View.INVISIBLE);
        }

//        LoginThread lt = new LoginThread(player);
//        lt.start();
//
//        while (!lt.isDone()) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        UI.fragmentSwitcher(new LobbyFragment(), false);

        return view;
    }

    private View.OnClickListener buttonRegisterListener() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final View viewRegister = LayoutInflater.from(getActivity()).inflate(R.layout.register_table, null);

                // 輸入名字
                editTextName = (EditText) viewRegister.findViewById(R.id.editTextName);

                // 選擇性別
                textViewPickerGender = (TextView) viewRegister.findViewById(R.id.textViewPickerGender);
                textViewPickerGender.setOnClickListener(Listeners.textViewPickerGenderListener(getActivity(), textViewPickerGender));

                // 選擇年齡
                textViewPickerAge = (TextView) viewRegister.findViewById(R.id.textViewPickerAge);
                textViewPickerAge.setOnClickListener(Listeners.textViewPickerAgeListener(getActivity(), textViewPickerAge));

                AlertDialog alertDialogRegister = new AlertDialog.Builder(getActivity())
                        .setTitle("請輸入個人資料")
                        .setView(viewRegister)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // 輸入正確
                                if (editTextName.getText().toString().trim().isEmpty()) {
                                    Toast.makeText(getActivity(), "請重新輸入名字", Toast.LENGTH_SHORT).show();
                                } else {

                                    // 設定名字
                                    name = editTextName.getText().toString();
                                    player.setUserName(name);

                                    // 設定性別
                                    if (textViewPickerGender.getText().toString().equals("女")) {
                                        gender = 0;
                                    } else {
                                        gender = 1;
                                    }
                                    player.setGender(gender);

                                    // 設定年齡
                                    age = Integer.parseInt(textViewPickerAge.getText().toString());
                                    player.setAge(age);

                                    // 傳資料
                                    new RegisterThread(player).start();
                                }
                            }
                        })
                        .create();
                UI.showImmersiveModeDialog(alertDialogRegister, true);
            }
        };
    }

    private View.OnClickListener buttonLoginListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.appDatabase.playerDao().getPlayerBySerialID(serialID) != null) {
                    Log.e("check db", "account already exists");
                    player = MainActivity.appDatabase.playerDao().getPlayerBySerialID(serialID);

                    // do login send account(serialID)
                    LoginThread lt = new LoginThread(player);
                    lt.start();

                    while (!lt.isDone()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    UI.fragmentSwitcher(new LobbyFragment(), false);
                } else {
                    Toast.makeText(getContext(), "請先註冊帳號", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

}
