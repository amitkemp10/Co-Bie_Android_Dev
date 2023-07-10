package com.example.co_bie.Event.MyEvent;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.co_bie.FireBaseQueries;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AddUsersDialogFragment extends DialogFragment {

    View rootView;
    private FireBaseQueries fq;
    RecyclerView mRecyclerView;
    TextView tv_no_valid_users;
    HashMap<String, User> mUsers;
    String virtual_physical, event_id, event_hobby, event_title, event_manager;

    public AddUsersDialogFragment(String event_hobby, String virtual_physical, String event_id, String event_title, String event_manager) {
        this.event_id = event_id;
        this.event_title = event_title;
        this.virtual_physical = virtual_physical;
        this.event_hobby = event_hobby;
        this.event_manager = event_manager;
        fq = new FireBaseQueries();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_users_dialog, container, false);
        mRecyclerView = rootView.findViewById(R.id.rv_add_users);
        tv_no_valid_users = rootView.findViewById(R.id.tv_no_valid);
        // Inflate the layout for this fragment
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Call the matchUsersWithHobby method to fetch all users with the same hobby as the event
        matchUsersWithHobby(event_hobby, new OnMatchUsersCallback() {
            @Override
            public void onMatchUsers(HashMap<String, User> users) {
                // Call the filterUsersExistsInEvent method to filter out users who are already participating in the event
                filterUsersExistsInEvent(users, virtual_physical, event_id, new OnFilterUsersCallback() {
                    @Override
                    public void onFilterUsers(HashMap<String, User> filteredUsers) {
                        // Set the mUsers ArrayList to the filtered users list
                        mUsers = filteredUsers;
                        // Show the RecyclerView with the filtered users or the "No valid users" message
                        if (mUsers == null || mUsers.size() == 0) {
                            tv_no_valid_users.setVisibility(View.VISIBLE);
                        } else {
                            AddUsersAdapter addUsersAdapter = new AddUsersAdapter(getContext(), mUsers, event_title, event_id, virtual_physical, event_manager);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            mRecyclerView.setAdapter(addUsersAdapter);
                        }
                    }
                });
            }
        });

        return rootView;
    }
;
    private void matchUsersWithHobby(String event_hobby, OnMatchUsersCallback callback) {
        fq.getAllUsersWithSameEventHobby(event_hobby, new FireBaseQueries.getAllUsersWithSameEventHobbyCallback() {
            @Override
            public void onCallback(HashMap<String, User> matchUsers) {
                if (callback != null) {
                    callback.onMatchUsers(matchUsers);
                }
            }
        });
    }

    private void filterUsersExistsInEvent(HashMap<String, User> mUsers, String virtual_physical, String event_id, OnFilterUsersCallback callback) {
        fq.getFilterUsersExistsInEvent(mUsers, virtual_physical, event_id, new FireBaseQueries.getFilterUsersExistsInEventCallback() {
            @Override
            public void onCallback(HashMap<String, User> filterUsers) {
                if (callback != null) {
                    callback.onFilterUsers(filterUsers);
                }
            }
        });
    }

    // Callback interface for the matchUsersWithHobby method
    public interface OnMatchUsersCallback {
        void onMatchUsers(HashMap<String, User> users);
    }

    // Callback interface for the filterUsersExistsInEvent method
    public interface OnFilterUsersCallback {
        void onFilterUsers(HashMap<String, User> filteredUsers);
    }

}
