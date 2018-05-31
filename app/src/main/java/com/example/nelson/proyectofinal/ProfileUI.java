package com.example.nelson.proyectofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileUI extends AppCompatActivity {

    private TextView profileName,profileStatus,email,relationship,birthday,gender,languages;
    private ImageView profileImage;
    private String receiver_user_id;
    private DatabaseReference friendsReference;
    private Button updateProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_ui);

        receiver_user_id = getIntent().getExtras().get("user").toString();
        //receiver_user_id = FirebaseAuth.getInstance().getUid();
        friendsReference = FirebaseDatabase.getInstance().getReference().child("Users");
        friendsReference.keepSynced(true);

        languages = (TextView) findViewById(R.id.profileui_learning_languages);
        profileName = (TextView) findViewById(R.id.profileui_fullname);
        profileImage = (ImageView) findViewById(R.id.profileui_photo);
        profileStatus = (TextView) findViewById(R.id.profileui_status);
        email = (TextView) findViewById(R.id.profileui_email);
        relationship = (TextView) findViewById(R.id.profileui_relationship_status);
        birthday = (TextView) findViewById(R.id.profileui_dob);
        gender = (TextView) findViewById(R.id.profileui_gender);
        updateProfile = (Button) findViewById(R.id.update_profile);

        setProfile();

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToSettingsActivity();
            }
        });
    }

    private void sendUserToSettingsActivity() {
        Intent settings = new Intent(ProfileUI.this,SettingsActivity.class);
        settings.putExtra("user",receiver_user_id);
        startActivity(settings);
    }

    private void setProfile() {
        friendsReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("fullname").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("profileimage").getValue().toString();
                String dob = dataSnapshot.child("dob").getValue().toString();
                String relation = dataSnapshot.child("relationshipstatus").getValue().toString();
                String genderA = dataSnapshot.child("gender").getValue().toString();
                String mail = dataSnapshot.child("email").getValue().toString();
                String idioma = dataSnapshot.child("languages").getValue().toString();

                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.get().load(image).into(profileImage);
                email.setText(mail);
                relationship.setText(relation);
                birthday.setText(dob);
                gender.setText(genderA);
                languages.setText(idioma);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
