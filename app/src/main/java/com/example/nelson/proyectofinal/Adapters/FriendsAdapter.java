package com.example.nelson.proyectofinal.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nelson.proyectofinal.ChatActivity;
import com.example.nelson.proyectofinal.Model.Friends;
import com.example.nelson.proyectofinal.ProfileActivity;
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

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>{

    private ArrayList<Friends> listaAmigos;
    private Context context;
    private DatabaseReference usersReference;

    public FriendsAdapter() {
    }

    public FriendsAdapter(ArrayList<Friends> listaAmigos, Context context) {
        this.listaAmigos = listaAmigos;
        this.context = context;
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

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersReference.keepSynced(true);


        final String list_user_id = listaAmigos.get(position).getID_Friend();
        holder.setDate("Friends Since: "+listaAmigos.get(position).getDate());

        usersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final String userName =dataSnapshot.child("fullname").getValue().toString();
                String userImage = dataSnapshot.child("profileimage").getValue().toString();

                if (dataSnapshot.hasChild("online")){
                    String online_status = (String) dataSnapshot.child("online").getValue().toString();
                    holder.setUserOnline(online_status);
                }

                holder.setUsername(userName);
                holder.setImage(userImage);


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                            userName + "'s Profile", "Send Message"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    Intent profile =new Intent(context,ProfileActivity.class);
                                    profile.putExtra("user",list_user_id);
                                    context.startActivity(profile);

                                }


                                if (which==1){
                                    if (dataSnapshot.child("online").exists()){
                                        Intent chat =new Intent(context,ChatActivity.class);
                                        chat.putExtra("user",list_user_id);
                                        chat.putExtra("user_name", userName);
                                        context.startActivity(chat);
                                    }

                                    else{
                                        usersReference.child(list_user_id).child("online")
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
                            }
                        });

                        builder.show();
                    }
                });
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

        public void setUserOnline(String online_status) {

            ImageView onlineStatusView = (ImageView) mView.findViewById(R.id.online_status);
            ImageView offlineStatusView = (ImageView) mView.findViewById(R.id.offline_status);

            if (online_status.equals("true")){
                onlineStatusView.setVisibility(View.VISIBLE);
                offlineStatusView.setVisibility(View.INVISIBLE);
            }else{
                onlineStatusView.setVisibility(View.INVISIBLE);
                offlineStatusView.setVisibility(View.VISIBLE);
            }
        }
    }


    
}
