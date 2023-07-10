package com.example.co_bie.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.co_bie.Event.Date;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Time;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.FireBaseQueries;
import com.example.co_bie.R;
import com.google.android.datatransport.cct.internal.LogEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EventReminderService extends Service {
    private long INTERVAL = 15 * 1000;
    private String title, message, type, token;
    private FireBaseQueries fireBaseQueries;
    private boolean virtualEventsCompleted = false;
    private boolean physicalEventsCompleted = false;
    private List<Event> myEventsList;
    private Handler mHandler;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            getEventsHappeningSoon();
            mHandler.postDelayed(this, INTERVAL);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        fireBaseQueries = new FireBaseQueries();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myEventsList = new ArrayList<>();
        mHandler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Show notification that the service is running
        String channelId = "my_channel";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId).setSmallIcon(R.drawable.notification_toast_cb_logo).setContentTitle("Event Reminder Service").setContentText("The service is running in the background").setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Event Reminder Service", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(1, builder.build());

        // Start scheduling reminders
        mHandler.postDelayed(mRunnable, INTERVAL);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getEventsHappeningSoon() {
        addEvents();
    }

    private void addEvents() {
        myEventsList.clear();
        fireBaseQueries.getMyVirtualEventsParticipate(new FireBaseQueries.MyVirtualEventsCallbackParticipate() {
            @Override
            public void onCallback(ArrayList<VirtualEvent> veList) {
                myEventsList.clear();
                myEventsList.addAll(veList);
                virtualEventsCompleted = true;
                checkIfFinished();
            }
        });

        fireBaseQueries.getMyPhysicalEventsParticipate(new FireBaseQueries.MyPhysicalEventsCallbackParticipate() {
            @Override
            public void onCallback(ArrayList<PhysicalEvent> peList) {
                myEventsList.clear();
                myEventsList.addAll(peList);
                physicalEventsCompleted = true;
                checkIfFinished();
            }
        });
    }

    private void checkIfFinished() {
        if (virtualEventsCompleted && physicalEventsCompleted) {
            for (Event event : myEventsList) {
                if (checkIfSameDate(event.getEventDate())) {
                    if (checkIfHappeningSoon(event.getEventTime())) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("fcmToken");
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                token = dataSnapshot.getValue(String.class);
                                Log.d("TAG", "Token: " + token);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("TAG", databaseError.getMessage()); // Handle error
                            }
                        };
                        userRef.addValueEventListener(valueEventListener);
                        type = "Event_Reminder";
                        title = event.getEventName() + " event";
                        message = "This event is just around the corner, less than an hour away!";
                        Context context = this;
                        if (event instanceof VirtualEvent)
                            FCMSend.pushNotifications(context, token, title, message, type, event, ((VirtualEvent) event).getEventPlatform().toString(), null);
                        else
                            FCMSend.pushNotifications(context, token, title, message, type, event, ((PhysicalEvent) event).getLocation().getName(), null);
                    }
                }
            }
        }
    }


    private boolean checkIfSameDate(Date eventDate) {
        LocalDate today = LocalDate.now();
        if (eventDate.getDay() == today.getDayOfMonth() && eventDate.getMonth() == today.getMonth().getValue() && eventDate.getYear() == today.getYear()) {
            return true;
        }
        return false;
    }

    private boolean checkIfHappeningSoon(Time eventTime) {
        int event_min, event_hour, total_in_min, current, diff;
        LocalTime currentTime = LocalTime.now();
        event_min = eventTime.getMinute();
        event_hour = eventTime.getHour() * 60;
        total_in_min = event_hour + event_min;
        current = currentTime.getHour() * 60 + currentTime.getMinute();
        diff = total_in_min - current;
        if (diff == 60) return true;
        return false;
    }
}
