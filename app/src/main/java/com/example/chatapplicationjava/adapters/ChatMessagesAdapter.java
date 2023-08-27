package com.example.chatapplicationjava.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapplicationjava.R;
import com.example.chatapplicationjava.Utilities;
import com.example.chatapplicationjava.model.ChatMessage;
import com.example.chatapplicationjava.model.ChatMessagesModel;

import java.util.List;

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {


    public interface OnInformRecyclerViewToScrollDownListener {
        public void onInformRecyclerViewToScrollDown( int size);
    }

    public interface OnItemLongClickListener{
        public void onItemLongClick(int uniqueId , View anchor);
    }
    private static final int SENT = 1;
    private static final int RECEIVED = 2;
    private static final String LOGTAG ="ChatMessageAdapter" ;

    private List<ChatMessage> mChatMessageList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private String contactJid;
    private OnInformRecyclerViewToScrollDownListener mOnInformRecyclerViewToScrollDownListener;
    private OnItemLongClickListener onItemLongClickListener;


    public void setmOnInformRecyclerViewToScrollDownListener(OnInformRecyclerViewToScrollDownListener mOnInformRecyclerViewToScrollDownListener) {
        this.mOnInformRecyclerViewToScrollDownListener = mOnInformRecyclerViewToScrollDownListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public ChatMessagesAdapter(Context context, String contactJid)
    {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.contactJid = contactJid;

        mChatMessageList = ChatMessagesModel.get(context).getMessages(contactJid);
        Log.d(LOGTAG,"Getting messages for :"+ contactJid);


    }

    public void informRecyclerViewToScrollDown()
    {
        mOnInformRecyclerViewToScrollDownListener.onInformRecyclerViewToScrollDown(mChatMessageList.size());
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType)
        {
            case  SENT :
                itemView = mLayoutInflater.inflate(R.layout.chat_message_sent,parent,false);
                return new ChatMessageViewHolder(itemView,this);
            case RECEIVED:
                itemView = mLayoutInflater.inflate(R.layout.chat_message_received,parent,false);
                return new ChatMessageViewHolder(itemView,this);
            default:
                itemView = mLayoutInflater.inflate(R.layout.chat_message_sent,parent,false);
                return new ChatMessageViewHolder(itemView,this);

        }
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {
        ChatMessage chatMessage =mChatMessageList.get(position);
        holder.bindChat(chatMessage);

    }

    @Override
    public int getItemCount() {
        return mChatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage.Type messageType = mChatMessageList.get(position).getType();
        if ( messageType == ChatMessage.Type.SENT)
        {
            return SENT;
        }else{
            return RECEIVED;
        }
    }

    public void onMessageAdd() {
        mChatMessageList = ChatMessagesModel.get(mContext).getMessages(contactJid);
        notifyDataSetChanged();
        informRecyclerViewToScrollDown();

    }
}

class ChatMessageViewHolder extends RecyclerView.ViewHolder{

    private TextView mMessageBody, mMessageTimestamp;
//    private ImageView profileImage;
    private ChatMessage mChatMessage;

    public ChatMessageViewHolder(final View itemView,final ChatMessagesAdapter mAdapter) {
        super(itemView);
        mMessageBody =  itemView.findViewById(R.id.text_message_body);
        mMessageTimestamp =  itemView.findViewById(R.id.text_message_timestamp);
//        profileImage = itemView.findViewById(R.id.profile);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ChatMessagesAdapter.OnItemLongClickListener listener = mAdapter.getOnItemLongClickListener();
                if ( listener!=null)
                {
                    listener.onItemLongClick(mChatMessage.getPersistID(),itemView);
                }
                return false;
            }
        });
    }

    public void bindChat(ChatMessage chatMessage)
    {
        mChatMessage = chatMessage;
        mMessageBody.setText(chatMessage.getMessage());
        mMessageTimestamp.setText(Utilities.getFormattedTime(chatMessage.getTimestamp()));
//        profileImage.setImageResource(R.drawable.login_icon);
    }
}
