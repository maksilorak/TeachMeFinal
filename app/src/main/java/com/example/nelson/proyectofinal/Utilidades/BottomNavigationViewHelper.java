package com.example.nelson.proyectofinal.Utilidades;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.example.nelson.proyectofinal.MainDosActivity;
import com.example.nelson.proyectofinal.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }


    public static void enableNavigation(final Context context, BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_house:
                        Intent intent1 = new Intent(context, MainDosActivity.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        break;

                        //pARA LOS DEMAS CASOS
                        // https://github.com/mitchtabian/Android-Instagram-Clone/blob/b42ec4471f1a63c8d6463783b23ca558c12381c4/app/src/main/java/tabian/com/instagramclone/Utils/BottomNavigationViewHelper.java
                }

                return false;
            }
        });
    }
}
