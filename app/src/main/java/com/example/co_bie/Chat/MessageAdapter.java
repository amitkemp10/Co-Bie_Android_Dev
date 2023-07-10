package com.example.co_bie.Chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.co_bie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private final List<MessageList> messageList;
    private final Context context;
    private DatabaseReference refDatabase;
    FirebaseUser user;
    private FirebaseDatabase database;
    FirebaseAuth mAuth;
    String theLastMsg;

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
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.template_msg, null));

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
        lastMsg(list.getId(), holder.lastMsg, position);

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("friend_id", list.getId());
                intent.putExtra("friend_username", list.getName());
                intent.putExtra("gender", list.getGender());
                intent.putExtra("friend_image", list.getProfileImg());
                intent.putExtra("status", list.getStatus());
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
        private LinearLayout rootLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.contacts_img_chat);
            name = itemView.findViewById(R.id.full_name_chat);
            lastMsg = itemView.findViewById(R.id.last_message_chat);
            rootLayout = itemView.findViewById(R.id.root_layout_chat);
        }
    }

    private void lastMsg(String friendId, TextView lastMsg, int position) {

        theLastMsg = "default";
        lastMsg.setTextColor(Color.parseColor("#8692f7"));
        refDatabase = database.getReference("chats");
        refDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chats chats = ds.getValue(Chats.class);
                    if (user != null && chats != null)
                        if (chats.getSender().equals(friendId) && chats.getReceiver().equals(user.getUid()) || chats.getSender().equals(user.getUid()) && chats.getReceiver().equals(friendId))
                            theLastMsg = chats.getMessage();
                }

                switch (theLastMsg) {
                    case "default":
                        messageList.get(position);
                        messageList.remove(position);
                        notifyItemRemoved(position);
                        break;
                    default:
                        lastMsg.setText(theLastMsg);
                }

                theLastMsg = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
