package com.example.co_bie;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private final List<MessageList> messageList;
    private final Context context;
    private DatabaseReference refDatabase;
    FirebaseUser user;
    private FirebaseDatabase database;
    FirebaseAuth mAuth;

    public MessageAdapter(Context context, List<MessageList> messageList) {
        this.messageList = messageList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        refDatabase = database.getReference("users");
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.template_message, null));

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
        MessageList list = messageList.get(position);
        if (!list.getProfileImg().equals(""))
            Picasso.get().load(list.getProfileImg()).into(holder.profileImg);
        else if (list.getGender().equals("Male"))
            holder.profileImg.setImageResource(R.drawable.ic_boy);
        else holder.profileImg.setImageResource(R.drawable.ic_girl);
        holder.name.setText(list.getName());
        holder.lastMsg.setText(list.getLastMessage());
        if (list.getUnseenMessages() == 0)
            holder.unseenMsg.setVisibility(View.GONE);
        else holder.unseenMsg.setVisibility(View.VISIBLE);

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("friend_id", list.getId());
                intent.putExtra("friend_username", list.getName());
                intent.putExtra("gender", list.getGender());
                intent.putExtra("friend_image", list.getProfileImg());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImg;
        private TextView name;
        private TextView lastMsg;
        private TextView unseenMsg;
        private LinearLayout rootLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.contacts_img_chat);
            name = itemView.findViewById(R.id.full_name_chat);
            lastMsg = itemView.findViewById(R.id.last_message_chat);
            unseenMsg = itemView.findViewById(R.id.unseenMsg);
            rootLayout = itemView.findViewById(R.id.root_layout_chat);
        }
    }
}
