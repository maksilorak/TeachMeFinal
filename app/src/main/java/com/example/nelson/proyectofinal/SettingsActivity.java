package com.example.nelson.proyectofinal;


import android.content.Intent;
import android.content.pm.InstrumentationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText userName,userProfileName,userStatus, userCountry,userGender,userRelation, userDOB;
    private Button updateAccountsSettingsButton;
    private CircleImageView userprofileImage;

    private DatabaseReference settingsUserReference;
    private FirebaseAuth mAuth;
    private StorageReference usrProfileImageRef;

    private String currentUserID;

    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        settingsUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        settingsUserReference.keepSynced(true);
        usrProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        //getSupportActionBar().setDisplayShowHomeEnabled();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.settings_username);
        userProfileName = (EditText) findViewById(R.id.settings_profilename);
        userStatus = (EditText) findViewById(R.id.settings_status);
        userCountry = (EditText) findViewById(R.id.settings_profile_country);
        userGender = (EditText) findViewById(R.id.settings_profile_gender);
        userRelation = (EditText) findViewById(R.id.settings_profile_relationship_status);
        userDOB = (EditText) findViewById(R.id.settings_profile_date_birth);

        userprofileImage = (CircleImageView) findViewById(R.id.settings_profile_image);

        updateAccountsSettingsButton = (Button) findViewById(R.id.update_account_settings_button);



        settingsUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myProfileUsername = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myProfileDOB = dataSnapshot.child("dob").getValue().toString();
                    String myProfileCountry = dataSnapshot.child("country").getValue().toString();
                    String myProfileGender = dataSnapshot.child("gender").getValue().toString();
                    String myProfileRelationStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    Picasso.get().load(myProfileImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(userprofileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userprofileImage);
                        }
                    });

                    userName.setText(myProfileUsername);
                    userProfileName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userCountry.setText(myProfileCountry);
                    userGender.setText(myProfileGender);
                    userRelation.setText(myProfileRelationStatus);
                    userDOB.setText(myProfileDOB);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateAccountsSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAccountInfo();
            }
        });

        userprofileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

    }


    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data != null){
            Uri imageUri = data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();

                StorageReference filePath = usrProfileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this,"Profile Image Stored Successfully", Toast.LENGTH_SHORT).show();

                            final  String downloadUrl = task.getResult().getDownloadUrl().toString();

                            settingsUserReference.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent i = new Intent(SettingsActivity.this,SettingsActivity.class);
                                                startActivity(i);
                                                Toast.makeText(SettingsActivity.this,"Profile Image Stored ", Toast.LENGTH_SHORT).show();
                                            }else{
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SettingsActivity.this,"Error"+message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(SettingsActivity.this,"Error Image couldn't be cropped", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }


    private void validateAccountInfo() {
        String username = userName.getText().toString();
        String profilename = userProfileName.getText().toString();
        String status = userStatus.getText().toString();
        String dob = userDOB.getText().toString();
        String gender = userGender.getText().toString();
        String relation = userRelation.getText().toString();
        String country = userCountry.getText().toString();

        if(username.isEmpty()){
            userName.setError("Enter a valid username");
        }
        else if (profilename.isEmpty()){
            userProfileName.setError("Enter a valid Profile Name");
        }

        else if (status.isEmpty()){
            userStatus.setError("Please Write your Status");
        }

        else if (dob.isEmpty()){
            userDOB.setError("Enter a valid Date of Birth");
        }

        else if (country.isEmpty()){
            userCountry.setError("Enter a valid Country");
        }

        else if (gender.isEmpty()){
            userProfileName.setError("Enter a valid Gender");
        }

        else if (relation.isEmpty()){
            userRelation.setError("Enter a valid Relationship Status");
        }
        else {
            updateAccountInfo(username,profilename,status,dob,country,gender,relation);
        }
    }

    private void updateAccountInfo(String username, String profilename, String status, String dob, String country, String gender, String relation) {
        HashMap userMap = new HashMap();
        userMap.put("username",username);
        userMap.put("fullname",profilename);
        userMap.put("status",status);
        userMap.put("dob",dob);
        userMap.put("country",country);
        userMap.put("gender",gender);
        userMap.put("relationshipstatus",relation);
        settingsUserReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    goMainActivity();
                    Toast.makeText(SettingsActivity.this,"Settings Account Updated", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SettingsActivity.this,"Error Ocurred while Updating Account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goMainActivity() {
        Intent main = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(main);
        finish();
    }
}
