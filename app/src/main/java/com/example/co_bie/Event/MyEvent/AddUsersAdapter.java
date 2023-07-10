package com.example.co_bie.Event.MyEvent;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.co_bie.CustomToast;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.Notification.FCMSend;
import com.example.co_bie.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class AddUsersAdapter extends RecyclerView.Adapter<AddUsersAdapter.RecyclerAdapterViewHolder> {

    Context mContext;
    private int selected_index = RecyclerView.NO_POSITION;
    FirebaseAuth mAuth;
    HashMap<String, User> mUsers;
    List<User> userList;
    String event_title;
    String event_id;
    String virtual_physical;
    String event_manager;
    List<String> userKeyList;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    private Event event;

    public AddUsersAdapter(Context mContext, HashMap<String, User> mUsers, String event_title, String event_id, String virtual_physical, String event_manager) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.event_title = event_title;
        this.event_id = event_id;
        this.virtual_physical = virtual_physical;
        this.event_manager = event_manager;
        database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public AddUsersAdapter.RecyclerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View template_user = LayoutInflater.from(mContext).inflate(R.layout.template_users_list, parent, false);
        AddUsersAdapter.RecyclerAdapterViewHolder recyclerAdapterViewHolder = new AddUsersAdapter.RecyclerAdapterViewHolder(template_user);
        return recyclerAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddUsersAdapter.RecyclerAdapterViewHolder holder, int position) {
        userList = new ArrayList<>(mUsers.values());
        userKeyList = new ArrayList<>(mUsers.keySet());
        User user = userList.get(position);
        String userKey = userKeyList.get(position);
        holder.fillData(user, userKey, mContext);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_full_name, tv_age;
        private ImageView img_user, iv_delete, iv_report;
        private String keyUser;
        private User user;

        public RecyclerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            tv_full_name = itemView.findViewById(R.id.full_name_chat);
            tv_age = itemView.findViewById(R.id.last_message_chat);
            img_user = itemView.findViewById(R.id.contacts_img_chat);
            iv_delete = itemView.findViewById(R.id.iv_delete_user);
            iv_delete.setOnClickListener(this);
            iv_report = itemView.findViewById(R.id.iv_report_user);

            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFriendRequestNotification(keyUser, event_manager);
                    iv_delete.setImageResource(R.drawable.ic_done);
                    CustomToast.makeText(mContext, "Friend request sent").show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((Activity) mContext).onBackPressed();
                        }
                    }, 2500);
                }
            });
        }

        private void sendFriendRequestNotification(String keyUser, String event_manager_id) {
            DatabaseReference userRef = database.getReference("users").child(keyUser);
            DatabaseReference eventRef = database.getReference("events").child(virtual_physical).child(event_id);
            DatabaseReference managerRef = database.getReference("users").child(event_manager_id);

            Task<DataSnapshot> userTask = userRef.get();
            Task<DataSnapshot> eventTask = eventRef.get();
            Task<DataSnapshot> managerTask = managerRef.get();

            Tasks.whenAll(userTask, eventTask, managerTask).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot userSnapshot = userTask.getResult();
                    String token = userSnapshot.child("fcmToken").getValue(String.class);
                    String type = "New_Invitation";

                    DataSnapshot eventSnapshot = eventTask.getResult();
                    if (virtual_physical.equals("Physical_Events")) {
                        PhysicalEvent e = eventSnapshot.getValue(PhysicalEvent.class);
                        event = e;
                    } else {
                        VirtualEvent e = eventSnapshot.getValue(VirtualEvent.class);
                        event = e;
                    }

                    DataSnapshot managerSnapshot = managerTask.getResult();
                    User manager = managerSnapshot.getValue(User.class);
                    String managerName = manager.getFull_name();

                    String title = "Invitation to " + event_title;
                    String message = "You have a new invitation from " + managerName;
                    Context context = mContext;
                    FCMSend.pushNotifications(context, token, title, message, type, event, virtual_physical, null);
                } else {
                    // Handle the error
                }
            });
        }


        public void fillData(final User user, final String keyUser, Context mContext) {
            this.user = user;
            this.keyUser = keyUser;
            iv_delete.setImageResource(R.drawable.ic_send_friend_request);
            iv_delete.setVisibility(View.VISIBLE);
            iv_report.setVisibility(View.GONE);
            tv_full_name.setText(user.getFull_name());
            tv_age.setText("Age: " + getAge(user.getBirth_date()));
            if (!user.getProfile_img().equals(""))
                Picasso.get().load(user.getProfile_img()).into(img_user);
            else if (user.getGender().equals("Male")) img_user.setImageResource(R.drawable.ic_boy);
            else img_user.setImageResource(R.drawable.ic_girl);

        }

        private String getAge(String birth_date) {
            String[] birth_array = birth_date.split("/");
            Calendar now = Calendar.getInstance();
            int curr_year = now.get(Calendar.YEAR);
            return String.valueOf(curr_year - Integer.parseInt(birth_array[2]));
        }

        @Override
        public void onClick(View view) {
            return;
        }
    }
}
