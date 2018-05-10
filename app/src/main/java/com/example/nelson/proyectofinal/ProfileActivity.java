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


    private Button sendFriendRequest,declineFriendRequest;
    private TextView profileName,profileStatus;
    private ImageView profileImage;

    private DatabaseReference usersReference, friendRequestReference;
    private FirebaseAuth auth;

    private String CURRENT_STATE,sender_user_id,receiver_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receiver_user_id = getIntent().getExtras().get("user").toString();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requets");
        //sender_user_id = auth.getCurrentUser().getUid();



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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
