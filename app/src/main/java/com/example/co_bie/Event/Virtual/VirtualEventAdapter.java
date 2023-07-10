package com.example.co_bie.Event.Virtual;

import android.content.Context;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.co_bie.CustomToast;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.JoinEventDialogFragment;
import com.example.co_bie.Event.Utils;
import com.example.co_bie.FireBaseQueries;
import com.example.co_bie.Hobby.Hobby;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;

public class VirtualEventAdapter extends RecyclerView.Adapter<VirtualEventAdapter.RecyclerAdapterViewHolder> {

    Context mContext;
    List<VirtualEvent> mVirtualEventsList;
    private int selected_index = RecyclerView.NO_POSITION;
    private FireBaseQueries fireBaseQueries;
    private User currUser;

    public VirtualEventAdapter(Context mContext, List<VirtualEvent> mVirtualEventsList) {
        this.mContext = mContext;
        this.mVirtualEventsList = mVirtualEventsList;
        fireBaseQueries = new FireBaseQueries();
    }

    @NonNull
    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View template_virtual_hobbie = LayoutInflater.from(mContext).inflate(R.layout.template_virtual_hobby, parent, false);
        RecyclerAdapterViewHolder recyclerAdapterViewHolder = new RecyclerAdapterViewHolder(template_virtual_hobbie);
        return recyclerAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterViewHolder holder, int position) {
        VirtualEvent virtualEvent = mVirtualEventsList.get(position);
        holder.fillData(virtualEvent, mContext);
    }

    @Override
    public int getItemCount() {
        return mVirtualEventsList.size();
    }

    public class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_title, tv_date, tv_time, tv_description, tv_participants;
        private ImageView img_hobbie, img_platform;
        String currUserUUID;

        public RecyclerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            currUserUUID = fireBaseQueries.getCurrentUserUUID();
            tv_title = itemView.findViewById(R.id.tv_title_virtual);
            tv_date = itemView.findViewById(R.id.tv_date_virtual);
            tv_time = itemView.findViewById(R.id.tv_time_virtual);
            tv_description = itemView.findViewById(R.id.tv_description_virtual);
            tv_participants = itemView.findViewById(R.id.tv_participants_virtual);
            img_hobbie = itemView.findViewById(R.id.img_virtual_hobbie);
            img_platform = itemView.findViewById(R.id.img_virtual_platform);
        }

        public void fillData(final VirtualEvent virtualEvent, Context mContext) {
            SpannableString content = new SpannableString(virtualEvent.getEventName());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            if (virtualEvent.getParticipants() == null)
                virtualEvent.setParticipants(new HashMap<>());
            tv_title.setText(content);
            tv_date.setText(virtualEvent.getEventDate().toString());
            tv_time.setText(virtualEvent.getEventTime().toString());
            tv_description.setText(virtualEvent.getEventDescription());
            tv_participants.setText(virtualEvent.getParticipants().size() + " participants");
            img_platform.setImageResource(Utils.appropriatePlatformImage(virtualEvent.getEventPlatform().toString()));
            img_platform.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar snackbar = Snackbar.make(view, virtualEvent.getEventPlatform().toString() + " Platform", Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snackbar.dismiss();
                        }
                    }, 2000);
                }
            });
            img_hobbie.setImageResource(Utils.appropriateImage(virtualEvent.getHobby().getHobby_name()));
        }


        @Override
        public void onClick(View view) {
            selected_index = getLayoutPosition();
            notifyItemChanged(selected_index);
            VirtualEvent virtualEvent = mVirtualEventsList.get(selected_index);
            if (virtualEvent.getManagerUid().equals(currUserUUID)) {
                CustomToast.makeText(mContext, "You manage this event").show();
                return;
            }
            JoinEventDialogFragment dialogFragment = new JoinEventDialogFragment(virtualEvent, Utils.EventType.VIRTUAL);
            FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
            dialogFragment.show(fragmentManager, "join");
        }
    }

}
