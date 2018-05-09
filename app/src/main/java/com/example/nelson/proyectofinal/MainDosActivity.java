package com.example.nelson.proyectofinal;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.nelson.proyectofinal.Adapters.SectionsPagerAdapter;
import com.example.nelson.proyectofinal.Utilidades.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainDosActivity extends AppCompatActivity {


    private static final String TAG = "HomeActivity";
    private Context mContext = MainDosActivity.this;
    private static final int ACTIVITY_NUM=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dos);

        setupBottomNavigationView();
        setupViewPager();
    }

    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UsersFragment());
        adapter.addFragment(new RequestsFragment());
        adapter.addFragment(new FriendsFragment());
        adapter.addFragment(new ChatsFragment());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Users");
        tabLayout.getTabAt(1).setText("Requests");
        tabLayout.getTabAt(2).setText("Friends");
        tabLayout.getTabAt(3).setText("Chats");
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
