package com.example.chatapplicationjava.xmpp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.chatapplicationjava.Constants;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ping.android.ServerPingWithAlarmManager;

import java.io.IOException;


public class RoosterConnectionService extends Service {
    private static final String LOGTAG ="RoosterConService";

    private boolean mActive;//Stores whether or not the thread is active
    private Thread mThread;
    private Handler mTHandler;//We use this handler to post messages to

    public static RoosterConnection getConnection() {
        return mConnection;
    }

    //the background thread.
    private static RoosterConnection mConnection;


    public RoosterConnectionService() {
    }

    private void initConnection()
    {
        Log.d(LOGTAG,"initConnection()");
        if( mConnection == null)
        {
            mConnection = new RoosterConnection(this);
        }

        try {
            mConnection.connect();
        } catch (IOException |SmackException |XMPPException e) {

            Log.d(LOGTAG,"Something went wrong while connecting ,make sure the credentials are right and try again");

            Intent i = new Intent(Constants.BroadCastMessages.UI_CONNECTION_ERROR);
            i.setPackage(getApplicationContext().getPackageName());
            getApplicationContext().sendBroadcast(i);
            Log.d(LOGTAG,"Sent the broadcast for connection Error from service");

            //Stop the service all together if user is not logged in already.
            boolean logged_in_state = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean("xmpp_logged_in",false);
            if(!logged_in_state)
            {
                Log.d(LOGTAG,"Logged in state :"+ logged_in_state + "calling stopself()");

                stopSelf();

            }else
            {
                Log.d(LOGTAG,"Logged in state :"+ logged_in_state);

            }

            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ServerPingWithAlarmManager.onCreate(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Do your task here
        start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ServerPingWithAlarmManager.onDestroy();
        stop();
    }

    public void start()
    {
        Log.d(LOGTAG," Service Start() function called. mActive :"+ mActive);
        if(!mActive)
        {
            mActive = true;
            if( mThread ==null || !mThread.isAlive())
            {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                        Looper.loop();

                    }
                });
                mThread.start();
            }
        }

    }


    public void stop()
    {
        Log.d(LOGTAG,"stop()");
        mActive = false;
        mTHandler.post(() -> {
            if( mConnection != null)
            {
                mConnection.disconnect();
            }
        });

    }

}
