package com.example.nelson.proyectofinal;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nelson.proyectofinal.Adapters.UsersAdapter;
import com.example.nelson.proyectofinal.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    /// para el adapter creado

    private ArrayList<User> listaUsuarios;
    private RecyclerView resultlist;
    private RecyclerView.Adapter adapterUsuarios;
    private RecyclerView.LayoutManager layoutManager;

    //database
    private DatabaseReference mUserDatabaseReference;
    private FirebaseDatabase mUserDatabase;



    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_users, container, false);
        resultlist = (RecyclerView) vista.findViewById(R.id.users_result_list);
        resultlist.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        resultlist.setLayoutManager(layoutManager);

        listaUsuarios =new ArrayList<>();

        adapterUsuarios = new UsersAdapter(listaUsuarios,getContext());
        resultlist.setAdapter(adapterUsuarios);

        mUserDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference =mUserDatabase.getReference();
        mUserDatabaseReference.keepSynced(true);



        mUserDatabaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaUsuarios.clear();

                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        User usuarios = snapshot.getValue(User.class);
                        listaUsuarios.add(usuarios);
                        //Log.d("Users id","ID: "+usuarios.getUid());
                    }
                    adapterUsuarios.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return vista;

    }


}
