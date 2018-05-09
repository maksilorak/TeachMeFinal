package com.example.nelson.proyectofinal;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus,mProfileFriendsCount;
    private Button mProfileSendReqBtn,mDeclineBtn;


    private DatabaseReference mUsersDatabase, mFriendsReqDatabase, mFriendDatabase,mNotificationDatabase;
    private FirebaseUser mCurrentUser;


    private String current_user_id, current_state;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        current_user_id = getIntent().getStringExtra("user");
        //current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");


        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_fullname);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mDeclineBtn = (Button) findViewById(R.id.profile_decline_btn);


        current_state = "Not_friends";


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("fullname").getValue().toString();
                String displayStatus = dataSnapshot.child("status").getValue().toString();
                String displayImage = dataSnapshot.child("profileimage").getValue().toString();

                mProfileName.setText(displayName);
                mProfileStatus.setText(displayStatus);
                //Picasso.with(ProfileActivity.this).load(displayImage).into(mProfileImage);
                Picasso.get().load(displayImage).into(mProfileImage);

                // ------------------------FRIENDS LIST  /REQUEST FEATURE---------------

                mFriendsReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(current_user_id)){
                            String req_type = dataSnapshot.child(current_user_id).child("request_type").getValue().toString();

                            if (req_type.equals("Received")){

                                current_state = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);


                            }else if (req_type.equals("Sent")){
                                current_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }

                            mProgressDialog.dismiss();
                        }else{
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(current_user_id)){
                                        current_state = "friends";
                                        mProfileSendReqBtn.setText("Unfriend this person");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });
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


        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendReqBtn.setEnabled(false);


                // ------------ NOT FRIENDS STATE -------------------------------

                if (current_state.equals("Not_friends")){

                    mFriendsReqDatabase.child(mCurrentUser.getUid()).child(current_user_id).child("request_type")
                            .setValue("Sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mFriendsReqDatabase.child(current_user_id).child(mCurrentUser.getUid()).child("request_type")
                                .setValue("Received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //mProfileSendReqBtn.setEnabled(true);

                                        HashMap<String,String> notificationData = new HashMap<>();
                                        notificationData.put("from",mCurrentUser.getUid());
                                        notificationData.put("type","request");
                                        mNotificationDatabase.child(current_user_id).push().setValue(notificationData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                current_state = "req_sent";
                                                mProfileSendReqBtn.setText("Cancel Friend Request");

                                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                                mDeclineBtn.setEnabled(false);
                                            }
                                        });


                                        //Toast.makeText(ProfileActivity.this,"Request Sent Succesfullyt",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                Toast.makeText(ProfileActivity.this,"Failed Sending Request",Toast.LENGTH_SHORT).show();
                            }

                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });
                }

                // ------------ CANCEL REQUEST STATE -------------------------------

                if (current_state.equals("req_sent")){
                    mFriendsReqDatabase.child(mCurrentUser.getUid()).child(current_user_id).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsReqDatabase.child(current_user_id).child(mCurrentUser.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProfileSendReqBtn.setEnabled(true);
                                            current_state = "Not_friends";
                                            mProfileSendReqBtn.setText("Send Friend Request");

                                            mDeclineBtn.setVisibility(View.INVISIBLE);
                                            mDeclineBtn.setEnabled(false);
                                        }
                                    });
                        }
                    });
                }


                // ------------  REQUEST RECEIVED STATE -------------------------------

                if (current_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(current_user_id).setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendDatabase.child(current_user_id).child(mCurrentUser.getUid()).setValue(currentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                    mFriendsReqDatabase.child(mCurrentUser.getUid()).child(current_user_id).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mFriendsReqDatabase.child(current_user_id).child(mCurrentUser.getUid()).removeValue()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    mProfileSendReqBtn.setEnabled(true);
                                                                                    current_state = "friends";
                                                                                    mProfileSendReqBtn.setText("Unfriend this Person");

                                                                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                                                                    mDeclineBtn.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            });

                                                }
                                            });
                                }
                            });
                }

            }
        });

    }
}
