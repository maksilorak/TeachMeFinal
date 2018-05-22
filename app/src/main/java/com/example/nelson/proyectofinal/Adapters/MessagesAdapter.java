package com.example.nelson.proyectofinal.Adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nelson.proyectofinal.Model.Messages;
import com.example.nelson.proyectofinal.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{

    private ArrayList<Messages> usersMessagesList;
    private FirebaseAuth mAuth;

    public MessagesAdapter(ArrayList<Messages> usersMessagesList) {
        this.usersMessagesList = usersMessagesList;
    }

    public MessagesAdapter() {
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_messages_layout,parent,false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = usersMessagesList.get(position);
        String fromUserId = messages.getFrom();

        if (fromUserId.equals(messageSenderID)){
            holder.messageText.setBackgroundResource(R.drawable.message_background_two);
            holder.messageText.setTextColor(Color.BLACK);
            holder.messageText.setGravity(Gravity.RIGHT);
        }else{
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.CYAN);
            holder.messageText.setGravity(Gravity.LEFT);
        }


        holder.messageText.setText(messages.getMessage());
    }

    @Override
    public int getItemCount() {
        return usersMessagesList.size();
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        //public CircleImageView userProfileImage;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message_text);
           // userProfileImage =(CircleImageView) itemView.findViewById(R.id.message_profile_image);
        }
    }
}
