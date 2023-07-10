package com.example.co_bie.Event.MyEvent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.co_bie.Chat.ChatActivity;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventUsersFeatureAdapter extends RecyclerView.Adapter<EventUsersFeatureAdapter.RecyclerAdapterViewHolder> {

    Context mContext;
    HashMap<String, User> mUsers;
    List<User> userList;
    List<String> userKeyList;
    private int selected_index = RecyclerView.NO_POSITION;
    String event_id, event_type, code;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    FirebaseAuth mAuth;
    List<String> reported_by;

    private OnDeleteListener mListener;

    public interface OnDeleteListener {
        void onDelete(int size);
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        mListener = listener;
    }

    public EventUsersFeatureAdapter(Context mContext, HashMap<String, User> mUsers, String event_id, String event_type, String code) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.event_id = event_id;
        this.event_type = event_type;
        this.code = code;
        database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public EventUsersFeatureAdapter.RecyclerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View template_user = LayoutInflater.from(mContext).inflate(R.layout.template_users_list, parent, false);
        EventUsersFeatureAdapter.RecyclerAdapterViewHolder recyclerAdapterViewHolder = new EventUsersFeatureAdapter.RecyclerAdapterViewHolder(template_user);
        return recyclerAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventUsersFeatureAdapter.RecyclerAdapterViewHolder holder, int position) {
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
        private ImageView img_user, iv_delete, iv_report, iv_manager;
        private String userKey;
        private User user;

        public RecyclerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mAuth = FirebaseAuth.getInstance();
            tv_full_name = itemView.findViewById(R.id.full_name_chat);
            tv_age = itemView.findViewById(R.id.last_message_chat);
            img_user = itemView.findViewById(R.id.contacts_img_chat);
            iv_delete = itemView.findViewById(R.id.iv_delete_user);
            iv_report = itemView.findViewById(R.id.iv_report_user);
            iv_manager = itemView.findViewById(R.id.iv_manager);
            iv_delete.setOnClickListener(this);
            iv_report.setOnClickListener(this);
            setOnClick(iv_delete, iv_report);
        }

        public void setOnClick(ImageView iv_del, ImageView iv_rep) {
            iv_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteDialog(user.getFull_name());
                }
            });

            iv_rep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (iv_rep.getColorFilter() == null) addReportDialog(user.getFull_name());
                    else cancelReportDialog(user.getFull_name());
                }
            });
        }

        public void showDeleteDialog(String user_full_name) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Delete 👋");
            builder.setMessage("\nAre you sure you want to delete " + user_full_name + " from the event?");

            // Create Dialog variable
            Dialog dialog = builder.create();

            // Inflate custom layout for buttons
            View view = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog_buttons, null);
            ((AlertDialog) dialog).setView(view);

            // Get references to buttons
            Button positiveButton = view.findViewById(R.id.dialog_button_positive);
            Button negativeButton = view.findViewById(R.id.dialog_button_negative);

            // Set click listeners for buttons
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUsers.remove(userKey);
                    removeParticipantFromEvent(event_id, event_type, userKey, mUsers);
                    mListener.onDelete(mUsers.size());
                    dialog.dismiss();
                }
            });
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Create a custom Drawable with rounded corners
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.WHITE);
            gradientDrawable.setCornerRadius(15); // set corner radius here

            // Set the custom Drawable as the background of the AlertDialog window
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(gradientDrawable);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);

            dialog.show();
        }

        public void addReportDialog(String user_full_name) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Report 📄");
            builder.setMessage("\nAre you sure you want to report about " + user_full_name + "?");

            // Create Dialog variable
            Dialog dialog = builder.create();

            // Inflate custom layout for buttons
            View view = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog_buttons, null);
            ((AlertDialog) dialog).setView(view);

            // Get references to buttons
            Button positiveButton = view.findViewById(R.id.dialog_button_positive);
            Button negativeButton = view.findViewById(R.id.dialog_button_negative);

            // Set click listeners for buttons
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_report.setColorFilter(Color.RED);
                    reportAboutUser(userKey, 1);
                    dialog.dismiss();
                }
            });
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Create a custom Drawable with rounded corners
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.WHITE);
            gradientDrawable.setCornerRadius(15); // set corner radius here

            // Set the custom Drawable as the background of the AlertDialog window
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(gradientDrawable);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);

            dialog.show();
        }

        public void cancelReportDialog(String user_full_name) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Report 📄");
            builder.setMessage("\nDo you want to cancel the report about " + user_full_name + "?");

            // Create Dialog variable
            Dialog dialog = builder.create();

            // Inflate custom layout for buttons
            View view = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog_buttons, null);
            ((AlertDialog) dialog).setView(view);

            // Get references to buttons
            Button positiveButton = view.findViewById(R.id.dialog_button_positive);
            Button negativeButton = view.findViewById(R.id.dialog_button_negative);

            // Set click listeners for buttons
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_report.setColorFilter(null);
                    reportAboutUser(userKey, 2);
                    dialog.dismiss();
                }
            });
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Create a custom Drawable with rounded corners
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.WHITE);
            gradientDrawable.setCornerRadius(15); // set corner radius here

            // Set the custom Drawable as the background of the AlertDialog window
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(gradientDrawable);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);

            dialog.show();
        }

        public void fillData(final User user, final String userKey, Context mContext) {
            iv_report.setColorFilter(null);
            this.userKey = userKey;
            this.user = user;
            tv_full_name.setText(user.getFull_name());
            tv_age.setText("Age: " + getAge(user.getBirth_date()));
            if (!user.getProfile_img().equals(""))
                Picasso.get().load(user.getProfile_img()).into(img_user);
            else if (user.getGender().equals("Male")) img_user.setImageResource(R.drawable.ic_boy);
            else img_user.setImageResource(R.drawable.ic_girl);
            reportAboutUser(userKey, 3);
            iv_manager.setVisibility(View.GONE);
            if (userKey.equals(mAuth.getCurrentUser().getUid())) {
                iv_delete.setVisibility(View.GONE);
                iv_report.setVisibility(View.GONE);
                tv_full_name.setText(user.getFull_name());
            }
            checkIfManager(iv_manager);
            if (code.equals("2")) iv_delete.setVisibility(View.GONE);
        }

        private void checkIfManager(ImageView iv_manager) {
            refDatabase = database.getReference("events").child(event_type).child(event_id).child("managerUid");
            refDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) if (snapshot.getValue(String.class).equals(userKey))
                        iv_manager.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        private String getAge(String birth_date) {
            String[] birth_array = birth_date.split("/");
            Calendar now = Calendar.getInstance();
            int curr_year = now.get(Calendar.YEAR);
            return String.valueOf(curr_year - Integer.parseInt(birth_array[2]));
        }

        @Override
        public void onClick(View view) {
            if (userKey.equals(mAuth.getCurrentUser().getUid())) return;
            selected_index = getLayoutPosition();
            notifyItemChanged(selected_index);
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("friend_id", userKey);
            intent.putExtra("friend_username", user.getFull_name());
            intent.putExtra("gender", user.getGender());
            intent.putExtra("friend_image", user.getProfile_img());
            intent.putExtra("status", user.getStatus());
            mContext.startActivity(intent);
            //add code when selected.
        }

        private void removeParticipantFromEvent(String event_id, String locationPlatformName, String uid, HashMap<String, User> mUsers) {
            database.getReference("events").child(locationPlatformName).child(event_id).child("participants").child(uid).removeValue();
            database.getReference("events").child(locationPlatformName).child(event_id).child("participants").setValue(mUsers);
        }

        private void reportAboutUser(String userKey, int id) {
            refDatabase = database.getReference("users").child(userKey);
            switch (id) {
                case 1:
                    refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                reported_by = snapshot.getValue(User.class).getReported_by();
                                if (reported_by != null && !reported_by.contains(mAuth.getCurrentUser())) {
                                    reported_by.add(mAuth.getCurrentUser().getUid());
                                    refDatabase.child("reported_by").setValue(reported_by);
                                } else {
                                    List<String> newList = new ArrayList<>();
                                    newList.add(mAuth.getCurrentUser().getUid());
                                    refDatabase.child("reported_by").setValue(newList);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    break;
                case 2:
                    refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                reported_by = snapshot.getValue(User.class).getReported_by();
                                if (reported_by.contains(mAuth.getCurrentUser().getUid())) {
                                    reported_by.remove(mAuth.getCurrentUser().getUid());
                                    refDatabase.child("reported_by").setValue(reported_by);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    break;
                case 3:
                    refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                reported_by = snapshot.getValue(User.class).getReported_by();
                                if (reported_by != null && reported_by.contains(mAuth.getCurrentUser().getUid()))
                                    iv_report.setColorFilter(Color.RED);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    break;
            }
        }
    }
}
