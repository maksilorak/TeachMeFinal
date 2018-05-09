package com.example.nelson.proyectofinal.Adapters;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nelson.proyectofinal.Model.User;
import com.example.nelson.proyectofinal.ProfileActivity;
import com.example.nelson.proyectofinal.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{

    private ArrayList<User> listaUsuarios;
    private int resource;
    private Activity activity;


    public UsersAdapter(ArrayList<User> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public UsersAdapter(ArrayList<User> listaUsuarios, int resource, Activity activity) {
        this.listaUsuarios = listaUsuarios;
        this.resource = resource;
        this.activity = activity;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(resource,parent,false);
        return new UsersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {

        final User users = listaUsuarios.get(position);
        holder.setUsuarios(users,activity);
        final String name = users.getFullname();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idUsuario=users.getUid();
                Intent profile = new Intent(activity,ProfileActivity.class);
                profile.putExtra("user",idUsuario);
                Log.d("Dato enviado","user: "+idUsuario);
                activity.startActivity(profile);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        TextView username,status_search;
        CircleImageView image;
        String UID;

        public UsersViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.name_search);
            status_search = (TextView) itemView.findViewById(R.id.status_search);
            image = (CircleImageView) itemView.findViewById(R.id.photo_search);
        }


        public void setUsuarios(User usuarios, Activity activity){
            username.setText(usuarios.getFullname());
            status_search.setText(usuarios.getStatus());
            Picasso.get().load(usuarios.getProfileimage()).into(image);

        }

    }
}
