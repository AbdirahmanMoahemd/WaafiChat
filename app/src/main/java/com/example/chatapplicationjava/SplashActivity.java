package com.example.chatapplicationjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceManager;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        new Handler().postDelayed(() -> {
            boolean logged_in_state = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean("xmpp_logged_in",false);
            if(!logged_in_state)
            {
                Intent i = new Intent(SplashActivity.this,login_activity.class);
                startActivity(i);
            }else
            {
                Intent i = new Intent(SplashActivity.this,ChatListActivity.class);
                startActivity(i);

            }
            finish();
        },1000);



    }
}