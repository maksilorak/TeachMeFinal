package com.example.nelson.proyectofinal;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nelson.proyectofinal.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {

    private EditText searchField;
    private ImageButton searchButton;

    private RecyclerView resultlist;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");

        searchField = (EditText) findViewById(R.id.search_field);
        searchButton = (ImageButton) findViewById(R.id.imageButton);
        resultlist = (RecyclerView) findViewById(R.id.result_list);
        resultlist.setHasFixedSize(true);
        resultlist.setLayoutManager(new LinearLayoutManager(this));


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchField.getText().toString();
                userSearch(searchText);
            }
        });
    }

    private void userSearch(String searchText ) {

        mUserDatabase.orderByChild("fullname").startAt(searchText).endAt(searchText+ "\uf8ff");
        FirebaseRecyclerAdapter <User,usersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, usersViewHolder>(
                User.class,
                R.layout.list_layout,
                usersViewHolder.class,
                mUserDatabase

        ) {
            @Override
            protected void populateViewHolder(usersViewHolder viewHolder, User model, int position) {
                viewHolder.setFullname(model.getFullname());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setProfileimage(getApplicationContext(),model.getProfileimage());
            }
        };


        resultlist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class usersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public usersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFullname(String fullname) {
            TextView username = (TextView) mView.findViewById(R.id.name_search);
            username.setText(fullname);
        }

        public void setStatus(String status) {
            TextView status_search = (TextView) mView.findViewById(R.id.status_search);
            status_search.setText(status);
        }

        public void setProfileimage(Context ctx, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.photo_search);
            Picasso.with(ctx).load(profileimage).into(image);
        }

    }
}
