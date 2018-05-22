package com.example.nelson.proyectofinal;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nelson.proyectofinal.Adapters.ChatsAdapter;
import com.example.nelson.proyectofinal.Adapters.FriendsAdapter;
import com.example.nelson.proyectofinal.Model.Chats;
import com.example.nelson.proyectofinal.Model.Friends;
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
public class ChatsFragment extends Fragment {

/// para el adapter creado

    private ArrayList<Chats> listaChats;
    private RecyclerView chatsLists;
    private RecyclerView.Adapter adapterChats;
    private RecyclerView.LayoutManager layoutManager;



    //database
    private DatabaseReference mFriendsDatabaseReference;
    private FirebaseDatabase mFriendsDatabase;
    private FirebaseAuth mAuth;

    String online_user_id;



    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myMainView= inflater.inflate(R.layout.fragment_chats, container, false);

        //recycler view del fragment Chat
        chatsLists = (RecyclerView) myMainView.findViewById(R.id.chats_list);
        chatsLists.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        chatsLists.setLayoutManager(layoutManager);

        listaChats =new ArrayList<>();

        adapterChats = new ChatsAdapter(listaChats,getContext());
        chatsLists.setAdapter(adapterChats);

        mAuth=FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance();
        //Referencia a la base de datos de amigos.
        mFriendsDatabaseReference =mFriendsDatabase.getReference().child("Friends").child(online_user_id);
        mFriendsDatabaseReference.keepSynced(true);

        mFriendsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaChats.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Chats chats = snapshot.getValue(Chats.class);
                        listaChats.add(chats);
                        Log.d("Chat Users ID","ID: "+chats.getID_Friend());
                    }
                    adapterChats.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        return myMainView;
    }


}
