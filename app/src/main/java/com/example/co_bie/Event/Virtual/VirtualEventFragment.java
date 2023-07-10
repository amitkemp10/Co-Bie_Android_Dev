package com.example.co_bie.Event.Virtual;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.co_bie.Event.Date;
import com.example.co_bie.Event.Duration;
import com.example.co_bie.Event.Time;
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


public class VirtualEventFragment extends Fragment {

    Context mContext;
    List<VirtualEvent> mVirtualEvents;
    RecyclerView mRecyclerView;
    VirtualEventAdapter mVirtualEventAdapter;
    FragmentManager fm;
    View rootView;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    private FireBaseQueries fireBaseQueries;
    private User currUser;
    private Button openOptionsBtn ;
    private Button option1Btn ;
    private Button option2Btn ;
    private Button menuBtn;
    private VirtualEventFragmentListener virtualEventFragmentListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.virtualEventFragmentListener = (VirtualEventFragment.VirtualEventFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'GetEventSelectedLocationListener'");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_virtual_event, container, false);
        database = FirebaseDatabase.getInstance();
        fireBaseQueries = new FireBaseQueries();

        fireBaseQueries.getUserByUUID(fireBaseQueries.getCurrentUserUUID(), new FireBaseQueries.getUserByUUIDCallback() {
            @Override
            public void onCallback(User user) {
                currUser = user;
                mVirtualEvents = new ArrayList<>();
                mVirtualEventAdapter = new VirtualEventAdapter(getContext(), mVirtualEvents);
                addNewEvents();
            }
        });
        mRecyclerView = rootView.findViewById(R.id.rv_virtual_events);
        openOptionsBtn  = rootView.findViewById(R.id.open_options_btn_virtual);
        option1Btn  = rootView.findViewById(R.id.btn_option_1_virtual);
        option2Btn  = rootView.findViewById(R.id.btn_option_2_virtual);
        menuBtn = rootView.findViewById(R.id.btn_drawer_menu_virtual);
        fm = getActivity().getSupportFragmentManager();

        openOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOptionsClick();
            }
        });

        option1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualEventFragmentListener.onClickOptionVirtualFragment(2);
            }
        });

        option2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualEventFragmentListener.onClickOptionVirtualFragment(3);
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                virtualEventFragmentListener.onClickOptionVirtualFragment(4);
            }
        });


        return rootView;
    }

    private void sortEvents(){
        for(int i = 0; i < mVirtualEvents.size(); i++)
            for(int j = 0; j < currUser.getHobbiesList().size(); j++)
        {
            if(mVirtualEvents.get(i).getHobby().getHobby_name().equals(currUser.getHobbiesList().get(j).getHobby_name())){
                VirtualEvent savedEvent = mVirtualEvents.get(i);
                mVirtualEvents.remove(savedEvent);
                mVirtualEvents.add(0, savedEvent);
            }
        }
    }
    private void addNewEvents() {
        refDatabase = database.getReference("events").child("Virtual_Events");
        refDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mVirtualEvents.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    VirtualEvent ve = ds.getValue(VirtualEvent.class);
                    if(!ve.isPassed())
                        mVirtualEvents.add(new VirtualEvent(ve.getEventName(), new Date(ve.getEventDate().getYear(), ve.getEventDate().getMonth(), ve.getEventDate().getDay()), new Time(ve.getEventTime().getHour(), ve.getEventTime().getMinute()), new Duration(ve.getEventDuration().getHours(), ve.getEventDuration().getMinutes()), ve.getEventDescription(), ve.getParticipants(), new Hobby(ve.getHobby().getHobby_name(), ve.getHobby().getImg_hobby()), ve.getManagerUid(), null, ve.getEventPlatform(),ve.getMeetingLink(), ve.getID()));
                }
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                sortEvents();
                mRecyclerView.setAdapter(mVirtualEventAdapter);
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

    private void handleOptionsClick(){
        if (option1Btn.getVisibility() == View.GONE) {
            // Slide up the buttons
            ObjectAnimator openMenuBtnAnimator = ObjectAnimator.ofFloat(openOptionsBtn, "rotation", 0, 45);
            openMenuBtnAnimator.setDuration(500);
            ObjectAnimator option1Animator = ObjectAnimator.ofFloat(option1Btn, "translationY", 0f, -350f);
            option1Animator.setDuration(500);
            option1Animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    option1Btn.setVisibility(View.VISIBLE);
                }
            });
            openMenuBtnAnimator.start();
            option1Animator.start();

            ObjectAnimator option2Animator = ObjectAnimator.ofFloat(option2Btn, "translationY", 0f, -180f);
            option2Animator.setDuration(500);
            option2Animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    option2Btn.setVisibility(View.VISIBLE);
                }
            });
            option2Animator.start();
        } else {
            // Slide down the buttons
            ObjectAnimator openMenuBtnAnimator = ObjectAnimator.ofFloat(openOptionsBtn, "rotation", 45, 0);
            openMenuBtnAnimator.setDuration(500);
            ObjectAnimator option1Animator = ObjectAnimator.ofFloat(option1Btn, "translationY", -350f, 0f);
            option1Animator.setDuration(500);
            option1Animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    option1Btn.setVisibility(View.GONE);
                }
            });
            option1Animator.start();
            openMenuBtnAnimator.start();

            ObjectAnimator option2Animator = ObjectAnimator.ofFloat(option2Btn, "translationY", -180f, 0f);
            option2Animator.setDuration(500);
            option2Animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    option2Btn.setVisibility(View.GONE);
                }
            });
            option2Animator.start();
        }
    }

    public interface VirtualEventFragmentListener {
        public void onClickOptionVirtualFragment(int option);
    }
}