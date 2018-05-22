package com.example.nelson.proyectofinal.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nelson.proyectofinal.ChatActivity;
import com.example.nelson.proyectofinal.Model.Requests;
import com.example.nelson.proyectofinal.ProfileActivity;
import com.example.nelson.proyectofinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder> {

    private ArrayList<Requests> listaRequests;
    private Context context;
    private DatabaseReference usersReference,requestsReference,friendsReference;

    public RequestsAdapter() {
    }

    public RequestsAdapter(ArrayList<Requests> listaRequests, Context context) {
        this.listaRequests = listaRequests;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        LayoutInflater inflater = LayoutInflater.from(context);
        itemView = inflater.inflate(R.layout.friend_request_all_users_layout, parent, false);
        return new RequestsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position) {
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersReference.keepSynced(true);

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        requestsReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");


        final String friendsRequestUser = listaRequests.get(position).getID();
        final String request_type = listaRequests.get(position).getStatus_request();


        if (request_type.equals("received")){
            usersReference.child(friendsRequestUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String userName = dataSnapshot.child("fullname").getValue().toString();
                    String userImage = dataSnapshot.child("profileimage").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    holder.setUsername(userName);
                    holder.setImage(userImage);
                    holder.setUserStatus(userStatus);
                    holder.setTypeRequest("Type of Request: " +request_type);
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        else {
            usersReference.child(friendsRequestUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String userName = dataSnapshot.child("fullname").getValue().toString();
                    String userImage = dataSnapshot.child("profileimage").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();


                    holder.setUsername(userName);
                    holder.setImage(userImage);
                    holder.setUserStatus(userStatus);
                    holder.setButton();
                    holder.setTypeRequest("Type of Request: " +request_type);
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        holder.aceptarRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.setDataReceiverFriend(friendsReference,friendsRequestUser);
                holder.removeRequest(requestsReference,friendsRequestUser);
                holder.AcceptFriendRequest(friendsReference,friendsRequestUser);
                holder.setMessageSuccess();



            }
        });


        holder.cancelarRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sender_user_id = FirebaseAuth.getInstance().getUid();
                requestsReference.child(sender_user_id).child(friendsRequestUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            requestsReference.child(friendsRequestUser).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        holder.setMessageSuccess();
                                        holder.setCancelSuccess();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

    }



    @Override
    public int getItemCount() {
        return listaRequests.size();
    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button aceptarRequest;
        Button cancelarRequest;

        public RequestsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

            aceptarRequest=(Button) mView.findViewById(R.id.Accept_request);
            cancelarRequest = (Button) mView.findViewById(R.id.Cancel_request);
        }


        public void setUsername(String userName) {
            TextView username = (TextView) mView.findViewById(R.id.request_profile_name);
            username.setText(userName);
        }

        public  void setImage(final String userImage) {

            final CircleImageView image = (CircleImageView) mView.findViewById(R.id.request_profile_image);

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


        public void setUserStatus(String userStatus) {
            TextView user_status = (TextView) mView.findViewById(R.id.request_profile_status);
            user_status.setText(userStatus);
        }


        public void setButton() {
            Button aceptar = (Button) mView.findViewById(R.id.Accept_request);
            aceptar.setVisibility(mView.INVISIBLE);
            aceptar.setEnabled(false);
        }

        public void setTypeRequest(String request_type) {
            TextView tipoPeticion = (TextView) mView.findViewById(R.id.request_profile_type);
            tipoPeticion.setText(request_type);
        }


        public void setDataReceiverFriend(DatabaseReference friendsReference, String friendsRequestUser) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calendar.getTime());
            String sender_user_id = FirebaseAuth.getInstance().getUid();


            HashMap datosFriends = new HashMap();
            datosFriends.put("isYourFriend","true");
            datosFriends.put("ID_Friend",sender_user_id);
            datosFriends.put("date",saveCurrentDate);

            friendsReference.child(friendsRequestUser).child(sender_user_id).updateChildren(datosFriends).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Log.d("Friend : ","Completely Created");
                    }else {
                        Log.d("Friend : ","Not Created");
                    }
                }
            });
        }

        public void removeRequest(final DatabaseReference requestsReference, final String friendsRequestUser) {
            final String sender_user_id = FirebaseAuth.getInstance().getUid();
            requestsReference.child(sender_user_id).child(friendsRequestUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        requestsReference.child(friendsRequestUser).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d("Borrado de Request","Exitoso");
                                }
                            }
                        });
                    }
                }
            });
        }

        public void AcceptFriendRequest(DatabaseReference friendsReference, String friendsRequestUser) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calendar.getTime());
            String sender_user_id = FirebaseAuth.getInstance().getUid();

            HashMap datosFriends = new HashMap();
            datosFriends.put("isYourFriend","true");
            datosFriends.put("ID_Friend",friendsRequestUser);
            datosFriends.put("date",saveCurrentDate);

            friendsReference.child(sender_user_id).child(friendsRequestUser).updateChildren(datosFriends).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){

                        Log.d("Friend : ","Created");

                    }else {
                        Log.d("Friend : ","Not Created");
                    }
                }
            });
        }


        public void setMessageSuccess() {
            TextView username = (TextView) mView.findViewById(R.id.request_profile_name);
            username.setText("");
            TextView user_status = (TextView) mView.findViewById(R.id.request_profile_status);
            user_status.setText("");
            Button aceptar = (Button) mView.findViewById(R.id.Accept_request);
            aceptar.setVisibility(mView.INVISIBLE);
            aceptar.setEnabled(false);
            Button cancelar = (Button) mView.findViewById(R.id.Cancel_request);
            cancelar.setVisibility(mView.INVISIBLE);
            cancelar.setEnabled(false);
            TextView tipoPeticion = (TextView) mView.findViewById(R.id.request_profile_type);
            tipoPeticion.setText("You´re already Friends.  Got to Friends!");
        }

        public void setCancelSuccess() {
            TextView tipoPeticion = (TextView) mView.findViewById(R.id.request_profile_type);
            tipoPeticion.setText("You Cancelled the friend  successfully");
        }
    }
}
