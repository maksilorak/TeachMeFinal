package com.example.nelson.proyectofinal.Adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nelson.proyectofinal.Model.User;
import com.example.nelson.proyectofinal.ProfileActivity;
import com.example.nelson.proyectofinal.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    private ArrayList<User> listaUsuarios;
    private Context context;

    public UsersAdapter(ArrayList<User> listaUsuarios, Context context) {
        this.listaUsuarios = listaUsuarios;
        this.context = context;
    }

    @NonNull

    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        LayoutInflater inflater =  LayoutInflater.from(context);
        itemView = inflater.inflate(R.layout.list_layout,parent,false);
        return new UsersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final UsersViewHolder holder, final int position) {


        //Llena los datos de todos los usuarios
        holder.username.setText(listaUsuarios.get(position).getFullname());
        holder.status_search.setText(listaUsuarios.get(position).getStatus());
        //With load of the picture in offline conditions
        Picasso.get().load(listaUsuarios.get(position).getProfileimage()).networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(listaUsuarios.get(position).getProfileimage()).into(holder.image);
                    }
                });
        holder.UID.setText(listaUsuarios.get(position).getUid());

        // cuando se presiona se un usuario en particular
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent perfil = new Intent(context, ProfileActivity.class);
                Log.d("OnClicked:  Clicked ON","USUARIO: "+listaUsuarios.get(position).getUid());
                perfil.putExtra("user",listaUsuarios.get(position).getUid());
                Log.d("Dato enviado","USUARIO: "+listaUsuarios.get(position).getUid());
                context.startActivity(perfil);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        TextView username,status_search,UID;
        CircleImageView image;


        public UsersViewHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.name_search);
            status_search = (TextView) itemView.findViewById(R.id.status_search);
            image = (CircleImageView) itemView.findViewById(R.id.photo_search);
            UID = (TextView) itemView.findViewById(R.id.user_id);
        }
    }
}
