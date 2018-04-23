package com.example.nelson.proyectofinal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {

    private DatabaseReference clickPostReference;
    private FirebaseAuth mAuth;

    private ImageView postImage;
    private TextView postDescription;
    private Button deletePostButton,editPostButton;
    private String postKey,currenUserID,databaseUserID,description,image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);


        mAuth = FirebaseAuth.getInstance();
        currenUserID = mAuth.getCurrentUser().getUid();

        postImage = (ImageView) findViewById(R.id.click_post_image);
        postDescription = (TextView) findViewById(R.id.click_post_description);
        deletePostButton = (Button) findViewById(R.id.delete_post_button);
        editPostButton = (Button) findViewById(R.id.edit_post_button);
        postKey = getIntent().getExtras().get("postKey").toString();
        clickPostReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey);

        deletePostButton.setVisibility(View.INVISIBLE);
        editPostButton.setVisibility(View.INVISIBLE);


        clickPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    description = dataSnapshot.child("description").getValue().toString();
                    image = dataSnapshot.child("postImage").getValue().toString();
                    databaseUserID = dataSnapshot.child("uid").getValue().toString();

                    postDescription.setText(description);
                    Picasso.with(ClickPostActivity.this).load(image).into(postImage);

                    if (currenUserID.equals(databaseUserID)){
                        deletePostButton.setVisibility(View.VISIBLE);
                        editPostButton.setVisibility(View.VISIBLE);
                    }

                    editPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editCurrentPost(description);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        deletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCurrentPost();
            }
        });

    }

    private void editCurrentPost(String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post: ");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickPostReference.child("description").setValue(inputField.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_dark);
    }

    private void deleteCurrentPost() {

        clickPostReference.removeValue();
        goMainActivity();
    }

    private void goMainActivity() {
        Intent main = new Intent(ClickPostActivity.this,MainActivity.class);
        startActivity(main);
        finish();
    }
}
