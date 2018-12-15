package com.example.user.drawinggame;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import com.example.user.drawinggame.Login.LoginFragment;
import com.example.user.drawinggame.database_classes.AppDatabase;
import com.example.user.drawinggame.utils.UI;

public class MainActivity extends AppCompatActivity {

    public static final String DATABASE_NAME = "players_db";
    public static FragmentManager fragmentManager;
    public static AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //不會暗屏

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Display display = getWindowManager().getDefaultDisplay();
        UI.getWindowSize(display);

        fragmentManager = getSupportFragmentManager();

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();

        if ((findViewById(R.id.fragment_container) != null)) {
            if (savedInstanceState != null) {
                return;
            }
            UI.fragmentSwitcher(new LoginFragment(), false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        UI.hideSystemUI(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UI.hideSystemUI((Activity) this);
        }
    }

    @Override
    public void onBackPressed() {
    }
}
