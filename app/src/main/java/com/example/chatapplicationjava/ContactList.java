package com.example.chatapplicationjava;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.chatapplicationjava.adapters.ContactListAdapter;
import com.example.chatapplicationjava.model.Chat;
import com.example.chatapplicationjava.model.ChatModel;
import com.example.chatapplicationjava.model.Contact;
import com.example.chatapplicationjava.model.ContactModel;
import com.example.chatapplicationjava.xmpp.RoosterConnectionService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ContactList extends AppCompatActivity implements ContactListAdapter.OnItemClickListener ,ContactListAdapter.OnItemLongClickListener {

    private RecyclerView contactListRecyclerView;
    ContactListAdapter mAdapter;
    FloatingActionButton newContactButton;
    Toolbar toolbar;
    private static final String LOGTAG = "ContactListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);



        toolbar = findViewById(R.id.toolbar_contacts);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        newContactButton = findViewById(R.id.new_contact_button);
        newContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();

            }
        });

        contactListRecyclerView =  findViewById(R.id.contact_list_recycler_view);
        contactListRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        mAdapter = new ContactListAdapter(getApplicationContext());
        mAdapter.setmOnItemClickListener(this);
        mAdapter.setmOnItemLongClickListener(this);
        contactListRecyclerView.setAdapter(mAdapter);
    }

    private void addContact()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_contact_label_text);
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.add_contact_confirm_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOGTAG,"User clicked on OK");
                if(ContactModel.get(getApplicationContext()).addContact(new Contact(input.getText().toString(), Contact.SubscriptionType.NONE)))
                {
                    mAdapter.onContactCountChange();
                    Log.d(LOGTAG,"Contact added successfully");
                }
                else
                {
                    Log.d(LOGTAG,"Could  not add contact");
                }
            }
        });
        builder.setNegativeButton(R.string.add_contact_cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOGTAG,"User clicked on Cancel");
                dialog.cancel();
            }
        });

        builder.show();


    }

    @Override
    public void onItemClick(String contactJid) {
        Log.d(LOGTAG,"Inside contactListActivity The clicked contact is :"+contactJid);

        //Create a Chat in the Chat List table associated with this contact
        List<Chat> chats = ChatModel.get(getApplicationContext()).getChatsByJid(contactJid);
        if( chats.size() == 0)
        {
            Log.d(LOGTAG, contactJid + " is a new chat, adding them. With timestamp :"+ Utilities.getFormattedTime(System.currentTimeMillis()));

            Chat chat = new Chat(contactJid,"",Chat.ContactType.ONE_ON_ONE,System.currentTimeMillis(),0);
            ChatModel.get(getApplicationContext()).addChat(chat);

            //Inside here we start the chat activity
            Intent intent = new Intent(ContactList.this
                    ,ChatView.class);
            intent.putExtra("contact_jid",contactJid);
            startActivity(intent);

            finish();

        }else
        {
            Log.d(LOGTAG, contactJid + " is ALREADY in chat db.Just opening conversation");
            //Inside here we start the chat activity
            Intent intent = new Intent(ContactList.this
                    ,ChatView.class);
            intent.putExtra("contact_jid",contactJid);
            startActivity(intent);

            finish();
        }

    }

    @Override
    public void onItemLongClick(final int uniqueId, final String contactJid, View anchor) {

        PopupMenu popup = new PopupMenu(ContactList.this,anchor, Gravity.CENTER);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.contact_list_popup_menu, popup.getMenu());


        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.delete_contact){
                if(ContactModel.get(getApplicationContext()).deleteContact(uniqueId) )
                {
                    mAdapter.onContactCountChange();
                    if(RoosterConnectionService.getConnection().removeRosterEntry(contactJid))
                    {
                        Log.d(LOGTAG,contactJid + "Successfully deleted from Roster");
                        Toast.makeText(
                                ContactList.this,
                                "Contact deleted successfully ",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                }
            } else if (R.id.delete_contact ==R.id.contact_details) {
                Intent i = new Intent(ContactList.this,ContactDetailsActivity.class);
                i.putExtra("contact_jid",contactJid);
                startActivity(i);
                return true;
            }

            return true;
        });
        popup.show();

    }
}
