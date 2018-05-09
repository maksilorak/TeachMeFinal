package com.example.nelson.proyectofinal.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nelson.proyectofinal.Model.Chats;
import com.example.nelson.proyectofinal.Model.User;
import com.example.nelson.proyectofinal.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {


    private ArrayList<Chats> chatsArrayList;
    private int resource;
    private Activity activity;

    public ChatsAdapter(ArrayList<Chats> chatsArrayList) {
        this.chatsArrayList = chatsArrayList;
    }

    public ChatsAdapter(ArrayList<Chats> chatsArrayList, int resource, Activity activity) {
        this.chatsArrayList = chatsArrayList;
        this.resource = resource;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder{



        public ChatsViewHolder(View itemView) {
            super(itemView);

        }

    }
}
