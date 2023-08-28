package com.example.chatapplicationjava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chatapplicationjava.xmpp.RoosterConnection;
import com.example.chatapplicationjava.xmpp.RoosterConnectionService;

public class MeActivity extends AppCompatActivity {

    private BroadcastReceiver mBroadcastReceiver;
    private TextView jid_text;
    private TextView connectionStatusTextView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String jid = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("xmpp_jid",null);

        jid_text = findViewById(R.id.jid_text);
        jid_text.setText(jid.split("@")[0]);

        String status;
        RoosterConnection connection = RoosterConnectionService.getConnection();
        connectionStatusTextView = findViewById(R.id.connection_status);

        if(  connection != null)
        {
            status = connection.getConnectionStateString();
            connectionStatusTextView.setText(status);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                switch (action)
                {
                    case Constants.BroadCastMessages.UI_CONNECTION_STATUS_CHANGE_FLAG:

                        String status = intent.getStringExtra(Constants.UI_CONNECTION_STATUS_CHANGE);
                        connectionStatusTextView.setText(status);
                        break;
                }



            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BroadCastMessages.UI_CONNECTION_STATUS_CHANGE_FLAG);
        this.registerReceiver(mBroadcastReceiver, filter);
    }
}
