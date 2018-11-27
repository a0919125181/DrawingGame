package com.example.user.drawinggame.Room;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.drawinggame.R;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessFragment extends Fragment {


    private TextView textViewTitle;


    public ProcessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_process, container, false);


        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);

        return view;
    }

    public TextView getTextViewTitle() {
        return textViewTitle;
    }


}
