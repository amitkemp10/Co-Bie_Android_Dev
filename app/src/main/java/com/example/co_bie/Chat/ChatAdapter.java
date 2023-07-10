package com.example.co_bie.Chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.co_bie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private DatabaseReference refDatabase;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private Context context;
    private List<Chats> chatsList;
    private int id;
    private HashMap<String, String> userColors; // to store the color of each user

    public static final int Msg_Right = 0; //Me
    public static final int Msg_Left = 1; //Friend
    String color;

    public ChatAdapter(Context context, List<Chats> chatsList, int id) {
        this.context = context;
        this.chatsList = chatsList;
        this.id = id;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userColors = new HashMap<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Msg_Right) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MyViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chats chats = chatsList.get(position);
        holder.msgText.setText(chats.getMessage());
        if (id == 2) {
            holder.sender_name.setText(chats.getSender_name());
            String senderId = chats.getSender();
            String senderColor = userColors.get(senderId);
            if (senderColor != null) {
                holder.sender_name.setTextColor(Color.parseColor(senderColor));
            } else {
                setColorForUser(holder.sender_name, senderId);
            }
        } else {
            holder.sender_name.setVisibility(View.GONE);
        }
    }


    private void setColorForUser(TextView sender_name, String senderId) {
        refDatabase = database.getReference("color_chats").child(senderId);
        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String color = snapshot.getValue(String.class);
                    sender_name.setTextColor(Color.parseColor(color));
                    userColors.put(senderId, color); // store the color in the HashMap
                } else {
                    color = generateRandomColor();
                    while (userColors.containsValue(color)) color = generateRandomColor();
                    refDatabase.setValue(color).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sender_name.setTextColor(Color.parseColor(color));
                                userColors.put(senderId, color); // store the color in the HashMap
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private String generateRandomColor() {
        Random random = new Random();
        float hue = random.nextFloat() * 360;
        float saturation = 0.5f;
        float value = 0.7f;
        int color = Color.HSVToColor(new float[]{hue, saturation, value});
        return String.format("#%06X", (0xFFFFFF & color));
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sender_name;
        TextView msgText;
        LinearLayout linearLayoutMe, linearLayoutFriend;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            msgText = itemView.findViewById(R.id.show_msg);
            sender_name = itemView.findViewById(R.id.show_msg_sender_name);
            linearLayoutMe = itemView.findViewById(R.id.my_design);
            linearLayoutFriend = itemView.findViewById(R.id.friend_design);
        }
    }

    public int getItemViewType(int position) {
        assert user != null;
        if (chatsList.get(position).getSender().equals(user.getUid())) return Msg_Right;
        return Msg_Left;
    }

}
