package com.example.user.drawinggame.Room.Drawing;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.user.drawinggame.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuessFragment extends Fragment {

    private FrameLayout frameLayoutGuess;
    private GuessView guessView;
    private GuessPath guessPath;

    public GuessPath getGuessPath() {
        return guessPath;
    }

    public GuessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_guess, container, false);

        frameLayoutGuess = view.findViewById(R.id.frameLayoutGuess);
        guessView = new GuessView(frameLayoutGuess.getContext());
        guessPath = new GuessPath();

        frameLayoutGuess.addView(guessView);

        return view;
    }

    public GuessView getGuessView() {
        return guessView;
    }

    public void setGuessView(GuessView guessView) {
        this.guessView = guessView;
    }
}
