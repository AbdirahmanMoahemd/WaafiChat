package com.example.chatapplicationjava.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.chatapplicationjava.model.Chat;
import com.example.chatapplicationjava.model.ChatMessage;
import com.example.chatapplicationjava.model.Contact;


public class DatabaseBackend extends SQLiteOpenHelper {

    private static final String LOGTAG = "DatabaseBackend";
    private static DatabaseBackend instance = null;
    private static final String DATABASE_NAME = "roosterPlus_db";
    private static final int DATABASE_VERSION = 2;


    //SQL Statements

    //Create Chat List Table
    private static String CREATE_CHAT_LIST_STATEMENT = "create table "
            + Chat.TABLE_NAME + "("
            + Chat.Cols.CHAT_UNIQUE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Chat.Cols.CONTACT_TYPE + " TEXT, " + Chat.Cols.CONTACT_JID + " TEXT,"
            + Chat.Cols.LAST_MESSAGE + " TEXT, " + Chat.Cols.UNREAD_COUNT + " NUMBER,"
            + Chat.Cols.LAST_MESSAGE_TIME_STAMP + " NUMBER"
            + ");";

    //Create Contact List Table
    //Create Contact List Table
    private static String CREATE_CONTACT_LIST_STATEMENT = "create table "
            + Contact.TABLE_NAME + "("
            + Contact.Cols.CONTACT_UNIQUE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Contact.Cols.SUBSCRIPTION_TYPE + " TEXT, " + Contact.Cols.CONTACT_JID + " TEXT,"
            + Contact.Cols.PROFILE_IMAGE_PATH + " TEXT,"
            + Contact.Cols.PENDING_STATUS_FROM + " NUMBER DEFAULT 0,"
            + Contact.Cols.PENDING_STATUS_TO + " NUMBER DEFAULT 0,"
            + Contact.Cols.ONLINE_STATUS + " NUMBER DEFAULT 0"
            + ");";

    //Create Chat Message List Table
    private static String CREATE_CHAT_MESSAGES_STATEMENT = "create table "
            + ChatMessage.TABLE_NAME + "("
            + ChatMessage.Cols.CHAT_MESSAGE_UNIQUE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ChatMessage.Cols.MESSAGE + " TEXT, "
            + ChatMessage.Cols.MESSAGE_TYPE + " TEXT, "
            + ChatMessage.Cols.TIMESTAMP + " NUMBER, "
            + ChatMessage.Cols.CONTACT_JID + " TEXT"
            + ");";

    private DatabaseBackend(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseBackend getInstance(Context context) {
        Log.d(LOGTAG,"Getting db instance");
        if (instance == null) {
            instance = new DatabaseBackend(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOGTAG,"Creating the tables");
        db.execSQL(CREATE_CONTACT_LIST_STATEMENT);
        db.execSQL(CREATE_CHAT_LIST_STATEMENT);
        db.execSQL(CREATE_CHAT_MESSAGES_STATEMENT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2 && newVersion >= 2) {
            Log.d(LOGTAG,"Upgrading db to version 2....");
            db.execSQL("ALTER TABLE " + Contact.TABLE_NAME + " ADD COLUMN "
                    + Contact.Cols.PENDING_STATUS_TO + " NUMBER DEFAULT 0");
            db.execSQL("ALTER TABLE " + Contact.TABLE_NAME + " ADD COLUMN "
                    + Contact.Cols.PENDING_STATUS_FROM + " NUMBER DEFAULT 0");
            db.execSQL("ALTER TABLE " + Contact.TABLE_NAME + " ADD COLUMN "
                    + Contact.Cols.ONLINE_STATUS + " NUMBER DEFAULT 0");
        }
    }
}
