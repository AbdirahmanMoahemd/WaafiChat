package com.example.chatapplicationjava;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chatapplicationjava.model.Contact;
import com.example.chatapplicationjava.model.ContactModel;
import com.example.chatapplicationjava.xmpp.RoosterConnectionService;

public class ContactDetailsActivity extends AppCompatActivity {
    private static final String LOGTAG = "ContactDetailsActivity" ;
    private String contactJid;
    private ImageView profileImage;
    private CheckBox fromCheckBox;
    private CheckBox toCheckBox;
    private Context mApplicationContext;

    private TextView pendingFrom;
    private TextView pendingTo;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        mApplicationContext=getApplicationContext();

        //Get the contact Jid
        Intent intent = getIntent();
        contactJid = intent.getStringExtra("contact_jid");


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(contactJid.split("@")[0]);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pendingFrom = findViewById(R.id.pending_from);
        pendingTo = findViewById(R.id.pending_to);

        fromCheckBox = findViewById(R.id.them_to_me);
        fromCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( fromCheckBox.isChecked())
                {
                    //There is nothing to do here
                    Log.d(LOGTAG,"The FROM checkbox is checked");
                }else
                {
                    //Send unsubscribed to cancel subscription
                    Log.d(LOGTAG,"The FROM checkbox is UNchecked");
                    if(RoosterConnectionService.getConnection().unsubscribed(contactJid))
                    {
                        Toast.makeText(mApplicationContext,"Successfully Stopped sending presence updates to "+ contactJid,Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        toCheckBox =  findViewById(R.id.me_to_tem);
        toCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( toCheckBox.isChecked())
                {
                    //Send subscription request
                    Log.d(LOGTAG,"The TO checkbox is checked");
                    if(RoosterConnectionService.getConnection().subscribe(contactJid))
                    {
                        Toast.makeText(mApplicationContext,"Subscription request sent to  "+ contactJid,Toast.LENGTH_LONG).show();
                    }
                }else
                {
                    //Send them an unsubscribe
                    Log.d(LOGTAG,"The TO checkbox is UNchecked");
                    if(RoosterConnectionService.getConnection().unsubscribe(contactJid))
                    {
                        Toast.makeText(mApplicationContext,"You successfuly stopped getting presence updates from  "+ contactJid,Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        if(!ContactModel.get(getApplication()).isContactStranger(contactJid))
        {
            Contact contact = ContactModel.get(getApplicationContext()).getContactByJidString(contactJid);
            Contact.SubscriptionType subType = contact.getSubscriptionType();
            if(subType == Contact.SubscriptionType.NONE)
            {
                fromCheckBox.setEnabled(false);
                fromCheckBox.setChecked(false);
                toCheckBox.setChecked(false);
            }else if (subType == Contact.SubscriptionType.FROM)
            {
                fromCheckBox.setEnabled(true);
                fromCheckBox.setChecked(true);
                toCheckBox.setChecked(false);

            }else if (subType == Contact.SubscriptionType.TO)
            {
                fromCheckBox.setEnabled(false);
                fromCheckBox.setChecked(false);
                toCheckBox.setChecked(true);

            }else if (subType == Contact.SubscriptionType.BOTH)
            {
                fromCheckBox.setEnabled(true);
                fromCheckBox.setChecked(true);
                toCheckBox.setChecked(true);

            }


            if(contact.isPendingFrom())
            {
                pendingFrom.setVisibility(View.VISIBLE);
            }else
            {
                pendingFrom.setVisibility(View.GONE);
            }

            if(contact.isPendingTo())
            {
                pendingTo.setVisibility(View.VISIBLE);
            }else
            {
                pendingTo.setVisibility(View.GONE);
            }

            if(subType == Contact.SubscriptionType.NONE)
            {
                fromCheckBox.setEnabled(false);
                fromCheckBox.setChecked(false);
                toCheckBox.setChecked(false);
            }else if (subType == Contact.SubscriptionType.FROM)
            {
                fromCheckBox.setEnabled(true);
                fromCheckBox.setChecked(true);
                toCheckBox.setChecked(false);

            }else if (subType == Contact.SubscriptionType.TO)
            {
                fromCheckBox.setEnabled(false);
                fromCheckBox.setChecked(false);
                toCheckBox.setChecked(true);

            }else if (subType == Contact.SubscriptionType.BOTH)
            {
                fromCheckBox.setEnabled(true);
                fromCheckBox.setChecked(true);
                toCheckBox.setChecked(true);

            }

        }else
        {

            fromCheckBox.setEnabled(false);
            fromCheckBox.setChecked(false);
            toCheckBox.setChecked(false);
            toCheckBox.setEnabled(true);
        }

    }
}
