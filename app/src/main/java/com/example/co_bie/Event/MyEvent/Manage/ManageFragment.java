package com.example.co_bie.Event.MyEvent.Manage;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.MyEvent.MyEventAdapter;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.FireBaseQueries;
import com.example.co_bie.R;

import java.util.ArrayList;
import java.util.List;

public class ManageFragment extends Fragment {

    Context mContext;
    List<Event> myEventsList;
    RecyclerView mRecyclerView;
    MyEventAdapter myEventAdapter;
    FragmentManager fm;
    View rootView;
    FireBaseQueries fireBaseQueries;
    private boolean virtualEventsCompleted = false;
    private boolean physicalEventsCompleted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        addEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fireBaseQueries = new FireBaseQueries();
        rootView = inflater.inflate(R.layout.fragment_manage, container, false);

        mRecyclerView = rootView.findViewById(R.id.rv_manage_events);
        myEventsList = new ArrayList<>();
        myEventAdapter = new MyEventAdapter(getContext(), myEventsList, 1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fm = getActivity().getSupportFragmentManager();
        return rootView;
    }

    private void addEvents() {
        myEventsList.clear();

        fireBaseQueries.getMyVirtualEventsManage(new FireBaseQueries.MyVirtualEventsCallback() {
            @Override
            public void onCallback(ArrayList<VirtualEvent> veList) {
                myEventsList.addAll(veList);
                virtualEventsCompleted = true;
                checkAndUpdateAdapter();
            }
        });

        fireBaseQueries.getMyPhysicalEventsManage(new FireBaseQueries.MyPhysicalEventsCallback() {
            @Override
            public void onCallback(ArrayList<PhysicalEvent> peList) {
                myEventsList.addAll(peList);
                physicalEventsCompleted = true;
                checkAndUpdateAdapter();
                mRecyclerView.setAdapter(myEventAdapter);
            }
        });
    }

    private void checkAndUpdateAdapter() {
        if (virtualEventsCompleted && physicalEventsCompleted) {
            myEventAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(myEventAdapter);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }
}
