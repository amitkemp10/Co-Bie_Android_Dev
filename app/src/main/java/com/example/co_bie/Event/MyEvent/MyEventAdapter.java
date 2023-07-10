package com.example.co_bie.Event.MyEvent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.co_bie.CustomToast;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.MyEvent.Manage.ManageFragment;
import com.example.co_bie.Event.MyEvent.Participate.ParticipateFragment;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Utils;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.RecyclerAdapterViewHolder> {

    Context context;
    List<Event> eventsList;
    int code;

    public MyEventAdapter(Context context, List<Event> eventsList, int code) {
        this.context = context;
        this.eventsList = eventsList;
        this.code = code;
    }

    @NonNull
    @Override
    public MyEventAdapter.RecyclerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View template_manage_hobbie = LayoutInflater.from(context).inflate(R.layout.template_virtual_hobby, parent, false);
        MyEventAdapter.RecyclerAdapterViewHolder recyclerAdapterViewHolder = new MyEventAdapter.RecyclerAdapterViewHolder(template_manage_hobbie);
        Collections.sort(eventsList, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                LocalDate localDate1 = LocalDate.of(event1.getEventDate().getYear(), event1.getEventDate().getMonth(), event1.getEventDate().getDay());
                LocalDate localDate2 = LocalDate.of(event2.getEventDate().getYear(), event2.getEventDate().getMonth(), event2.getEventDate().getDay());
                return localDate2.compareTo(localDate1);
            }
        });
        return recyclerAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyEventAdapter.RecyclerAdapterViewHolder holder, int position) {
        Event myEvent = eventsList.get(position);
        if (myEvent instanceof VirtualEvent)
            holder.fillDataVirtual((VirtualEvent) myEvent, context);
        else holder.fillDataPhysical((PhysicalEvent) myEvent, context);

    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }


    public class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_title, tv_date, tv_time, tv_description, tv_participants, tv_finish;
        private ImageView img_hobby, img_platform, img_remove_event;
        private String event_description, event_participants, event_time, event_date, event_title, event_platform_location, event_hobby, event_id, event_manager;

        public RecyclerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_title = itemView.findViewById(R.id.tv_title_virtual);
            tv_date = itemView.findViewById(R.id.tv_date_virtual);
            tv_time = itemView.findViewById(R.id.tv_time_virtual);
            tv_description = itemView.findViewById(R.id.tv_description_virtual);
            tv_participants = itemView.findViewById(R.id.tv_participants_virtual);
            img_hobby = itemView.findViewById(R.id.img_virtual_hobbie);
            img_platform = itemView.findViewById(R.id.img_virtual_platform);
            img_remove_event = itemView.findViewById(R.id.img_remove_event);
            tv_finish = itemView.findViewById(R.id.tv_finish);
        }

        public void fillDataVirtual(final VirtualEvent myEvent, Context context) {
            SpannableString content = new SpannableString(myEvent.getEventName());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            tv_title.setText(content);
            if (code == 1) img_remove_event.setImageResource(R.drawable.ic_del_event);
            event_title = myEvent.getEventName();
            event_date = myEvent.getEventDate().toString();
            event_manager = myEvent.getManagerUid();
            tv_date.setText(event_date);
            event_id = myEvent.getID();
            event_time = myEvent.getEventTime().toString();
            tv_time.setText(event_time);
            event_description = myEvent.getEventDescription();
            tv_description.setText(event_description);
            if (myEvent.getParticipants() != null)
                event_participants = String.valueOf(myEvent.getParticipants().size());
            else event_participants = "0";
            tv_participants.setText(event_participants + " participants");
            event_platform_location = myEvent.getEventPlatform().toString();
            img_platform.setImageResource(Utils.appropriatePlatformImage(myEvent.getEventPlatform().toString()));
            img_platform.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar snackbar = Snackbar.make(view, myEvent.getEventPlatform().toString() + " Platform", Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snackbar.dismiss();
                        }
                    }, 2000);
                }
            });
            img_remove_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogRemoveEvent(context);
                }
            });
            event_hobby = myEvent.getHobby().getHobby_name();
            img_hobby.setImageResource(Utils.appropriateImage(event_hobby));
            if (myEvent.isPassed()) {
                tv_finish.setVisibility(View.VISIBLE);
            }
        }

        private void dialogRemoveEvent(Context context) {
            View view;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete event  👥");
            builder.setMessage("\nAre you sure you want to delete " + tv_title.getText() + " event?");

            // Create Dialog variable
            Dialog dialog = builder.create();

            // Inflate custom layout for buttons
            view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_buttons, null);
            ((AlertDialog) dialog).setView(view);

            // Get references to buttons
            Button positiveButton = view.findViewById(R.id.dialog_button_positive);
            Button negativeButton = view.findViewById(R.id.dialog_button_negative);

            // Set click listeners for buttons
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Event myEvent = eventsList.get(position);
                    FirebaseDatabase.getInstance().getReference().child("events").child(myEvent instanceof VirtualEvent ? "Virtual_Events" : "Physical_Events").child(myEvent.getID()).removeValue();
                    eventsList.remove(position);
                    notifyItemRemoved(position);
                    dialog.dismiss();
                    CustomToast.makeText(context, "Event removed successfully").show();
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

        public void fillDataPhysical(final PhysicalEvent myEvent, Context context) {
            SpannableString content = new SpannableString(myEvent.getEventName());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            tv_title.setText(content);
            if (code == 1) img_remove_event.setImageResource(R.drawable.ic_del_event);
            event_title = myEvent.getEventName();
            event_date = myEvent.getEventDate().toString();
            event_time = myEvent.getEventTime().toString();
            event_manager = myEvent.getManagerUid();
            event_description = myEvent.getEventDescription();
            if (myEvent.getParticipants() != null)
                event_participants = String.valueOf(myEvent.getParticipants().size());
            else event_participants = "0";
            event_platform_location = myEvent.getLocation().getName();
            event_id = myEvent.getID();
            event_hobby = myEvent.getHobby().getHobby_name();
            tv_date.setText(event_date);
            tv_time.setText(event_time);
            tv_description.setText(event_description);
            tv_participants.setText(event_participants + " participants");
            img_platform.setImageResource(R.drawable.ic_gps);
            img_platform.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar snackbar = Snackbar.make(view, myEvent.getLocation().getName(), Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snackbar.dismiss();
                        }
                    }, 2000);
                }
            });
            img_remove_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogRemoveEvent(context);
                }
            });
            img_hobby.setImageResource(Utils.appropriateImage(event_hobby));
            if (myEvent.isPassed()) {
                tv_finish.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, EventActivity.class);
            intent.putExtra("code", String.valueOf(code));
            intent.putExtra("event_title", event_title);
            intent.putExtra("event_date", event_date);
            intent.putExtra("event_time", event_time);
            intent.putExtra("event_description", event_description);
            intent.putExtra("event_participants", event_participants + " participants");
            intent.putExtra("event_hobby", event_hobby);
            intent.putExtra("event_platform_location", event_platform_location);
            intent.putExtra("event_id", event_id);
            intent.putExtra("event_manager", event_manager);
            context.startActivity(intent);
        }
    }
}



