package com.example.nelson.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.nelson.proyectofinal.Model.Posts;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference usersRef, postsRef;
    FirebaseRecyclerAdapter <Posts,MainActivity.postsViewHolder> firebaseRecyclerAdapter;

    private GoogleApiClient googleApiClient;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private CircleImageView navProfileImage;
    private TextView navProfileUserName;
    private ImageButton addNewPostButton;


    private ViewPager mViewPager;



    String currenUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializar();

        currenUserID = firebaseAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");



        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        addNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navigation_view = navigationView.inflateHeaderView(R.layout.navigation_header);
        navProfileImage = (CircleImageView) navigation_view.findViewById(R.id.nav_profile_image);
        navProfileUserName = (TextView) navigation_view.findViewById(R.id.nav_user_full_name);



        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);



        usersRef.child(currenUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.hasChild("fullname")){
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        navProfileUserName.setText(fullname);
                    }

                    if (dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        //Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(navProfileImage);
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(navProfileImage);
                    }else{
                        Toast.makeText(MainActivity.this,"Profile name does not exists",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                userMenuSelectro(item);
                return false;
            }
        });

        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPostActivity();
            }
        });


        displayAllUsersPosts();
    }

    private void sendUserToPostActivity() {
        Intent post = new Intent(MainActivity.this,PostActivity.class);
        startActivity(post);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void userMenuSelectro(MenuItem item) {
        switch (item.getItemId()){


        }
    }

    private void goToProfile() {
        Intent profile = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(profile);
        finish();
    }

    private void sendUserToMessageActivity() {

    }

    private void goToSeearchFriends() {
       Intent friends = new Intent(MainActivity.this,UsersFragment.class);
       startActivity(friends);
       finish();
    }

    private void sendUserToSettingsActivity() {
        Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settings);
        //finish();
    }

    private void goSetup() {
        Intent setup = new Intent(MainActivity.this,SetupActivity.class);
        startActivity(setup);
        finish();
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
    protected void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
        displayAllUsersPosts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleApiClient.stopAutoManage(this);
        googleApiClient.disconnect();
    }

    private void inicializar() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null){
                    goLoginActivity();
                    Log.d("FirebaseUser: ", "Usuario No Logueado");

                }
                else{

                    Log.d("FirebaseUser: ", "Usuario Logueado");
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

    public void cerrarSesion() {
        firebaseAuth.signOut();
        if (Auth.GoogleSignInApi != null){
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()){
                        goLoginActivity();
                    }
                }
            });
        }

        if (LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
        }
    }

    private void goLoginActivity() {
        Intent login = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(login);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void displayAllUsersPosts() {


    }

    public static class postsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public postsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFullname(String fullname) {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileimage(Context ctx, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            //Picasso.with(ctx).load(profileimage).into(image);
            Picasso.get().load(profileimage).into(image);
        }

        public void setTime(String time) {
            TextView post_time = (TextView) mView.findViewById(R.id.post_time);
            post_time.setText(" "+time);
        }

        public void setDate(String date) {
            TextView post_date = (TextView) mView.findViewById(R.id.post_date);
            post_date.setText(" "+date);
        }

        public void setDescription(String description) {
            TextView post_description = (TextView) mView.findViewById(R.id.post_description);
            post_description.setText(description);
        }

        public void setPostimage(Context ctx, String postImage) {
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            //Picasso.with(ctx).load(postImage).into(post_image);
            Picasso.get().load(postImage).into(post_image);
        }

    }
}
