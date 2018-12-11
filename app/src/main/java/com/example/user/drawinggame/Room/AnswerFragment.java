package com.example.user.drawinggame.Room;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.drawinggame.R;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnswerFragment extends Fragment {

    private RoomFragment fragment;
    private EditText editTextAnswer;

    public EditText getEditTextAnswer() {
        return editTextAnswer;
    }

    private Button buttonSubmit;


    @SuppressLint("ValidFragment")
    public AnswerFragment(RoomFragment fragment) {
        this.fragment = fragment;
    }

    public AnswerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_answer, container, false);

        editTextAnswer = view.findViewById(R.id.editTextAnswer);

        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextAnswer.setEnabled(false);
                buttonSubmit.setEnabled(false);
                sendAnswer();
            }
        });

        return view;
    }


    public void sendAnswer() {
        String ans = String.valueOf(editTextAnswer.getText());

        // send answer
        new Client_FunctionCode("15", fragment.roomSocket, "頑皮豹");

        TextView textViewChat = new TextView(getContext());
        textViewChat.setText("你的答案: " + ans);
        textViewChat.setTextColor(Color.parseColor("#006400"));
        fragment.linearLayoutChat.addView(textViewChat);
    }


}
