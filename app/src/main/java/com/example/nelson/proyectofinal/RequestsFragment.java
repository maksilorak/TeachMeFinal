package com.example.nelson.proyectofinal;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nelson.proyectofinal.Adapters.RequestsAdapter;
import com.example.nelson.proyectofinal.Model.Requests;
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
public class RequestsFragment extends Fragment {


    /// para el adapter creado

    private ArrayList<Requests> listaRequest;
    private RecyclerView requestList;
    private RecyclerView.Adapter adapterRequest;


    //database
    private DatabaseReference mFriendsDatabaseReference;
    private FirebaseAuth mAuth;

    String online_user_id;




    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_requests, container, false);


        requestList = (RecyclerView) myView.findViewById(R.id.requests_list);
        requestList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        requestList.setLayoutManager(linearLayoutManager);


        listaRequest =new ArrayList<>();

        adapterRequest = new RequestsAdapter(listaRequest,getContext());
        requestList.setAdapter(adapterRequest);

        mAuth=FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        Log.d("Requests User ID",online_user_id);



        //Referencia a la base de datos de pedidos de amistad.
        mFriendsDatabaseReference =FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(online_user_id);
        mFriendsDatabaseReference.keepSynced(true);



        mFriendsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaRequest.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Requests requests = snapshot.getValue(Requests.class);
                        listaRequest.add(requests);
                        Log.d("Requests ID","ID: "+requests.getID());
                    }
                    adapterRequest.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return myView;
    }

}
