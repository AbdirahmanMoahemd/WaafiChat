package com.example.chatapplicationjava;

import androidx.appcompat.app.AppCompatActivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatapplicationjava.xmpp.RoosterConnectionService;

public class login_activity extends AppCompatActivity {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private static final String LOGTAG = "RoosterPlus";

    // UI references.
    private EditText mJidView;
    private EditText mPasswordView;
    private Button mJidSignInButton;
    private ProgressBar mProgressView;

    private BroadcastReceiver mBroadcastReceiver;//Allows us to know when to show the ChatListActivity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mJidView =  findViewById(R.id.email_input);

        mPasswordView =  findViewById(R.id.password_input);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mJidSignInButton = findViewById(R.id.singIn_btn);
        mJidSignInButton.setOnClickListener(view -> attemptLogin());

//        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.progress_bar);
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
                    case Constants.BroadCastMessages.UI_AUTHENTICATED:
                        Log.d(LOGTAG,"Got a broadcast to show the main app window");
                        //Show the main app window
                        showProgress(false);
                        Intent i = new Intent(getApplicationContext(),ChatListActivity.class);
                        startActivity(i);
                        finish();
                        break;

                    case Constants.BroadCastMessages.UI_CONNECTION_ERROR:
                        Log.d(LOGTAG,"Got Connection Error in login activity");
                        showProgress(false);
                        mJidView.setError("Something went wrong while connecting. Make sure the credentials are valid and try again. ");
                        break;
                }

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BroadCastMessages.UI_AUTHENTICATED);
        filter.addAction(Constants.BroadCastMessages.UI_CONNECTION_ERROR);

        this.registerReceiver(mBroadcastReceiver, filter);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid Jid, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mJidView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String jid = mJidView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid Jid address.
        if (TextUtils.isEmpty(jid)) {
            mJidView.setError(getString(R.string.error_field_required));
            focusView = mJidView;
            cancel = true;
        } else if (!isJidValid(jid)) {
            mJidView.setError(getString(R.string.error_invalid_jid));
            focusView = mJidView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            saveCredentialsAndLogin();

        }
    }

    private boolean isJidValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mJidSignInButton.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    private void saveCredentialsAndLogin()
    {
        Log.d(LOGTAG,"saveCredentialsAndLogin() called.");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putString("xmpp_jid", mJidView.getText().toString())
                .putString("xmpp_password", mPasswordView.getText().toString())
                .commit();


        //Start the service
        Intent i1 = new Intent(this, RoosterConnectionService.class);
        startService(i1);

    }



}



