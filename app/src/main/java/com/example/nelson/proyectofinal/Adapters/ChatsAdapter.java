package com.example.nelson.proyectofinal.Adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nelson.proyectofinal.ChatActivity;
import com.example.nelson.proyectofinal.Model.Chats;
import com.example.nelson.proyectofinal.Model.User;
import com.example.nelson.proyectofinal.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {


    private ArrayList<Chats> chatsArrayList;
    private Context context;
    private DatabaseReference usersreference;


    public ChatsAdapter() {
    }

    public ChatsAdapter(ArrayList<Chats> chatsArrayList) {
        this.chatsArrayList = chatsArrayList;
    }

    public ChatsAdapter(ArrayList<Chats> chatsArrayList, Context context) {
        this.chatsArrayList = chatsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        LayoutInflater inflater =  LayoutInflater.from(context);
        itemView = inflater.inflate(R.layout.list_friends_layout,parent,false);
        return new ChatsAdapter.ChatsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position) {
        usersreference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersreference.keepSynced(true);

        final String list_user_id = chatsArrayList.get(position).getID_Friend();

        usersreference.child(list_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final String userName = dataSnapshot.child("fullname").getValue().toString();
                String userImage = dataSnapshot.child("profileimage").getValue().toString();
                String userStatus = dataSnapshot.child("status").getValue().toString();


                holder.setUsername(userName);
                holder.setImage(userImage);
                holder.setStatus(userStatus);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (dataSnapshot.child("online").exists()){
                            Intent chat =new Intent(context,ChatActivity.class);
                            chat.putExtra("user",list_user_id);
                            chat.putExtra("user_name", userName);
                            context.startActivity(chat);
                        }

                        else{
                            usersreference.child(list_user_id).child("online")
                                    .setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent chat =new Intent(context,ChatActivity.class);
                                    chat.putExtra("user",list_user_id);
                                    chat.putExtra("user_name", userName);
                                    context.startActivity(chat);
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatsArrayList.size() ;
    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

        }


        public void setUsername(String userName) {
            TextView username = (TextView) mView.findViewById(R.id.name_friend);
            username.setText(userName);
            Log.d("User Name Chat: ","Set Successfully");
        }

        public  void setImage(final String userImage) {

            final CircleImageView image = (CircleImageView) mView.findViewById(R.id.photo_friend);

            //With load of the picture in offline conditions
            Picasso.get().load(userImage).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(userImage).into(image);
                            Log.d("User Photo Chat: ","Set Successfully");
                        }
                    });
        }


        public void setStatus(String status) {
            TextView chatStatus = (TextView) mView.findViewById(R.id.status_friend);
            chatStatus.setText(status);
        }
    }
}
