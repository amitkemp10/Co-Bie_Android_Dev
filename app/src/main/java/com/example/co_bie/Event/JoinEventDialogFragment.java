package com.example.co_bie.Event;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.co_bie.CustomToast;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.FireBaseQueries;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.Notification.FCMSend;
import com.example.co_bie.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class JoinEventDialogFragment extends DialogFragment {
    private TextView tvEventName;
    private TextView tvPlatform;
    private TextView tvTime;
    private TextView tvDate;
    private TextView tvHobby;
    private TextView tvDuration;
    private TextView tvLink;
    private TextView tvDescription;
    private TextView tvNumParticipants;
    private TextView tvLocation;
    private TextView tvLocationORlinkVal;
    private Event event;
    private Utils.EventType type;
    private LinearLayout platformLayout;
    private Button btnJoinEvent;
    private FireBaseQueries fireBaseQueries;
    private FirebaseDatabase database;
    private DatabaseReference eventRef;
    private String currUserUUID;

    public JoinEventDialogFragment(Event event, Utils.EventType type) {
        this.event = event;
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.dialog_join_event, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        fireBaseQueries = new FireBaseQueries();
        currUserUUID = fireBaseQueries.getCurrentUserUUID();
        tvEventName = view.findViewById(R.id.event_name_title);
        tvPlatform = view.findViewById(R.id.platform_value);
        tvTime = view.findViewById(R.id.time_value);
        tvDate = view.findViewById(R.id.date_value);
        tvHobby = view.findViewById(R.id.hobby_value);
        tvDuration = view.findViewById(R.id.duration_value);
        tvLink = view.findViewById(R.id.link_text);
        tvLocation = view.findViewById(R.id.location_text);
        tvDescription = view.findViewById(R.id.description_value);
        tvNumParticipants = view.findViewById(R.id.num_participants_value);
        tvLocationORlinkVal = view.findViewById(R.id.linkORlocation_value);
        platformLayout = view.findViewById(R.id.platform_layout);
        btnJoinEvent = view.findViewById(R.id.join_event_button);
        fillData();
    }

    private void fillData() {
        tvEventName.setText(event.getEventName());
        tvTime.setText(event.getEventTime().toString());
        tvDate.setText(event.getEventDate().toString());
        tvHobby.setText(event.getHobby().getHobby_name());
        tvDuration.setText(event.getEventDuration().toString());
        tvDescription.setText(event.getEventDescription());

        if (type == Utils.EventType.PHYSICAL) {
            tvLocation.setVisibility(View.VISIBLE);
            tvLink.setVisibility(View.GONE);
            platformLayout.setVisibility(View.GONE);
            PhysicalEvent specificEvent = (PhysicalEvent) event;
            tvLocationORlinkVal.setText(specificEvent.getLocation().getName());
            eventRef = database.getReference("events").child("Physical_Events").child(event.getID());
        } else {
            tvLocation.setVisibility(View.GONE);
            tvLink.setVisibility(View.VISIBLE);
            platformLayout.setVisibility(View.VISIBLE);
            VirtualEvent specificEvent = (VirtualEvent) event;
            tvPlatform.setText(specificEvent.getEventPlatform().toString());
            handleLink((specificEvent.getMeetingLink()));
            eventRef = database.getReference("events").child("Virtual_Events").child(event.getID());
        }

        if (event.getParticipants() == null) {
            HashMap<String, User> newParticipants = new HashMap<>();
            event.setParticipants(newParticipants);
            eventRef.setValue(event);
        }
        tvNumParticipants.setText(String.valueOf(event.getParticipants().size()));

        updateJoinLeaveBtn();
    }

    private void handleLink(String link) {
        if (link.equals("No link")) {
            tvLocationORlinkVal.setText("No Link");
        } else {
            String text = "Click Here";
            SpannableString spannableString = new SpannableString(text);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    // Handle link click
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);
                }
            };
            spannableString.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvLocationORlinkVal.setText(spannableString);
            tvLocationORlinkVal.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void onClickJoinEvent() {

        fireBaseQueries.getUserByUUID(currUserUUID, new FireBaseQueries.getUserByUUIDCallback() {
            @Override
            public void onCallback(User user) {
                HashMap<String, User> newParticipants = new HashMap<>();
                newParticipants = event.getParticipants();
                newParticipants.put(currUserUUID, user);
                event.setParticipants(newParticipants);
                eventRef.child("participants").setValue(newParticipants);
                CustomToast.makeText(getContext(), "Joined Event").show();
                tvNumParticipants.setText(String.valueOf(event.getParticipants().size()));
                updateJoinLeaveBtn();
                sendJoinedNotification(currUserUUID, event.getEventName(), event.getManagerUid());
            }
        });
    }

    private void sendJoinedNotification(String keyUser, String event_name, String keyManager) {
        DatabaseReference userRef = database.getReference("users").child(keyUser);
        DatabaseReference managerRef = database.getReference("users").child(keyManager);

        Task<DataSnapshot> userTask = userRef.get();
        Task<DataSnapshot> managerTask = managerRef.get();

        Tasks.whenAll(userTask, managerTask).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot managerSnapshot = managerTask.getResult();
                String token = managerSnapshot.child("fcmToken").getValue(String.class);
                String type = "New_Join";

                DataSnapshot userSnapshot = userTask.getResult();
                User user = userSnapshot.getValue(User.class);
                String UserName = user.getFull_name();

                String title = "New friend joined your event";
                String message = UserName + " joined " + event_name + " event";
                Context context = getContext();
                if (event instanceof VirtualEvent)
                    FCMSend.pushNotifications(context, token, title, message, type, event, ((VirtualEvent) event).getEventPlatform().toString(), null);
                else
                    FCMSend.pushNotifications(context, token, title, message, type, event, ((PhysicalEvent) event).getLocation().getName(), null);

            } else {
                // Handle the error
            }
        });
    }

    private void onClickLeaveEvent() {
        HashMap<String, User> newParticipants = new HashMap<>();
        newParticipants = event.getParticipants();
        newParticipants.remove(currUserUUID);
        event.setParticipants(newParticipants);
        eventRef.child("participants").setValue(newParticipants);
        CustomToast.makeText(getContext(), "Leaved Event").show();
        tvNumParticipants.setText(String.valueOf(event.getParticipants().size()));
        updateJoinLeaveBtn();
    }

    private void updateJoinLeaveBtn() {
        if (!event.getParticipants().containsKey(currUserUUID)) {
            btnJoinEvent.setText("Join");
            btnJoinEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickJoinEvent();
                }
            });
        } else {
            btnJoinEvent.setText("Leave");
            btnJoinEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickLeaveEvent();
                }
            });
        }
    }
}
