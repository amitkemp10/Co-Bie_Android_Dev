package com.example.co_bie;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.co_bie.Event.Date;
import com.example.co_bie.Event.Duration;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.Physical.Location;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Time;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.Hobby.Hobby;
import com.example.co_bie.LoginAndRegistration.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FireBaseQueries {

    FirebaseAuth mAuth;
    FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    private ArrayList<VirtualEvent> ve_return;

    public interface MyVirtualEventsCallback {
        void onCallback(ArrayList<VirtualEvent> veList);
    }

    public interface MyPhysicalEventsCallback {
        void onCallback(ArrayList<PhysicalEvent> peList);
    }

    public interface MyVirtualEventsCallbackParticipate {
        void onCallback(ArrayList<VirtualEvent> veList);
    }

    public interface MyPhysicalEventsCallbackParticipate {
        void onCallback(ArrayList<PhysicalEvent> peList);
    }

    public interface getAllUsersWithSameEventHobbyCallback {
        void onCallback(HashMap<String, User> matchUsers);
    }

    public interface getFilterUsersExistsInEventCallback {
        void onCallback(HashMap<String, User> filterUsers);
    }

    public interface getUserByUUIDCallback {
        void onCallback(User user);
    }


    public FireBaseQueries() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
    }

    public ArrayList<VirtualEvent> getMyVirtualEventsManage(final MyVirtualEventsCallback callback) {
        ArrayList<VirtualEvent> veList = new ArrayList<>();
        refDatabase = database.getReference("events").child("Virtual_Events");
        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    VirtualEvent ve = ds.getValue(VirtualEvent.class);
                    if (ve.getManagerUid().equals(user.getUid()))
                        veList.add(new VirtualEvent(ve.getEventName(), new Date(ve.getEventDate().getYear(), ve.getEventDate().getMonth(), ve.getEventDate().getDay()), new Time(ve.getEventTime().getHour(), ve.getEventTime().getMinute()), new Duration(ve.getEventDuration().getHours(), ve.getEventDuration().getMinutes()), ve.getEventDescription(), ve.getParticipants(), new Hobby(ve.getHobby().getHobby_name(), ve.getHobby().getImg_hobby()), ve.getManagerUid(), null, ve.getEventPlatform(), ve.getMeetingLink(), ve.getID()));
                }
                callback.onCallback(veList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return veList;
    }

    public ArrayList<PhysicalEvent> getMyPhysicalEventsManage(final MyPhysicalEventsCallback callback) {
        ArrayList<PhysicalEvent> peList = new ArrayList<>();
        refDatabase = database.getReference("events").child("Physical_Events");
        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PhysicalEvent pe = ds.getValue(PhysicalEvent.class);
                    if (pe.getManagerUid().equals(user.getUid()))
                        peList.add(new PhysicalEvent(pe.getEventName(), new Date(pe.getEventDate().getYear(), pe.getEventDate().getMonth(), pe.getEventDate().getDay()), new Time(pe.getEventTime().getHour(), pe.getEventTime().getMinute()), new Duration(pe.getEventDuration().getHours(), pe.getEventDuration().getMinutes()), pe.getEventDescription(), pe.getParticipants(), new Hobby(pe.getHobby().getHobby_name(), pe.getHobby().getImg_hobby()), pe.getManagerUid(), null, new Location(pe.getLocation().getLatitude(), pe.getLocation().getLongitude(), pe.getLocation().getName()), pe.getID()));
                }
                callback.onCallback(peList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return peList;
    }

    public ArrayList<VirtualEvent> getMyVirtualEventsParticipate(final MyVirtualEventsCallbackParticipate callback) {
        ArrayList<VirtualEvent> veList = new ArrayList<>();
        refDatabase = database.getReference("events").child("Virtual_Events");
        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    VirtualEvent ve = ds.getValue(VirtualEvent.class);
                    if (ve.getParticipants() != null)
                        if (!ve.getManagerUid().equals(user.getUid()) && ve.getParticipants().containsKey(user.getUid()))
                            veList.add(new VirtualEvent(ve.getEventName(), new Date(ve.getEventDate().getYear(), ve.getEventDate().getMonth(), ve.getEventDate().getDay()), new Time(ve.getEventTime().getHour(), ve.getEventTime().getMinute()), new Duration(ve.getEventDuration().getHours(), ve.getEventDuration().getMinutes()), ve.getEventDescription(), ve.getParticipants(), new Hobby(ve.getHobby().getHobby_name(), ve.getHobby().getImg_hobby()), ve.getManagerUid(), null, ve.getEventPlatform(), ve.getMeetingLink(), ve.getID()));
                }
                callback.onCallback(veList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return veList;
    }

    public ArrayList<PhysicalEvent> getMyPhysicalEventsParticipate(final MyPhysicalEventsCallbackParticipate callback) {
        ArrayList<PhysicalEvent> peList = new ArrayList<>();
        refDatabase = database.getReference("events").child("Physical_Events");
        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PhysicalEvent pe = ds.getValue(PhysicalEvent.class);
                    if (pe.getParticipants() != null)
                        if (!pe.getManagerUid().equals(user.getUid()) && pe.getParticipants().containsKey(user.getUid()))
                            peList.add(new PhysicalEvent(pe.getEventName(), new Date(pe.getEventDate().getYear(), pe.getEventDate().getMonth(), pe.getEventDate().getDay()), new Time(pe.getEventTime().getHour(), pe.getEventTime().getMinute()), new Duration(pe.getEventDuration().getHours(), pe.getEventDuration().getMinutes()), pe.getEventDescription(), pe.getParticipants(), new Hobby(pe.getHobby().getHobby_name(), pe.getHobby().getImg_hobby()), pe.getManagerUid(), null, new Location(pe.getLocation().getLatitude(), pe.getLocation().getLongitude(), pe.getLocation().getName()), pe.getID()));
                }
                callback.onCallback(peList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return peList;
    }

    public void getUserByUUID(String userId, getUserByUUIDCallback listener) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = databaseRef.child("users");

        Query userQuery = usersRef.orderByKey().equalTo(userId);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(userId).getValue(User.class);
                listener.onCallback(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public HashMap<String, User> getAllUsersWithSameEventHobby(String hobbyName, getAllUsersWithSameEventHobbyCallback listener) {
        HashMap<String, User> matchUsers = new HashMap<>();
        refDatabase = database.getReference("users");
        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User u = ds.getValue(User.class);
                    for (Hobby h : u.getHobbiesList())
                        if (h.getHobby_name().equals(hobbyName)) matchUsers.put(ds.getKey(), u);
                }
                listener.onCallback(matchUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return matchUsers;
    }

    public void getFilterUsersExistsInEvent(HashMap<String, User> mUsers, String virtual_physical, String event_id, getFilterUsersExistsInEventCallback listener) {
        if (mUsers != null) {
            HashMap<String, User> filterUsers = new HashMap<>();
            refDatabase = database.getReference("events").child(virtual_physical).child(event_id);
            refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (virtual_physical.equals("Physical_Events")) {
                            PhysicalEvent e = snapshot.getValue(PhysicalEvent.class);
                            for (String u : mUsers.keySet())
                                if (!e.getParticipants().containsKey(u)) {
                                    filterUsers.put(u, mUsers.get(u));
                                }
                        } else if (virtual_physical.equals("Virtual_Events")) {
                            VirtualEvent e = snapshot.getValue(VirtualEvent.class);
                            for (String u : mUsers.keySet())
                                if (!e.getParticipants().containsKey(u))
                                    filterUsers.put(u, mUsers.get(u));
                        }
                        listener.onCallback(filterUsers);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }


    public String getCurrentUserUUID() {
        return user.getUid();
    }
}
