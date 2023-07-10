package com.example.co_bie.Event.MyEvent;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.co_bie.Chat.ChatActivity;
import com.example.co_bie.Chat.ChatAdapter;
import com.example.co_bie.Chat.Chats;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Utils;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.Notification.FCMSend;
import com.example.co_bie.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventGroupChatFeatureFragment extends Fragment {

    Context mContext;
    FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    View rootView;
    private TextView tvName;
    private EditText edMsg;
    private ImageView sendBtn;
    private CircleImageView eventProfilePic;
    String msg, my_id, event_id, event_type;
    List<Chats> chatsList;
    ChatAdapter chatAdapter;
    RecyclerView recyclerView;
    Bundle data;
    User user;
    Event event;
    private static boolean isChatActivityForeground = false;

    @Override
    public void onPause() {
        super.onPause();
        setIsChatActivityForeground(false);
    }

    public static void setIsChatActivityForeground(boolean value) {
        isChatActivityForeground = value;
    }

    public static boolean getIsChatActivityForeground() {
        return isChatActivityForeground;
    }

    public EventGroupChatFeatureFragment(Bundle data) {
        this.data = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        setIsChatActivityForeground(true);
        if (getActivity() != null) {
            getActivity().findViewById(R.id.activity_event).setBackground(null);
            getActivity().findViewById(R.id.lay_title).setVisibility(View.GONE);
            getActivity().findViewById(R.id.lay_sec_title).setVisibility(View.GONE);
            getActivity().findViewById(R.id.lay_third_title).setVisibility(View.GONE);
            getActivity().findViewById(R.id.lay_forth_title).setVisibility(View.GONE);
            getActivity().findViewById(R.id.lay_five_title).setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_event_group_chat_feature, container, false);

        tvName = rootView.findViewById(R.id.event_name_chat);
        edMsg = rootView.findViewById(R.id.ed_msg_chat);
        sendBtn = rootView.findViewById(R.id.send_btn_chat);
        eventProfilePic = rootView.findViewById(R.id.event_image_chat);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        my_id = mAuth.getUid();
        if (data != null) {
            event_id = data.getString("event_id");
            event_type = data.getString("event_type");
        }
        getEventInfo();

        recyclerView = rootView.findViewById(R.id.rv_group_chat);
        LinearLayoutManager layoutManger = new LinearLayoutManager(mContext);
        layoutManger.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManger);

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
                sendMsg(event_id, my_id, msg);
                edMsg.setText(" ");
                sendMsgGroupNotification(my_id, event_id, event_type, msg);
            }
        });

        readMsg(event_id);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void sendMsgGroupNotification(String my_id, String event_id, String event_type, String msg) {
        DatabaseReference usersRef = database.getReference("users");
        DatabaseReference myRef = database.getReference("users").child(my_id);
        DatabaseReference eventRef = database.getReference("events").child(event_type).child(event_id);

        Task<DataSnapshot> usersTask = usersRef.get();
        Task<DataSnapshot> myTask = myRef.get();
        Task<DataSnapshot> eventTask = eventRef.get();


        Tasks.whenAll(usersTask, myTask, eventTask).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot userSnapshot = myTask.getResult();
                User user = userSnapshot.getValue(User.class);
                String userKey = userSnapshot.getKey();
                String senderName = user.getFull_name();

                Event new_event;
                DataSnapshot eventSnapshot = eventTask.getResult();
                if (event_type.equals("Physical_Events"))
                    new_event = eventSnapshot.getValue(PhysicalEvent.class);
                else new_event = eventSnapshot.getValue(VirtualEvent.class);

                DataSnapshot usersSnapshot = usersTask.getResult();


                for (String u : new_event.getParticipants().keySet())
                    if (!u.equals(userKey)) {
                        String token = usersSnapshot.child(u).child("fcmToken").getValue(String.class);
                        String type = "New_Msg_Group";
                        String title = new_event.getEventName() + " Event";
                        String message = senderName + ": " + msg;
                        Context context = getContext();
                        if (event instanceof PhysicalEvent)
                            FCMSend.pushNotifications(context, token, title, message, type, event, ((PhysicalEvent) new_event).getLocation().getName(), user);
                        else
                            FCMSend.pushNotifications(context, token, title, message, type, event, ((VirtualEvent) new_event).getEventPlatform().toString(), user);

                    }
            } else {
                // Handle the error
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    private void getEventInfo() {
        refDatabase = database.getReference("events").child(event_type).child(event_id);
        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) if (event_type.equals("Physical_Events"))
                    event = snapshot.getValue(PhysicalEvent.class);
                else event = snapshot.getValue(VirtualEvent.class);
                tvName.setText(event.getEventName());
                if (event.getEvent_img() != null)
                    Picasso.get().load(event.getEvent_img()).into(eventProfilePic);
                else eventProfilePic.setImageResource(R.drawable.default_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMsg(String event_id) {
        chatsList = new ArrayList<>();
        refDatabase = database.getReference("group_chats").child(event_id);
        refDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    chatsList.add(ds.getValue(Chats.class));
                    chatAdapter = new ChatAdapter(getActivity(), chatsList, 2);
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMsg(String event_id, String my_id, String msg) {
        HashMap<String, Object> hashMap = new HashMap<>();
        refDatabase = database.getReference("users").child(my_id);
        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) user = snapshot.getValue(User.class);
                hashMap.put("sender", my_id);
                hashMap.put("sender_name", user.getFull_name());
                hashMap.put("receiver", null);
                hashMap.put("message", msg);

                refDatabase = database.getReference("group_chats");
                refDatabase.child(event_id).push().setValue(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}