package com.example.nelson.proyectofinal;

import android.app.ProgressDialog;
import android.icu.util.Currency;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.flags.IFlagProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    private Button sendFriendRequest,declineFriendRequest;
    private TextView profileName,profileStatus;
    private ImageView profileImage;

    private DatabaseReference usersReference, friendRequestReference,friendsReference;
    private FirebaseAuth mAuth;

    private String CURRENT_STATE,sender_user_id,receiver_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receiver_user_id = getIntent().getExtras().get("user").toString();


        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendsReference.keepSynced(true);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requets");
        friendRequestReference.keepSynced(true);
        mAuth =FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();



        sendFriendRequest=(Button) findViewById(R.id.profile_send_req_btn);
        declineFriendRequest=(Button) findViewById(R.id.profile_decline_btn);
        profileName = (TextView) findViewById(R.id.profile_fullname);
        profileStatus = (TextView) findViewById(R.id.profile_status);
        profileImage = (ImageView) findViewById(R.id.profile_image);

        CURRENT_STATE = "not_friends";

        usersReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("fullname").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("profileimage").getValue().toString();


                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.get().load(image).into(profileImage);

                friendRequestReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       if (dataSnapshot.exists()){
                           if (dataSnapshot.hasChild(receiver_user_id)){
                               String reg_type= dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                               if (reg_type.equals("sent")){
                                   CURRENT_STATE = "request_sent";
                                   sendFriendRequest.setText("Cancel Friend Request");

                                   declineFriendRequest.setVisibility(View.INVISIBLE);
                                   declineFriendRequest.setEnabled(false);
                               }

                               else if (reg_type.equals("received")){
                                   CURRENT_STATE = "request_received";
                                   sendFriendRequest.setText("Accept Friend Request");

                                   declineFriendRequest.setVisibility(View.VISIBLE);
                                   declineFriendRequest.setEnabled(true);

                                   declineFriendRequest.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           DeclineFriendRequest();
                                       }
                                   });
                               }
                           }
                           else{
                               friendsReference.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       if (dataSnapshot.hasChild(receiver_user_id)){
                                           CURRENT_STATE = "friends";
                                           sendFriendRequest.setText("Unfriend this Person");

                                           declineFriendRequest.setVisibility(View.INVISIBLE);
                                           declineFriendRequest.setEnabled(false);
                                       }
                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        declineFriendRequest.setVisibility(View.INVISIBLE);
        declineFriendRequest.setEnabled(false);

        if (!sender_user_id.equals(receiver_user_id)){
            sendFriendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendFriendRequest.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friends")){
                        sendFriendRequestToPerson();
                    }

                    if (CURRENT_STATE.equals("request_sent")){
                        CancelFriendRequest();
                    }

                    if (CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }

                    if (CURRENT_STATE.equals("friends")){
                        UnfriendACurrentFriend();
                    }
                }
            });
        }

        else{
            declineFriendRequest.setVisibility(View.INVISIBLE);
            sendFriendRequest.setVisibility(View.INVISIBLE);
        }

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
                                        sendFriendRequest.setEnabled(true);
                                        CURRENT_STATE = "not_friends";
                                        sendFriendRequest.setText("Send Friend Request");

                                        declineFriendRequest.setVisibility(View.INVISIBLE);
                                        declineFriendRequest.setEnabled(false);
                                    }
                                }
                            });
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
                                                sendFriendRequest.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequest.setText("Send Friend Request");

                                                declineFriendRequest.setVisibility(View.INVISIBLE);
                                                declineFriendRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calendar.getTime());

        friendsReference.child(sender_user_id).child(receiver_user_id).setValue(saveCurrentDate)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                friendsReference.child(receiver_user_id).child(sender_user_id).setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        sendFriendRequest.setEnabled(true);
                                                        CURRENT_STATE = "friends";
                                                        sendFriendRequest.setText("Unfriend this Person");
                                                        declineFriendRequest.setVisibility(View.INVISIBLE);
                                                        declineFriendRequest.setEnabled(false);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                });
            }
        });

    }

    private void CancelFriendRequest() {
        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        sendFriendRequest.setEnabled(true);
                                        CURRENT_STATE = "not_friends";
                                        sendFriendRequest.setText("Send Friend Request");

                                        declineFriendRequest.setVisibility(View.INVISIBLE);
                                        declineFriendRequest.setEnabled(false);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void sendFriendRequestToPerson() {
        friendRequestReference.child(sender_user_id).child(receiver_user_id).child("request_type")
                .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    friendRequestReference.child(receiver_user_id).child(sender_user_id)
                            .child("request_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        sendFriendRequest.setEnabled(true);
                                        CURRENT_STATE = "request_sent";
                                        sendFriendRequest.setText("Cancel Friend Request");

                                        declineFriendRequest.setVisibility(View.INVISIBLE);
                                        declineFriendRequest.setEnabled(false);
                                    }
                                }
                            });
                }
            }
        });
    }




    

}
