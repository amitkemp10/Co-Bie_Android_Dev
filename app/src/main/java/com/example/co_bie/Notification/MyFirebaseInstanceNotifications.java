package com.example.co_bie.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.co_bie.Chat.ChatActivity;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.MyEvent.EventActivity;
import com.example.co_bie.Event.MyEvent.EventGroupChatFeatureFragment;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.HomePage.MainActivity;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class MyFirebaseInstanceNotifications extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "MyFirebaseInstanceNotifications";
    Intent intent;
    Bundle extras;
    String eventJson;
    PendingIntent pendingIntent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (ChatActivity.getIsChatActivityForeground()) return;

        if (EventGroupChatFeatureFragment.getIsChatActivityForeground()) return;

        if (remoteMessage.getData().isEmpty()) return;

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");
        String type = remoteMessage.getData().get("type");
        String virtual_physical = remoteMessage.getData().get("virtual_physical");
        Gson gsonEvent = new Gson();
        Event event;
        if (virtual_physical.equals("Physical_Events")) {
            event = gsonEvent.fromJson(remoteMessage.getData().get("event"), PhysicalEvent.class);
        } else {
            event = gsonEvent.fromJson(remoteMessage.getData().get("event"), VirtualEvent.class);
        }
        Gson gsonUser = new Gson();
        User user;
        user = gsonUser.fromJson(remoteMessage.getData().get("user"), User.class);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "MyFirebaseInstanceNotifications", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
        NotificationCompat.Builder builder;

        switch (type) {
            case "New_Invitation":
                builder = openFriendRequest(title, message, type, virtual_physical, gsonEvent, event);
                break;
            case "New_Join":
                builder = openUserJoined(title, message, virtual_physical, event);
                break;
            case "New_Msg":
                builder = openUserChat(title, message, user, virtual_physical);
                break;
            case "New_Msg_Group":
                builder = openGroupChat(title, message, type, virtual_physical, event);
                break;
            case "Event_Reminder":
                builder = openSpecificEvent(title, message, virtual_physical, event);
                break;
            default:
                builder = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(title).setContentText(message).setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true);
                break;
        }

        // Show the notification
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public NotificationCompat.Builder openGroupChat(String title, String message, String type, String virtual_physical, Event event) {
        NotificationCompat.Builder builder;
        intent = new Intent(this, EventActivity.class);
        intent.putExtra("type", type);
        if (FirebaseAuth.getInstance().getUid().equals(event.getManagerUid()))
            intent.putExtra("code", "1");
        else intent.putExtra("code", "2");
        intent.putExtra("event_title", event.getEventName());
        intent.putExtra("event_date", event.getEventDate().toString());
        intent.putExtra("event_time", event.getEventTime().toString());
        intent.putExtra("event_description", event.getEventDescription());
        intent.putExtra("event_participants", event.getParticipants().size() + " participants");
        intent.putExtra("event_hobby", event.getHobby().getHobby_name());
        intent.putExtra("event_platform_location", virtual_physical);
        intent.putExtra("event_id", event.getID());
        intent.putExtra("event_manager", event.getManagerUid());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int uniqueId = (int) System.currentTimeMillis();
        pendingIntent = PendingIntent.getActivity(this, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(title).setSmallIcon(R.drawable.notification_toast_cb_logo).setContentText(message).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);
        return builder;
    }

    public NotificationCompat.Builder openSpecificEvent(String title, String message, String virtual_physical, Event event) {
        NotificationCompat.Builder builder;
        intent = new Intent(this, EventActivity.class);
        if (FirebaseAuth.getInstance().getUid().equals(event.getManagerUid()))
            intent.putExtra("code", "1");
        else intent.putExtra("code", "2");
        intent.putExtra("event_title", event.getEventName());
        intent.putExtra("event_date", event.getEventDate().toString());
        intent.putExtra("event_time", event.getEventTime().toString());
        intent.putExtra("event_description", event.getEventDescription());
        intent.putExtra("event_participants", event.getParticipants().size() + " participants");
        intent.putExtra("event_hobby", event.getHobby().getHobby_name());
        intent.putExtra("event_platform_location", virtual_physical);
        intent.putExtra("event_id", event.getID());
        intent.putExtra("event_manager", event.getManagerUid());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int uniqueId = (int) System.currentTimeMillis();
        pendingIntent = PendingIntent.getActivity(this, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.notification_toast_cb_logo).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);
        return builder;
    }

    @NonNull
    private NotificationCompat.Builder openUserChat(String title, String message, User user, String friendKey) {
        NotificationCompat.Builder builder;
        intent = new Intent(this, ChatActivity.class);
        intent.putExtra("friend_id", friendKey);
        intent.putExtra("friend_username", user.getFull_name());
        intent.putExtra("gender", user.getGender());
        intent.putExtra("friend_image", user.getProfile_img());
        intent.putExtra("status", user.getStatus());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int uniqueId = (int) System.currentTimeMillis();
        pendingIntent = PendingIntent.getActivity(this, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.notification_toast_cb_logo).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);
        return builder;
    }

    @NonNull
    private NotificationCompat.Builder openUserJoined(String title, String message, String virtual_physical, Event event) {
        NotificationCompat.Builder builder;
        intent = new Intent(this, EventActivity.class);
        intent.putExtra("code", "1");
        intent.putExtra("event_title", event.getEventName());
        intent.putExtra("event_date", event.getEventDate().toString());
        intent.putExtra("event_time", event.getEventTime().toString());
        intent.putExtra("event_description", event.getEventDescription());
        intent.putExtra("event_participants", event.getParticipants().size() + " participants");
        intent.putExtra("event_hobby", event.getHobby().getHobby_name());
        intent.putExtra("event_platform_location", virtual_physical);
        intent.putExtra("event_id", event.getID());
        intent.putExtra("event_manager", event.getManagerUid());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int uniqueId = (int) System.currentTimeMillis();
        pendingIntent = PendingIntent.getActivity(this, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.notification_toast_cb_logo).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);
        return builder;
    }

    @NonNull
    private NotificationCompat.Builder openFriendRequest(String title, String message, String type, String virtual_physical, Gson gson, Event event) {
        NotificationCompat.Builder builder;
        intent = new Intent(this, MainActivity.class);
        extras = new Bundle();
        eventJson = gson.toJson(event);
        extras.putString("event", eventJson);
        extras.putString("virtual_physical", virtual_physical);
        extras.putString("type", type);
        intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int uniqueId = (int) System.currentTimeMillis();
        pendingIntent = PendingIntent.getActivity(this, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.notification_toast_cb_logo).setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);
        return builder;
    }

}


