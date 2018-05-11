package com.example.nelson.proyectofinal;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nelson.proyectofinal.Adapters.FriendsAdapter;
import com.example.nelson.proyectofinal.Model.Friends;
import com.example.nelson.proyectofinal.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    /// para el adapter creado

    private ArrayList<Friends> listaAmigos;
    private RecyclerView friendsLists;
    private RecyclerView.Adapter adapterAmigos;
    private RecyclerView.LayoutManager layoutManager;



    //database
    private DatabaseReference mFriendsDatabaseReference;
    private FirebaseDatabase mFriendsDatabase;
    private FirebaseAuth mAuth;

    String online_user_id;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View myMainView= inflater.inflate(R.layout.fragment_friends, container, false);


        friendsLists = (RecyclerView) myMainView.findViewById(R.id.friends_result_list);
        friendsLists.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        friendsLists.setLayoutManager(layoutManager);

        listaAmigos =new ArrayList<>();

        adapterAmigos = new FriendsAdapter(listaAmigos,getContext());
        friendsLists.setAdapter(adapterAmigos);

        mAuth=FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance();
        //Referencia a la base de datos de amigos.
        mFriendsDatabaseReference =mFriendsDatabase.getReference().child("Friends").child(online_user_id);
        mFriendsDatabaseReference.keepSynced(true);


        mFriendsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaAmigos.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Friends amigos = snapshot.getValue(Friends.class);
                        listaAmigos.add(amigos);
                        //Log.d("Users id","ID: "+amigos.getUid());
                    }
                    adapterAmigos.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return myMainView;
    }

}
