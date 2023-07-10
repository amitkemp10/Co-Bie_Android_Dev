package com.example.co_bie.Chat;

import static java.lang.Thread.sleep;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.co_bie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListFragment extends Fragment {

    private final List<MessageList> messageList = new ArrayList<>();
    MessageAdapter messageAdapter;
    RecyclerView mRecyclerView;
    FragmentManager fm;
    View rootView;
    private FirebaseDatabase database;
    FirebaseAuth mAuth;
    private DatabaseReference refDatabase;
    FirebaseUser user;
    String gender, img_link;
    private Context mContext;
    TextView tv_no_chats;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_message_list, container, false);

        final CircleImageView userProfilePic = rootView.findViewById(R.id.my_profile_img_chat);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        refDatabase = database.getReference("users");

        mRecyclerView = rootView.findViewById(R.id.rv_msg);
        mRecyclerView.setHasFixedSize(true);
        tv_no_chats = rootView.findViewById(R.id.tv_no_chats);

        setAllOtherUsers();


        ProgressDialog pd = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.show();
        setProfileImage(userProfilePic, pd);


        fm = getActivity().getSupportFragmentManager();
        return rootView;
    }

    private void setAllOtherUsers() {
        refDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!user.getUid().equals(dataSnapshot.getKey())) {
                        populateMessageList(dataSnapshot);
                    }
                }
                messageAdapter = new MessageAdapter(getContext(), messageList);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error getting data");
            }
        });
    }

    private void populateMessageList(DataSnapshot dataSnapshot) {
        String getId = dataSnapshot.getKey();
        String getName = dataSnapshot.child("full_name").getValue(String.class);
        String gender = dataSnapshot.child("gender").getValue(String.class);
        String status = dataSnapshot.child("status").getValue(String.class);
        String getProfileImage = dataSnapshot.child("profile_img").getValue(String.class);

        lastMsg(getId).thenAccept(result -> {
            if (result != null && !result.equals("default")) {
                MessageList msg = new MessageList(getId, getName, gender, getProfileImage, result, status);
                messageList.add(msg);
                tv_no_chats.setVisibility(View.GONE);
                messageAdapter.notifyDataSetChanged();
            } else tv_no_chats.setText("There are no available chats");
        });
    }

    private CompletableFuture<String> lastMsg(String friendId) {
        CompletableFuture<String> future = new CompletableFuture<>();

        DatabaseReference chatsRef = database.getReference("chats");
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMsg = "default";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chats chats = ds.getValue(Chats.class);
                    if (user != null && chats != null) {
                        if (chats.getSender().equals(friendId) && chats.getReceiver().equals(user.getUid()) || chats.getSender().equals(user.getUid()) && chats.getReceiver().equals(friendId)) {
                            theLastMsg = chats.getMessage();
                            break;
                        }
                    }
                }
                future.complete(theLastMsg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }


    private void setProfileImage(CircleImageView userProfilePic, ProgressDialog pd) {
        refDatabase.child(user.getUid()).child("profile_img").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    img_link = String.valueOf(task.getResult().getValue());
                    if (!img_link.isEmpty()) {
                        Picasso.get().load(img_link).into(userProfilePic);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        pd.dismiss();
                    } else {
                        setDefaultImage(userProfilePic);
                        try {
                            Thread.sleep(800);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        pd.dismiss();
                    }
                } else Log.e("Firebase", "Error getting data");
            }
        });

    }

    private void setDefaultImage(CircleImageView userProfilePic) {
        refDatabase.child(user.getUid()).child("gender").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    gender = String.valueOf(task.getResult().getValue());
                    if (gender.equals("Male")) userProfilePic.setImageResource(R.drawable.ic_boy);
                    else userProfilePic.setImageResource(R.drawable.ic_girl);
                } else Log.e("Firebase", "Error getting data");
            }
        });
    }
}