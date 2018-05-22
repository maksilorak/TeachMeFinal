package com.example.nelson.proyectofinal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.nelson.proyectofinal.Model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private FirebaseDatabase db;
    private DatabaseReference users;
    public static  final int SING_IN_CODE=999;
    private EditText eCorreo, eContrasena;
    private TextView signUpLink;
    private RelativeLayout rootLayout;
    private SignInButton btnSignInGoogle;
    private LoginButton btnSignInFacebook;




    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        inicializar();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        eCorreo = (EditText) findViewById(R.id.eCorreo);
        eContrasena = (EditText) findViewById(R.id.eContrasena);
        btnSignInGoogle = (SignInButton) findViewById(R.id.btnSignInGoogle);
        btnSignInFacebook = (LoginButton) findViewById(R.id.btnSignInFacebook);
        signUpLink = (TextView) findViewById(R.id.link_signup);


        loadingBar = new ProgressDialog(this);



        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(register);
                finish();
            }
        });

        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(i,SING_IN_CODE);
            }
        });

        //facebook Login
        btnSignInFacebook.setReadPermissions("email","public_profile");

        btnSignInFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Login con Facebook","Login Exitoso");
                signInFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("Login con Facebook","Login Cancelado");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Login con Facebook","Login Error");
                error.printStackTrace();
            }
        });




    }


    private void signInFacebook(AccessToken accessToken) {
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            getUserData();
                            goMainActivity();
                        }else{
                            Toast.makeText(LoginActivity.this,"Autenticacion con Facebook no exitosa",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== SING_IN_CODE){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInGoogle(googleSignInResult);
        }else{
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void signInGoogle(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()){
            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInResult.getSignInAccount().getIdToken(),null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    getUserData();
                    goMainActivity();
                }
            });
        }else{
            Toast.makeText(LoginActivity.this,"Autenticación con GOOGLE no exitosa",Toast.LENGTH_SHORT).show();

        }
    }

    private void getUserData(){

        String email = firebaseAuth.getCurrentUser().getEmail();
        String nombre =firebaseAuth.getCurrentUser().getDisplayName();
        String UID = firebaseAuth.getCurrentUser().getUid();
        String DeviceToken = FirebaseInstanceId.getInstance().getToken();
        String locale = getResources().getConfiguration().locale.getCountry();



        User user = new User();
        user.setUid(UID);
        user.setDevice_token(DeviceToken);
        user.setEmail(email);
        user.setFullname(nombre);
        user.setProfileimage(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
        user.setCountry(locale);
        user.setUsername(nombre);
        user.setDob("12-12-1900");
        user.setGender("MALE");
        user.setRelationshipstatus("Single");
        user.setStatus("Hi there.  I'm a new User wanting to contact teachers for learning a new language");



        users.child(UID)
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this,"Datos Usuario Actualizados", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goMainActivity() {
        Intent main = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(main);
        finish();
    }

    private void inicializar() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    goMainActivity();
                    Log.d("FirebaseUser: ", "Usuario Logueado");
                }
                else{
                    Log.d("FirebaseUser: ", "Usuario No Logueado");
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    public void iniciarSesionClicked(View view) {
        if (!validate()) {
            onLoginFailed();
        }else{
            iniciarSesion(eCorreo.getText().toString(),eContrasena.getText().toString());
        }

    }

    private void iniciarSesion(String correo, String contrasena) {

        firebaseAuth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            String online_user_id = firebaseAuth.getCurrentUser().getUid();
                            String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                            users.child(online_user_id).child("device_token").setValue(DeviceToken)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this,"Inicio de Sesión Exitoso", Toast.LENGTH_SHORT).show();
                                    goMainActivity();
                                }
                            });

                        }else{
                            Toast.makeText(LoginActivity.this,"Error en Inicio de Sesion", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean validate() {
        boolean valid = true;

        String email = eCorreo.getText().toString();
        String password = eContrasena.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eCorreo.setError("enter a valid email address");
            valid = false;
        } else {
            eCorreo.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            eContrasena.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            eContrasena.setError(null);
        }

        return valid;
    }

}
