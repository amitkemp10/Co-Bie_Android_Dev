package com.example.co_bie.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.Notification.FCMSend;
import com.example.co_bie.Notification.MyFirebaseInstanceNotifications;
import com.example.co_bie.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView tvName;
    private TextView tvStatus;
    private EditText edMsg;
    private ImageView sendBtn;
    private CircleImageView userProfilePic;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    String msg, my_id, friend_id;
    List<Chats> chatsList;
    ChatAdapter chatAdapter;
    RecyclerView recyclerView;
    private static boolean isChatActivityForeground = false;

    @Override
    protected void onResume() {
        super.onResume();
        setIsChatActivityForeground(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setIsChatActivityForeground(false);
    }

    public static void setIsChatActivityForeground(boolean value) {
        isChatActivityForeground = value;
    }

    public static boolean getIsChatActivityForeground() {
        return isChatActivityForeground;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        backBtn = findViewById(R.id.back_btn_chat);
        tvName = findViewById(R.id.username_chat);
        edMsg = findViewById(R.id.ed_msg_chat);
        sendBtn = findViewById(R.id.send_btn_chat);
        userProfilePic = findViewById(R.id.profile_image_chat);
        tvStatus = findViewById(R.id.user_status_chat);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        my_id = user.getUid();

        recyclerView = findViewById(R.id.rv_chat);
        LinearLayoutManager layoutManger = new LinearLayoutManager(this);
        layoutManger.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManger);

        tvName.setText(getIntent().getStringExtra("friend_username"));
        if (!getIntent().getStringExtra("friend_image").equals(""))
            Picasso.get().load(getIntent().getStringExtra("friend_image")).into(userProfilePic);
        else if (getIntent().getStringExtra("gender").equals("Male"))
            userProfilePic.setImageResource(R.drawable.ic_boy);
        else userProfilePic.setImageResource(R.drawable.ic_girl);

        String status = getIntent().getStringExtra("status");
        tvStatus.setText(status);
        if (status.equals("Offline")) tvStatus.setTextColor(Color.RED);

        friend_id = getIntent().getStringExtra("friend_id");

        edMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //check if msg empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = edMsg.getText().toString();
                sendMsg(my_id, friend_id, msg);
                edMsg.setText(" ");
                sendMsgNotification(my_id, friend_id, msg);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        readMsg(my_id, friend_id);
    }

    private void sendMsgNotification(String my_id, String friend_id, String msg) {
        DatabaseReference myRef = database.getReference("users").child(my_id);
        DatabaseReference friendRef = database.getReference("users").child(friend_id);

        Task<DataSnapshot> myTask = myRef.get();
        Task<DataSnapshot> friendTask = friendRef.get();

        Tasks.whenAll(myTask, friendTask).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot userSnapshot = myTask.getResult();
                User user = userSnapshot.getValue(User.class);
                String senderName = user.getFull_name();

                DataSnapshot friendSnapshot = friendTask.getResult();
                String token = friendSnapshot.child("fcmToken").getValue(String.class);
                String type = "New_Msg";


                String title = "New message from " + senderName;
                String message = msg;
                Context context = this;
                FCMSend.pushNotifications(context, token, title, message, type, null, userSnapshot.getKey(), user);

            } else {
                // Handle the error
            }
        });
    }

    private void readMsg(String my_id, String friend_id) {
        chatsList = new ArrayList<>();
        refDatabase = database.getReference("chats");
        refDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chats chats = ds.getValue(Chats.class);
                    if (chats.getSender().equals(my_id) && chats.getReceiver().equals(friend_id) || chats.getSender().equals(friend_id) && chats.getReceiver().equals(my_id))
                        chatsList.add(chats);
                    chatAdapter = new ChatAdapter(ChatActivity.this, chatsList, 1);
                    recyclerView.setAdapter(chatAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMsg(String my_id, String friend_id, String msg) {
        refDatabase = database.getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", my_id);
        hashMap.put("sender_name", null);
        hashMap.put("receiver", friend_id);
        hashMap.put("message", msg);
        refDatabase.child("chats").push().setValue(hashMap);
    }
}