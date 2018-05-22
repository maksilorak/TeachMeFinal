package com.example.nelson.proyectofinal;

import android.content.Context;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nelson.proyectofinal.Adapters.MessagesAdapter;
import com.example.nelson.proyectofinal.Model.Chats;
import com.example.nelson.proyectofinal.Model.Messages;
import com.facebook.internal.PlatformServiceClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId;
    private String messageReceiverName;
    private Toolbar chatToolbar;
    private TextView userNameTitle, userLastSeen;
    private CircleImageView userChatProfileImage;

    private ImageButton sendMessageButton, selectImageButton;
    private EditText inputMessageText;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private String messageSenderId;

    private RecyclerView usersMessagesList;

    private final ArrayList<Messages> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverId = getIntent().getExtras().get("user").toString();
        messageReceiverName = getIntent().getExtras().get("user_name").toString();
        messagesAdapter = new MessagesAdapter(messageList);
        linearLayoutManager = new LinearLayoutManager(this);

        usersMessagesList =(RecyclerView) findViewById(R.id.chats_result_list);
        usersMessagesList.setHasFixedSize(true);
        usersMessagesList.setLayoutManager(linearLayoutManager);
        usersMessagesList.setAdapter(messagesAdapter);

        FetchMessages();

        chatToolbar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater =(LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar = layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar);

        userNameTitle = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        userChatProfileImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        sendMessageButton = (ImageButton) findViewById(R.id.send_message);
        selectImageButton = (ImageButton) findViewById(R.id.select_image);
        inputMessageText = (EditText) findViewById(R.id.input_message);

        userNameTitle.setText(messageReceiverName);


        rootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String online = dataSnapshot.child("online").getValue().toString();
                final String userImageThumb = dataSnapshot.child("profileimage").getValue().toString();


                //With load of the picture in offline conditions
                Picasso.get().load(userImageThumb).networkPolicy(NetworkPolicy.OFFLINE)
                        .into(userChatProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(userImageThumb).into(userChatProfileImage);
                            }
                        });

                if (online.equals("true")){
                    userLastSeen.setText("Online");
                }else{


                    //LastSeenTime getTime = new LastSeenTime();

                    //long last_seen = Long.parseLong(online);
                    //Log.d("LAST SEEN",last_seen +"ago");
                    //String lastSeenDisplayTime = getTime.getTimeAgo(last_seen,getApplicationContext()).toString();
                    //Log.d("Last seen",lastSeenDisplayTime+"ago");

                    userLastSeen.setText("false");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    private void FetchMessages() {
        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messageList.add(messages);
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String messageText = inputMessageText.getText().toString();

        if (TextUtils.isEmpty(messageText)){
            Toast.makeText(ChatActivity.this, "Please write a message", Toast.LENGTH_SHORT).show();
        }else{
            String message_sender_refernce = "Messages/" + messageSenderId + "/" + messageReceiverId;
            String message_receiver_refernce = "Messages/" + messageReceiverId + "/" + messageSenderId;

            DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).push();

            String message_push_id = user_message_key.getKey();

            Map messageBody = new HashMap();
            messageBody.put("message",messageText);
            messageBody.put("seen",false);
            messageBody.put("type","text");
            messageBody.put("time",ServerValue.TIMESTAMP);
            messageBody.put("from",messageSenderId);

            Map messageDetails = new HashMap();
            messageDetails.put(message_sender_refernce + "/" + message_push_id, messageBody);
            messageDetails.put(message_receiver_refernce + "/" + message_push_id, messageBody);

            rootRef.updateChildren(messageDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null){
                        Log.d("Chat Log",databaseError.getMessage().toString());
                    }

                    inputMessageText.setText("");
                }
            });

        }
    }


}
