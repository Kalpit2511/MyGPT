package com.example.mygpt;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.myViewHolder> {

    List<message> messageList;
    public messageAdapter(List<message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,null);
       myViewHolder myViewHolder= new myViewHolder(chatView);
       return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        message message = messageList.get(position);

        if(message.getSentBy().equals(message.SENT_BY_ME)){
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightTextView.setText(message.getMessage());
        }
        else{
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.leftTextView.setText(message.getMessage());

        }
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatView, rightChatView;
        TextView leftTextView, rightTextView;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);

            leftTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightTextView = itemView.findViewById(R.id.right_chat_text_view);
        }
    }
}
