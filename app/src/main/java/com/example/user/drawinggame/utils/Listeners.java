package com.example.user.drawinggame.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.drawinggame.R;
import com.example.user.drawinggame.connections.php.SendMsgThread;
import com.example.user.drawinggame.database_classes.Player;

public class Listeners {

    public static View.OnClickListener textViewPickerGenderListener(final Context context, final TextView textViewPickerGender) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewSelectGender = LayoutInflater.from(context).inflate(R.layout.select_gender, null);
                final AlertDialog alertDialogSelectGender = new AlertDialog.Builder(context)
                        .setTitle("請選擇您的性別")
                        .setView(viewSelectGender)
                        .create();

                Button buttonBoy = (Button) viewSelectGender.findViewById(R.id.buttonBoy);
                buttonBoy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewPickerGender.setText("男");
                        alertDialogSelectGender.dismiss();
                    }
                });
                Button buttonGirl = (Button) viewSelectGender.findViewById(R.id.buttonGirl);
                buttonGirl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewPickerGender.setText("女");
                        alertDialogSelectGender.dismiss();
                    }
                });
                UI.showImmersiveModeDialog(alertDialogSelectGender, true);
            }
        };
    }

    public static View.OnClickListener textViewPickerAgeListener(final Context context, final TextView textViewPickerAge) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View viewAgePicker = LayoutInflater.from(context).inflate(R.layout.age_picker, null);
                final NumberPicker numberPicker = (NumberPicker) viewAgePicker.findViewById(R.id.numberPickerAge);
                Button buttonCancel = (Button) viewAgePicker.findViewById(R.id.buttonCancel);
                Button buttonConfirm = (Button) viewAgePicker.findViewById(R.id.buttonConfirm);

                final AlertDialog alertDialogAgePicker = new AlertDialog.Builder(context)
                        .setView(viewAgePicker)
                        .create();

                numberPicker.setMaxValue(100);
                numberPicker.setMinValue(0);
                numberPicker.setValue(Integer.parseInt(textViewPickerAge.getText().toString()));
                numberPicker.setWrapSelectorWheel(false);

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogAgePicker.dismiss();
                    }
                });

                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewPickerAge.setText(String.valueOf(numberPicker.getValue()));
                        alertDialogAgePicker.dismiss();
                    }
                });

                UI.showImmersiveModeDialog(alertDialogAgePicker, false);


                alertDialogAgePicker.getWindow().setLayout(UI.width / 5 * 3, UI.height / 5 * 3);
            }
        };
    }

    public static View.OnClickListener addFriendListener(final Context context, final Player player, final Player playerSearch) {
        return new View.OnClickListener() {
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
                        Toast.makeText(context, "交友邀請已送出", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "邀請失敗", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        };
    }

}
