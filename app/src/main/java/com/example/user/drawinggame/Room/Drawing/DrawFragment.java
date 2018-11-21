package com.example.user.drawinggame.Room.Drawing;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.Room.RoomFragment;
import com.example.user.drawinggame.database_classes.AppDatabase;
import com.example.user.drawinggame.database_classes.Player;

import java.net.Socket;

/**
 * A simple {@link Fragment} subclass.
 */
public class DrawFragment extends Fragment implements View.OnClickListener {


    // paint area
    private DrawView dv;
    private Paint paint;
    private Paint erase;
    private RelativeLayout relativeLayoutDraw;

    // paint control
    private ImageView imageViewPaint;
    private ImageView imageViewErase;
    private ImageView imageViewClear;
    private SeekBar seekBarBrush;


    private Socket socket;
    private Player player;

    public DrawFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public DrawFragment(Socket socket) {
        this.socket = socket;
        player = MainActivity.appDatabase.playerDao().getPlayerBySerialID(Build.SERIAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_draw, container, false);


        // 畫圖板
        relativeLayoutDraw = view.findViewById(R.id.relativeLayoutDraw);

        imageViewPaint = (ImageView) view.findViewById(R.id.imageViewPaint);
        imageViewPaint.setOnClickListener(this);

        imageViewErase = (ImageView) view.findViewById(R.id.imageViewErase);
        imageViewErase.setOnClickListener(this);

        imageViewClear = (ImageView) view.findViewById(R.id.imageViewClear);
        imageViewClear.setOnClickListener(this);

        seekBarBrush = (SeekBar) view.findViewById(R.id.seekBarBrush);
        seekBarBrush.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (imageViewErase.isEnabled()) {
                    imageViewPaint.callOnClick();
                } else if (imageViewPaint.isEnabled()) {
                    imageViewErase.callOnClick();
                }
            }
        });


        return view;
    }


    private void setPaint() {
        paint = new Paint();
        paint.setAntiAlias(true); //抗鋸齒
        paint.setDither(true); //防抖動
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE); //不填滿
        paint.setStrokeJoin(Paint.Join.ROUND); //圓角
        paint.setStrokeCap(Paint.Cap.ROUND); //單點形狀
        paint.setStrokeWidth(seekBarBrush.getProgress()); //粗細
    }

    private void setErase() {
        erase = new Paint();
        erase.setAntiAlias(true); //抗鋸齒
        erase.setDither(true); //防抖動
        erase.setColor(Color.rgb(250, 250, 250));
        erase.setStyle(Paint.Style.STROKE); //不填滿
        erase.setStrokeJoin(Paint.Join.ROUND); //圓角
        erase.setStrokeCap(Paint.Cap.ROUND); //單點形狀
        erase.setStrokeWidth(seekBarBrush.getProgress()); //粗細
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewPaint:
                setPaint();
                imageViewPaint.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_brush));
                imageViewPaint.setEnabled(false);
                imageViewErase.setBackgroundDrawable(null);
                imageViewErase.setEnabled(true);
                dv = new DrawView(relativeLayoutDraw.getContext(), paint, seekBarBrush.getProgress(), socket, String.valueOf(player.getUserID()));
                relativeLayoutDraw.addView(dv);
                break;
            case R.id.imageViewErase:
                setErase();
                imageViewErase.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_brush));
                imageViewErase.setEnabled(false);
                imageViewPaint.setBackgroundDrawable(null);
                imageViewPaint.setEnabled(true);
                dv = new DrawView(relativeLayoutDraw.getContext(), erase, seekBarBrush.getProgress(), socket, String.valueOf(player.getUserID()));
                relativeLayoutDraw.addView(dv);
                break;
            case R.id.imageViewClear:
                relativeLayoutDraw.removeAllViews();
                imageViewPaint.callOnClick();
                break;
        }
    }
}
