package com.example.nelson.proyectofinal;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference usersRef, postsRef;
    //FirebaseRecyclerAdapter <Posts,postsViewHolder> usersRecyclerAdapter;

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
    private TabLayout mTabLayout;
    private TabsPageAdapter mTabsPagerAdapter;



    String currenUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializar();

        currenUserID = firebaseAuth.getCurrentUser().getUid();
        //usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        // Tabs for Main Activity
        mViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        mTabsPagerAdapter = new TabsPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        //Toolbar

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("TeachMe");

        //addNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);


        //drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        //drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        //actionBarDrawerToggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        //navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //View navigation_view = navigationView.inflateHeaderView(R.layout.navigation_header);
        //navProfileImage = (CircleImageView) navigation_view.findViewById(R.id.nav_profile_image);
        //navProfileUserName = (TextView) navigation_view.findViewById(R.id.nav_user_full_name);



        //postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        //postList.setHasFixedSize(true);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        //postList.setLayoutManager(linearLayoutManager);





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawerlayout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:
                cerrarSesion();

            case R.id.profile:
                goToProfile();


        }

        return super.onOptionsItemSelected(item);
    }

    private void goMaps() {
        Intent maps = new Intent(MainActivity.this,MapsActivity.class);
        startActivity(maps);
        finish();
    }

    private void sendUserToPostActivity() {
        Intent post = new Intent(MainActivity.this,PostActivity.class);
        startActivity(post);
        finish();
    }

    private void goToPost() {
        Intent post = new Intent(MainActivity.this,PostActivity.class);
        startActivity(post);
        finish();
    }

    private void goToProfile() {
        Intent profile = new Intent(MainActivity.this,ProfileUI.class);
        profile.putExtra("user",currenUserID);
        startActivity(profile);

    }

    private void sendUserToMessageActivity() {

    }

    private void goToSeearchFriends() {
       Intent friends = new Intent(MainActivity.this,UsersFragment.class);
       startActivity(friends);
       finish();
    }



    private void goSetup() {
        Intent setup = new Intent(MainActivity.this,SetupActivity.class);
        startActivity(setup);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usersRef.child("online").setValue("false");
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

                    usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(currenUserID);

                    usersRef.child("online").setValue("true");
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


        usersRef.child("online").setValue("false");

        firebaseAuth.signOut();

        if (Auth.GoogleSignInApi != null){
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()){
                        usersRef.child("online").setValue("false");
                        goLoginActivity();
                    }
                }
            });
        }

        if (LoginManager.getInstance() != null){
            LoginManager.getInstance().logOut();
            usersRef.child("online").setValue("false");
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

}
