package com.example.nelson.proyectofinal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nelson.proyectofinal.Model.Friends;
import com.example.nelson.proyectofinal.ProfileActivity;
import com.example.nelson.proyectofinal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>{

    private ArrayList<Friends> listaAmigos;
    private Context context;
    private DatabaseReference usersreference;

    public FriendsAdapter() {
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        LayoutInflater inflater =  LayoutInflater.from(context);
        itemView = inflater.inflate(R.layout.list_friends_layout,parent,false);
        return new FriendsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendsViewHolder holder, final int position) {

        usersreference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersreference.keepSynced(true);

        String list_user_id = listaAmigos.get(position).getUid();
        holder.setDate("Friends Since: "+listaAmigos.get(position).getDate());

        usersreference.child(list_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName =dataSnapshot.child("fullname").getValue().toString();
                String userImage = dataSnapshot.child("profileimage").getValue().toString();

                holder.setUsername(userName);
                holder.setImage(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //holder.UID.setText(listaAmigos.get(position).getUid());

    }

    @Override
    public int getItemCount() {
        return listaAmigos.size();
    }

    public FriendsAdapter(ArrayList<Friends> listaAmigos, Context context) {
        this.listaAmigos = listaAmigos;
        this.context = context;
    }



    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;



            //cardView = (CardView) itemView.findViewById(R.id.cardeview_friend);
            //UID = (TextView) itemView.findViewById(R.id.user_id_friend);

        }

        public void setDate(String date){
            TextView friendsSince = (TextView) itemView.findViewById(R.id.status_friend);
            friendsSince.setText(date);
        }

        public void setUsername(String userName) {
            TextView username = (TextView) mView.findViewById(R.id.name_friend);
            username.setText(userName);
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
                        }
                    });
        }
    }
}
