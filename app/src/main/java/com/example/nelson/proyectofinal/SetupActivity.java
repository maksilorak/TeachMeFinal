package com.example.nelson.proyectofinal;

import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName,fullName, countryName;
    private Button btnSaveInfomation;
    private CircleImageView profileImage;

    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private StorageReference usrProfileImageRef;

    String currentUserID;
    final static int Gallery_Pick = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);


        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        usrProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        userName = (EditText) findViewById(R.id.setup_username);
        fullName = (EditText) findViewById(R.id.setup_full_name);
        countryName = (EditText) findViewById(R.id.setup_country_name);
        btnSaveInfomation = (Button) findViewById(R.id.save);
        profileImage = (CircleImageView) findViewById(R.id.setup_profile_image);


        btnSaveInfomation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountInfo();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
            }
        });

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("profileimage")){

                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(SetupActivity.this).load(image).placeholder(R.drawable.profile).into(profileImage);
                    }else{
                        Toast.makeText(SetupActivity.this,"Select Profile Image First", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                            Toast.makeText(SetupActivity.this,"Profile Image Stored Successfully", Toast.LENGTH_SHORT).show();

                            final  String downloadUrl = task.getResult().getDownloadUrl().toString();

                            usersRef.child("profileimage").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Intent i = new Intent(SetupActivity.this,SetupActivity.class);
                                                    startActivity(i);
                                                    Toast.makeText(SetupActivity.this,"Profile Image Stored ", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(SetupActivity.this,"Error"+message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                        }else {
                            Toast.makeText(SetupActivity.this,"Error Image couldn't be cropped", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void saveAccountInfo() {
        String username = userName.getText().toString();
        String fullname = fullName.getText().toString();
        String country = countryName.getText().toString();

        if (TextUtils.isEmpty(username)){
            userName.setError("between 4 and 10 alphanumeric characters");
        }

        if (TextUtils.isEmpty(fullname)){
            userName.setError("Please write your  full name");
        }

        if (TextUtils.isEmpty(country)){
            userName.setError("Please write your  country");
        }
        else {
            HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("country",country);
            userMap.put("status","Married");
            userMap.put("gender","none");
            userMap.put("dob","none");
            userMap.put("relationshipstatus","none");
            usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        goMainActivity();
                        Toast.makeText(SetupActivity.this,"Account Created Successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this,"Error Ocurred"+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void goMainActivity() {
        Intent main = new Intent(SetupActivity.this,MainActivity.class);
        startActivity(main);
        finish();
    }
}
