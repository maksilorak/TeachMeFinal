package com.example.nelson.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nelson.proyectofinal.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private Button btnSignUp;
     private EditText nameText, emailText, passwordText,repeatPasswordText;
    private TextView loginLink;

    String currentUserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnSignUp = (Button) findViewById(R.id.btn_signup);
        nameText = (EditText) findViewById(R.id.input_name);
        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        repeatPasswordText = (EditText) findViewById(R.id.input_repeat_password);
        loginLink = (TextView) findViewById(R.id.link_login);

        // firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("Users");
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){

                    Log.d("User:","User Logged In"+user.getDisplayName());
                }
            }
        };

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLoginActivity();
            }
        });

    }

    private void goLoginActivity() {
        Intent login = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(login);
        finish();
    }

    private void signUp() {

        final String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        final String name = nameText.getText().toString();


        if (!validate()) {
            onSignupFailed();
        }else{
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                String DeviceToken = FirebaseInstanceId.getInstance().getToken();
                                currentUserID = firebaseAuth.getCurrentUser().getUid();

                                User user = new User();
                                user.setDevice_token(DeviceToken);
                                user.setUid(currentUserID);
                                user.setEmail(email);
                                user.setFullname(name);
                                user.setProfileimage("https://i0.wp.com/geekazos.com/wp-content/uploads/2015/02/fb2.jpg?fit=1280%2C720");
                                user.setCountry("Colombia");
                                user.setUsername("none");
                                user.setDob("12-12-1900");
                                user.setGender("none");
                                user.setRelationshipstatus("Single");
                                user.setStatus("Hey There.  I'm using Teach Me to find people who can teach me a language");


                                myRef.child(currentUserID)
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RegisterActivity.this,"Cuenta Creada", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterActivity.this,"Cuenta No Creada"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                firebaseAuth.signOut();
                                goLoginActivity();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign Up Failed", Toast.LENGTH_LONG).show();

    }


    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String repeatPassword =repeatPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (!password.equals(repeatPassword)){
            repeatPasswordText.setError("The passwords don't match");
            valid = false;
        }

        return valid;
    }
}
