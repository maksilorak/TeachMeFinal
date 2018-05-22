package com.example.nelson.proyectofinal;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.nelson.proyectofinal.Model.Solicitud;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity {


    private Button sendFriendRequest,declineFriendRequest;
    private TextView profileName,profileStatus;
    private ImageView profileImage;

    private DatabaseReference usersReference, friendRequestReference,friendsReference,NotificactionsReference;


    private String sender_user_id,receiver_user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //Id del usuario sobre el que se presióno en el cardview, puede ser el mismo usuario que está logueado
        receiver_user_id = getIntent().getExtras().get("user").toString();


        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendsReference.keepSynced(true);
        NotificactionsReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotificactionsReference.keepSynced(true);


        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        friendRequestReference.keepSynced(true);


        //Usuario logueado en la aplicación. Quien estpa usando la aplicación.
        sender_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();



        sendFriendRequest=(Button) findViewById(R.id.profile_send_req_btn);
        declineFriendRequest=(Button) findViewById(R.id.profile_decline_btn);
        profileName = (TextView) findViewById(R.id.profile_fullname);
        profileStatus = (TextView) findViewById(R.id.profile_status);
        profileImage = (ImageView) findViewById(R.id.profile_image);



        setProfile();
        verifyRequetStatus();
        verifyFriendStatus();


        sendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineFriendRequest.setVisibility(View.VISIBLE);
                declineFriendRequest.setEnabled(true);
                sendFriendRequest.setVisibility(View.GONE);
                setRequestDataReceiver();
                setRequestDataSender();

            }
        });

        declineFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFriendRequest.setVisibility(View.VISIBLE);
                sendFriendRequest.setEnabled(true);
                declineFriendRequest.setVisibility(View.GONE);
                DeclineFriendRequest();

            }
        });


    }

    private void verifyFriendStatus() {
        friendsReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiver_user_id)){
                    String isAfriend = dataSnapshot.child(receiver_user_id).child("isYourFriend").getValue().toString();
                    if (isAfriend.equals("true")){
                        declineFriendRequest.setText("Unfriend this person");
                        declineFriendRequest.setVisibility(View.VISIBLE);
                        declineFriendRequest.setEnabled(true);
                        sendFriendRequest.setVisibility(View.GONE);
                        sendFriendRequest.setEnabled(false);

                        declineFriendRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                friendsReference.child(sender_user_id).child(receiver_user_id).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    friendsReference.child(receiver_user_id).child(sender_user_id)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                sendFriendRequest.setEnabled(true);
                                                                sendFriendRequest.setText("Send Friend Request");
                                                                declineFriendRequest.setVisibility(View.GONE);
                                                                declineFriendRequest.setEnabled(false);
                                                                DeclineFriendRequest();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void verifyRequetStatus() {
        friendRequestReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild(receiver_user_id)){
                        String statusRequest = dataSnapshot.child(receiver_user_id).child("status_request").getValue().toString();


                        if (statusRequest.equals("sent")){

                            declineFriendRequest.setVisibility(View.VISIBLE);
                            declineFriendRequest.setEnabled(true);
                            sendFriendRequest.setVisibility(View.GONE);
                            sendFriendRequest.setEnabled(false);

                        }
                        else if (statusRequest.equals("received")){
                            sendFriendRequest.setText("Accept Friend Request");
                            sendFriendRequest.setVisibility(View.VISIBLE);
                            declineFriendRequest.setEnabled(false);
                            declineFriendRequest.setVisibility(View.GONE);

                            sendFriendRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    setDataReceiverFriend();
                                    removeRequest();
                                    AcceptFriendRequest();
                                }
                            });
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeRequest() {
        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void setDataReceiverFriend() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calendar.getTime());

        HashMap datosFriends = new HashMap();
        datosFriends.put("isYourFriend","true");
        datosFriends.put("ID_Friend",sender_user_id);
        datosFriends.put("date",saveCurrentDate);

        friendsReference.child(receiver_user_id).child(sender_user_id).updateChildren(datosFriends).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this," Receiver Friends Updated", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ProfileActivity.this,"Error Ocurred while Updating Receiver Friend Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void AcceptFriendRequest() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calendar.getTime());

        HashMap datosFriends = new HashMap();
        datosFriends.put("isYourFriend","true");
        datosFriends.put("ID_Friend",receiver_user_id);
        datosFriends.put("date",saveCurrentDate);

        friendsReference.child(sender_user_id).child(receiver_user_id).updateChildren(datosFriends).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this," Sender Friends Updated", Toast.LENGTH_SHORT).show();
                    declineFriendRequest.setText("Unfriend this Person");
                    declineFriendRequest.setVisibility(View.VISIBLE);
                    declineFriendRequest.setEnabled(true);
                    sendFriendRequest.setEnabled(false);
                    sendFriendRequest.setVisibility(View.GONE);

                    declineFriendRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UnfriendACurrentFriend();

                        }
                    });


                }else {
                    Toast.makeText(ProfileActivity.this,"Error Ocurred while Updating Semder Friend Datat", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void UnfriendACurrentFriend() {
        friendsReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            friendsReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendFriendRequest.setVisibility(View.VISIBLE);
                                                sendFriendRequest.setEnabled(true);
                                                sendFriendRequest.setText("Send Friend Request");
                                                declineFriendRequest.setVisibility(View.GONE);
                                                declineFriendRequest.setEnabled(false);

                                                sendFriendRequest.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        setRequestDataReceiver();
                                                        setRequestDataSender();
                                                        setProfile();
                                                    }
                                                });

                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void DeclineFriendRequest() {
        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ProfileActivity.this, "You Declined a Friend Request Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }



    private void setRequestDataSender() {

        HashMap datosReceptor = new HashMap();
        datosReceptor.put("status_request", "sent");
        datosReceptor.put("ID", receiver_user_id);
        datosReceptor.put("friendsAlready","false");

        friendRequestReference.child(sender_user_id).child(receiver_user_id).updateChildren(datosReceptor).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this,"Request Sender Data Updated", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ProfileActivity.this,"Error Ocurred while Updating Request Sender Datat", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

   private void setRequestDataReceiver() {
       HashMap datosReceptor = new HashMap();
       datosReceptor.put("status_request", "received");
       datosReceptor.put("ID", sender_user_id);
       datosReceptor.put("friendsAlready","false");

        friendRequestReference.child(receiver_user_id).child(sender_user_id).updateChildren(datosReceptor).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this,"Request Receiver Data Updated", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ProfileActivity.this,"Error Ocurred while Updating Request Receiver Datat", Toast.LENGTH_SHORT).show();
                }
            }
        });


   }


    private void setProfile() {
        usersReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("fullname").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("profileimage").getValue().toString();

                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.get().load(image).into(profileImage);

                if (sender_user_id.equals(receiver_user_id)){
                    declineFriendRequest.setVisibility(View.INVISIBLE);
                    sendFriendRequest.setVisibility(View.INVISIBLE);
                }else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
