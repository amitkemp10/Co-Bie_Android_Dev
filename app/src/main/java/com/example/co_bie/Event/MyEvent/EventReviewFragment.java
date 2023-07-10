package com.example.co_bie.Event.MyEvent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EventReviewFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private Bundle data;
    private EventReviewAdapter eventReviewAdapter;
    private String event_id;
    private String event_type;
    private Event event;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    private Button btnAddReview;

    public EventReviewFragment(Bundle data){
        this.data = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onResume() {
        super.onResume();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        rootView = inflater.inflate(R.layout.fragment_event_review, container, false);
        btnAddReview = rootView.findViewById(R.id.add_review_button);
        btnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnClickAddReview();
            }
        });

        recyclerView = rootView.findViewById(R.id.rv_event_reviews);
        if (data != null) {
            event_id = data.getString("event_id");
            event_type = data.getString("event_type");
        }
        getEventInfo();
        return rootView;
    }

    private void getEventInfo() {
        refDatabase = database.getReference("events").child(event_type).child(event_id);
        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    if (event_type.equals("Physical_Events"))
                        event = snapshot.getValue(PhysicalEvent.class);
                    else event = snapshot.getValue(VirtualEvent.class);
                eventReviewAdapter = new EventReviewAdapter(getContext(), event);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(eventReviewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void handleOnClickAddReview(){
        AddReviewDialogFragment addReviewDialogFragment = new AddReviewDialogFragment(event, refDatabase, eventReviewAdapter);
        addReviewDialogFragment.show(getChildFragmentManager(), "");
    }
}