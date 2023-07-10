package com.example.co_bie.Event;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.co_bie.CustomToast;
import com.example.co_bie.Event.Physical.Location;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.Hobby.HobbiesActivity;
import com.example.co_bie.Hobby.HobbiesAdapter;
import com.example.co_bie.Hobby.Hobby;
import com.example.co_bie.HomePage.MainActivity;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class CreateEventFragment extends Fragment {
    private TextView tvLocation;
    private TextView tvDate;
    private TextView tvHobby;
    private TextView tvTime;
    private TextView tvDuration;
    private TextView tvPlatform;
    private EditText tvLink;
    private EditText etDescription;
    private EditText etName;
    private Date eventDate;
    private Time eventTime;
    private Duration eventDuration;
    private Hobby eventHobby;
    private Button createEventBtn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    private Location eventLocation;
    private Utils.EventType eventType;
    private Utils.Platform eventPlatform;
    private CreateEventListener createEventListener;
    private SharedViewModel sharedViewModel;
    private TextView pageTitle;
    String location_name;
    String hobbyName;
    int hobbyImg;
    LatLng location;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.createEventListener = (CreateEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " + context.getClass().getName() + " must implements the interface 'CreateEventListener'");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        eventType = handleArgsEventType();
        pageTitle = view.findViewById(R.id.CreateEventText);
        if (eventType == Utils.EventType.PHYSICAL) {
            tvLocation = view.findViewById(R.id.tv_select_location);
            tvLocation.setVisibility(view.VISIBLE);
            pageTitle.setText("Physical Event");

        } else {
            tvPlatform = view.findViewById(R.id.tv_select_platform);
            tvLink = view.findViewById(R.id.tv_meeting_link);
            tvPlatform.setVisibility(view.VISIBLE);
            tvLink.setVisibility(view.VISIBLE);
        }

        database = FirebaseDatabase.getInstance();
        refDatabase = database.getReference("events");
        eventLocation = new Location(0, 0, "");
        etDescription = view.findViewById(R.id.event_descreption);
        tvDate = view.findViewById(R.id.date);
        tvTime = view.findViewById(R.id.time);
        tvHobby = view.findViewById(R.id.hobby);
        etName = view.findViewById(R.id.event_name);
        tvDuration = view.findViewById(R.id.duration);
        createEventBtn = view.findViewById(R.id.create_event_button);
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    private Utils.EventType handleArgsEventType() {
        Bundle args = getArguments();
        return args.getString("EventType") == "Physical" ? Utils.EventType.PHYSICAL : Utils.EventType.VIRTUAL;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel.getLocationBundle().observe(getViewLifecycleOwner(), new Observer<Bundle>() {
            @Override
            public void onChanged(Bundle bundle) {
                // Handle the data received from the NewFragment
                location_name = bundle.getString("selected_location_name");
                location = bundle.getParcelable("selected_location");
                if (eventType == Utils.EventType.PHYSICAL) {
                    tvLocation.setText(location_name);
                    eventLocation.setLatitude(location.latitude);
                    eventLocation.setLongitude(location.longitude);
                    eventLocation.setName(location_name);
                }
                etName.setText(bundle.getString("Name"));
                tvTime.setText(bundle.getString("Time"));
                tvHobby.setText(bundle.getString("HobbyName"));
                tvDate.setText(bundle.getString("Date"));
                tvDuration.setText(bundle.getString("Duration"));
                etDescription.setText(bundle.getString("Description"));
            }
        });

        //Listeners
        tvHobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HobbiesActivity.class);
                intent.putExtra("isCreateEvent", true);
                startActivityForResult(intent, 2);
            }
        });
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker();
            }
        });
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker();
            }
        });
        tvDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DurationPicker();
            }
        });
        if (eventType == Utils.EventType.PHYSICAL) {
            tvLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendResultBack();
                    locationPicker();
                }
            });
        } else {
            tvPlatform.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlatformPicker();
                }
            });
        }
        createEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            hobbyName = data.getStringExtra("SelectedHobbyName");
            hobbyImg = data.getIntExtra("SelectedHobbyImg", 1);
            tvHobby.setText(hobbyName);
            eventHobby = new Hobby(hobbyName, hobbyImg);
        }
    }

    private void sendResultBack() {
        Bundle data = new Bundle();
        data.putString("Name", etName.getText().toString());
        data.putString("Date", tvDate.getText().toString());
        data.putString("Time", tvTime.getText().toString());
        data.putString("Duration", tvDuration.getText().toString());
        data.putString("Description", etDescription.getText().toString());
        data.putString("HobbyName", tvHobby.getText().toString());
        sharedViewModel.setDataFields(data);
    }

    private void DatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        eventDate = new Date(year, month, day);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.DateTimeDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                tvDate.setText(date);
                tvDate.setTextColor(Color.parseColor("#000000"));
                eventDate.setYear(year);
                eventDate.setMonth(month);
                eventDate.setDay(day);
            }
        }, year, month, day);

        // Set the minimum date to the current date
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        dialog.show();
    }

    private void TimePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        eventTime = new Time(hour, minutes);
        TimePickerDialog dialog = new TimePickerDialog(getContext(), R.style.DateTimeDialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                String time = String.format("%02d", hour) + ":" + String.format("%02d", minutes);
                tvTime.setText(time);
                tvTime.setTextColor(Color.parseColor("#000000"));
                eventTime.setHour(hour);
                eventTime.setMinute(minutes);
            }
        }, hour, minutes, true);
        dialog.show();
    }

    private void DurationPicker() {
        int hour = 1;
        int minute = 0;
        eventDuration = new Duration(hour, minute);
        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                String hourStr = (hour < 10) ? String.format("%d", hour) : String.format("%02d", hour);
                String minuteStr = (minute < 10) ? String.format("%d", minute) : String.format("%02d", minute);
                String duration = String.format("%s hours and %s minutes", hourStr, minuteStr);
                tvDuration.setText(duration);
                tvDuration.setTextColor(Color.parseColor("#000000"));
                eventDuration.setHours(hour);
                eventDuration.setMinutes(minute);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.MyTimePickerDialogStyle, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Choose duration");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }


    private void locationPicker() {
        createEventListener.handleCrateEventFragment(1);
    }

    private void PlatformPicker() {
        final String[] platforms = {"ZOOM", "TEAMS", "SKYPE"};

        // Create an AlertDialog with the platform options
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select a platform").setItems(platforms, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Get the selected platform
                String selectedPlatform = platforms[which];
                mapSelectedPlatform(selectedPlatform);
                tvPlatform.setText(selectedPlatform);

            }
        });
        // Show the AlertDialog
        builder.create().show();
    }

    private void mapSelectedPlatform(String strPlatform) {
        Drawable drawable;
        switch (strPlatform) {
            case "ZOOM":
                eventPlatform = Utils.Platform.ZOOM;
                drawable = getResources().getDrawable(R.drawable.ic_zoom);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                tvPlatform.setCompoundDrawables(null, null, drawable, null);
                tvPlatform.setCompoundDrawablePadding(20);
                break;
            case "TEAMS":
                eventPlatform = Utils.Platform.TEAMS;
                drawable = getResources().getDrawable(R.drawable.ic_teams);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                tvPlatform.setCompoundDrawables(null, null, drawable, null);
                tvPlatform.setCompoundDrawablePadding(20);
                break;
            case "SKYPE":
                eventPlatform = Utils.Platform.SKYPE;
                drawable = getResources().getDrawable(R.drawable.ic_skype);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                tvPlatform.setCompoundDrawables(null, null, drawable, null);
                tvPlatform.setCompoundDrawablePadding(20);
                break;
        }
    }

    private void createEvent() {
        Event newEvent = null;
        if (eventType == Utils.EventType.PHYSICAL) {
            if (tvLocation.getText() == "" || etName.getText().toString() == "" || tvHobby.getText() == "" || tvDate.getText() == "" || tvTime.getText() == "" || tvDuration.getText() == "" || TextUtils.isEmpty(etDescription.getText())) {
                CustomToast.makeText(getContext(), "Please fill all the required fields").show();
                return;
            }
            newEvent = new PhysicalEvent(etName.getText().toString(), eventDate, eventTime, eventDuration, etDescription.getText().toString(), new HashMap<String, User>(), eventHobby, mAuth.getCurrentUser().getUid(), null, eventLocation);
        } else {
            if (etName.getText().toString() == "" || tvHobby.getText() == "" || tvPlatform.getText() == "" || TextUtils.isEmpty(tvLink.getText()) || tvDate.getText() == "" || tvTime.getText() == "" || tvDuration.getText() == "" || TextUtils.isEmpty(etDescription.getText())) {
                CustomToast.makeText(getContext(), "Please fill all the required fields").show();
                return;
            }
            newEvent = new VirtualEvent(etName.getText().toString(), eventDate, eventTime, eventDuration, etDescription.getText().toString(), new HashMap<String, User>(), eventHobby, mAuth.getCurrentUser().getUid(), null, eventPlatform, tvLink.getText().toString());
        }
        addManagerToEvent(newEvent, eventType.toString());
        loadingBarAndGoToMainActivity();
    }


    private void addManagerToEvent(Event newEvent, String eventType) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                HashMap<String, User> participants = new HashMap<>();
                participants.put(userId, user);
                newEvent.setParticipants(participants);
                if (eventType.equals("PHYSICAL"))
                    refDatabase.child("Physical_Events").child(newEvent.getID()).setValue(newEvent);
                else refDatabase.child("Virtual_Events").child(newEvent.getID()).setValue(newEvent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadingBarAndGoToMainActivity() {
        CreateEventDialog createEventDialog = new CreateEventDialog(getActivity());
        createEventDialog.loadingDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createEventDialog.dismissDialog();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2500);
    }


    public interface CreateEventListener {
        public void handleCrateEventFragment(int request);
    }

}
