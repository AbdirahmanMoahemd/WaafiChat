package com.example.chatapplicationjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.chatapplicationjava.adapters.ChatListAdapter;
import com.example.chatapplicationjava.model.Chat;
import com.example.chatapplicationjava.model.ChatModel;
import com.example.chatapplicationjava.xmpp.RoosterConnectionService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatListActivity extends AppCompatActivity implements ChatListAdapter.OnItemClickListener,ChatListAdapter.OnItemLongClickListener {

    private static final String LOGTAG = "ChatListActivity";
    private RecyclerView chatsRecyclerView;
    private FloatingActionButton newConversationButton;

    ChatListAdapter mAdapter;
    Toolbar toolbar;

    private BroadcastReceiver mBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Waafi Chat");



        boolean logged_in_state = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("xmpp_logged_in",false);
        if(!logged_in_state)
        {
            Intent i = new Intent(ChatListActivity.this,login_activity.class);
            startActivity(i);
            finish();
        }else
        {
            if(!Utilities.isServiceRunning(RoosterConnectionService.class,getApplicationContext()))
            {
                Log.d(LOGTAG,"Service not running, starting it ...");
                //Start the service
                Intent i1 = new Intent(this,RoosterConnectionService.class);
                startService(i1);

            }else
            {
                Log.d(LOGTAG ,"The service is already running.");
            }

        }


        chatsRecyclerView =  findViewById(R.id.chatsRecyclerView);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        mAdapter = new ChatListAdapter(getApplicationContext());
        mAdapter.setmOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        chatsRecyclerView.setAdapter(mAdapter);

        newConversationButton =  findViewById(R.id.new_conversation_floating_button);
        newConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ChatListActivity.this,ContactList.class);
                startActivity(i);

            }
        });





    }





    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
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
                    case Constants.BroadCastMessages.UI_NEW_CHAT_ITEM:
                        mAdapter.onChatCountChange();
                        return;
                }

            }
        };

        IntentFilter filter = new IntentFilter(Constants.BroadCastMessages.UI_NEW_CHAT_ITEM);
        registerReceiver(mBroadcastReceiver,filter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_me_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId()  == R.id.me)
        {
            Intent i = new Intent(ChatListActivity.this,MeActivity.class);

            startActivity(i);
        }
        if( item.getItemId()  == R.id.reconnect)
        {

            Intent i1 = new Intent(this, RoosterConnectionService.class);
            startService(i1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(String contactJid, Chat.ContactType chatType) {

        Intent i = new Intent(ChatListActivity.this,ChatView.class);
        i.putExtra("contact_jid",contactJid);
        i.putExtra("chat_type",chatType);
        startActivity(i);
    }

    @Override
    public void onItemLongClick(final String contactJid,final int chatUniqueId, View anchor) {

        PopupMenu popup = new PopupMenu(ChatListActivity.this,anchor, Gravity.CENTER);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.chat_list_popup_menu, popup.getMenu());
        popup.setGravity(Gravity.CENTER);

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getTitle() == "Delete Chat"){
                if(ChatModel.get(getApplicationContext()).deleteChat(chatUniqueId) )
                {
                    mAdapter.onChatCountChange();
                    Toast.makeText(
                            ChatListActivity.this,
                            "Chat deleted successfully ",
                            Toast.LENGTH_SHORT
                    ).show();

                }
            }

            return true;
        });
        popup.show();

    }


}
