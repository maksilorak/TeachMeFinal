package com.example.nelson.proyectofinal;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class PostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton selectPostImage;
    private Button updatePostButton;
    private EditText postDescription;

    final static int Gallery_Pick = 1;
    private Uri imageUri;
    private String description;

    private StorageReference postImageReference;
    private DatabaseReference usersRef,postsReference;
    private FirebaseAuth auth;

    private String saveCurrentDate, saveCurrentTime,postRandomName, dowloadUrl, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        auth = FirebaseAuth.getInstance();
        current_user_id = auth.getCurrentUser().getUid();
        postImageReference = FirebaseStorage.getInstance().getReference();
        postsReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        selectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        updatePostButton = (Button) findViewById(R.id.update_post_button);
        postDescription = (EditText) findViewById(R.id.post_description);

        toolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");



        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePostInfo();
            }
        });

    }

    private void validatePostInfo() {
        description = postDescription.getText().toString();

        if (imageUri == null){
            Toast.makeText(PostActivity.this,"Select Post Image First", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(description)){
            Toast.makeText(PostActivity.this,"Write a description of the photo", Toast.LENGTH_SHORT).show();
        }

        else{
            storeImageToFirebaseStorage();
        }
    }

    private void storeImageToFirebaseStorage() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(time.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;


        StorageReference filepath = postImageReference.child("Post Images").child(imageUri.getLastPathSegment()+ postRandomName+".jpg");

        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    dowloadUrl = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(PostActivity.this,"Image Uploaded to Storage", Toast.LENGTH_SHORT).show();
                    savePostInfoToDatabase();
                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this,"Error: "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void savePostInfoToDatabase() {
        usersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();


                    HashMap postsMap = new HashMap();
                    postsMap.put("uid",current_user_id);
                    postsMap.put("date",saveCurrentDate);
                    postsMap.put("time",saveCurrentTime);
                    postsMap.put("description",description);
                    postsMap.put("postImage",dowloadUrl);
                    postsMap.put("profileimage",userProfileImage);
                    postsMap.put("fullname",userFullName);
                    postsReference.child(current_user_id + postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(PostActivity.this,"New Post Updated Successfully", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(PostActivity.this,"Error: ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            selectPostImage.setImageURI(imageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            sendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainActivity() {
        Intent main = new Intent(PostActivity.this,MainActivity.class);
        startActivity(main);
        finish();
    }
}
