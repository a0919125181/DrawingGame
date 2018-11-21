package com.example.user.drawinggame.Room.Drawing.PaintAPP;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.user.drawinggame.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FingerDrawFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{


    // paint app
    private ImageView imageViewPaint;
    private ImageView imageViewErase;
    private ImageView imageViewClear;
    private SeekBar seekBar;
    private PaintView paintView;


    public FingerDrawFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finger_draw, container, false);



        // paint app
        imageViewPaint = (ImageView) view.findViewById(R.id.imageViewPaint);
        imageViewPaint.setOnClickListener(this);
        imageViewPaint.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_brush));
        imageViewErase = (ImageView) view.findViewById(R.id.imageViewErase);
        imageViewErase.setOnClickListener(this);
        imageViewClear = (ImageView) view.findViewById(R.id.imageViewClear);
        imageViewClear.setOnClickListener(this);
        seekBar = (SeekBar) view.findViewById(R.id.seekBarBrush);
        seekBar.setOnSeekBarChangeListener(this);
        paintView = (PaintView) view.findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);



        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewPaint:
                paintView.setPaintColor();
                imageViewPaint.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_brush));
                imageViewErase.setBackgroundDrawable(null);
                break;
            case R.id.imageViewErase:
                paintView.erase();
                imageViewErase.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_brush));
                imageViewPaint.setBackgroundDrawable(null);
                break;
            case R.id.imageViewClear:
                paintView.clear();
                break;
        }

    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        paintView.changeBrushStroke(seekBar.getProgress());
    }
}
