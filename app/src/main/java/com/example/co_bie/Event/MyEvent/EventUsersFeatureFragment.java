package com.example.co_bie.Event.MyEvent;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.co_bie.Event.Date;
import com.example.co_bie.Event.Duration;
import com.example.co_bie.Event.Time;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.Event.Virtual.VirtualEventAdapter;
import com.example.co_bie.FireBaseQueries;
import com.example.co_bie.Hobby.Hobby;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventUsersFeatureFragment extends Fragment implements EventUsersFeatureAdapter.OnDeleteListener {

    Context mContext;
    HashMap<String, User> mUsers;
    RecyclerView mRecyclerView;
    EventUsersFeatureAdapter eventUsersFeatureAdapter;
    FragmentManager fm;
    View rootView;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    String event_id, event_type, code;
    Bundle data;

    public EventUsersFeatureFragment(Bundle data) {
        this.data = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_event_users_feature, container, false);

        database = FirebaseDatabase.getInstance();
        if (data != null) {
            event_id = data.getString("event_id");
            event_type = data.getString("event_type");
            code = data.getString("code");
        }
        mRecyclerView = rootView.findViewById(R.id.rv_event_users_feature);
        mUsers = new HashMap<>();
        eventUsersFeatureAdapter = new EventUsersFeatureAdapter(getContext(), mUsers, event_id, event_type, code);
        eventUsersFeatureAdapter.setOnDeleteListener(this);
        addEventUsers();
        fm = getActivity().getSupportFragmentManager();

        // Inflate the layout for this fragment
        return rootView;
    }

    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().findViewById(R.id.activity_event).setBackground(getActivity().getResources().getDrawable(R.drawable.main_bg));
            getActivity().findViewById(R.id.lay_title).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.lay_sec_title).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.lay_third_title).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.lay_forth_title).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.lay_five_title).setVisibility(View.VISIBLE);
        }
    }

    private void addEventUsers() {
        refDatabase = database.getReference("events").child(event_type).child(event_id).child("participants");
        refDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    mUsers.put(ds.getKey(), user);
                }
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(eventUsersFeatureAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public void onDelete(int size) {
        ((EventActivity) getActivity()).event_participants.setText(size + " participants");
    }
}