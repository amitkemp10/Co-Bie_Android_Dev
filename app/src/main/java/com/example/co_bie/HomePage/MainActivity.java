package com.example.co_bie.HomePage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.co_bie.AboutUsFragment;
import com.example.co_bie.Chat.MessageListFragment;
import com.example.co_bie.CustomToast;
import com.example.co_bie.Event.ChooseEventTypeFragment;
import com.example.co_bie.Event.CreateEventFragment;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.JoinEventDialogFragment;
import com.example.co_bie.Event.MyEvent.MyEventsFragment;
import com.example.co_bie.Event.Physical.MapPhysicalEventFragment;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Utils;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.Event.Virtual.VirtualEventFragment;
import com.example.co_bie.LoginAndRegistration.StartActivity;
import com.example.co_bie.MyProfileFragment;
import com.example.co_bie.Notification.EventReminderService;
import com.example.co_bie.R;
import com.example.co_bie.Event.SharedViewModel;
import com.example.co_bie.UsefulMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements CreateEventFragment.CreateEventListener, MapPhysicalEventFragment.GetEventSelectedLocationListener, ChooseEventTypeFragment.ChooseEventTypeListener, VirtualEventFragment.VirtualEventFragmentListener {

    Button add_img_profile;
    TextView tv_username;
    DrawerLayout drawerLayout;
    NavigationView nav_view;
    ActionBarDrawerToggle drawer_toggle;
    FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase, refPhy, refVir;
    FirebaseUser user;
    String full_name, gender, img_link;
    ImageView img_profile;
    private Uri imagePath;
    FragmentManager fm;
    private SharedViewModel sharedViewModel;
    Intent serviceIntent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.secondary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawer_toggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.create_event) getChooseEventTypeFragment();
        if (item.getItemId() == R.id.my_chats) getChatFragment();
        return super.onOptionsItemSelected(item);
    }

    private void getChooseEventTypeFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().setReorderingAllowed(true).replace(R.id.home_page_container, new ChooseEventTypeFragment(), "ChooseEventType").addToBackStack(null).commit();
        fm.executePendingTransactions();
    }

    private void getChatFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().setReorderingAllowed(true).replace(R.id.home_page_container, new MessageListFragment(), "Chat").addToBackStack(null).commit();
        fm.executePendingTransactions();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = fm.findFragmentById(R.id.home_page_container);
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            if (currentFragment instanceof HomePageFragment)
                nav_view.setCheckedItem(R.id.nav_home_page);
            else if (currentFragment instanceof MyEventsFragment)
                nav_view.setCheckedItem(R.id.nav_home_page);
            else if (currentFragment instanceof MyProfileFragment)
                nav_view.setCheckedItem(R.id.nav_home_page);
            else if (currentFragment instanceof AboutUsFragment)
                nav_view.setCheckedItem(R.id.nav_home_page);
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        fm = getSupportFragmentManager();
        setContentView(R.layout.activity_main);

        checkIfNotification();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        View headerView = navigationView.getHeaderView(0);
        tv_username = headerView.findViewById(R.id.tv_menu_username);
        img_profile = headerView.findViewById(R.id.iv_profile);
        add_img_profile = headerView.findViewById(R.id.add_profile_img);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(intent);
            finish();
        } else createMenu();

        add_img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, 1);
            }
        });

        serviceIntent = new Intent(this, EventReminderService.class);
        startService(serviceIntent);

        setFullNameOnMenu();
        setProfileImgOnMenu();
        getNotificationsPermission();
    }


    private void checkIfNotification() {
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("type")) {
            String type = getIntent().getExtras().getString("type");
            if (type.equals("New_Invitation")) {
                String jsonString = getIntent().getExtras().getString("event");
                String virtual_physical = getIntent().getExtras().getString("virtual_physical");
                Gson gson = new Gson();
                Event event;
                Utils.EventType eventType;
                if (virtual_physical.equals("Physical_Events")) {
                    event = gson.fromJson(jsonString, PhysicalEvent.class);
                    eventType = Utils.EventType.PHYSICAL;
                } else {
                    event = gson.fromJson(jsonString, VirtualEvent.class);
                    eventType = Utils.EventType.VIRTUAL;
                }
                JoinEventDialogFragment joinFragment = new JoinEventDialogFragment(event, eventType);
                FragmentManager fragmentManager = ((AppCompatActivity) this).getSupportFragmentManager();
                joinFragment.show(fragmentManager, "join");
            }
        }
    }


    private void getNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Channel name", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            // Push notifications are already enabled
        } else {
            // Push notifications are not enabled, request permission from the user
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent received_intent = getIntent();
        if (received_intent.getBooleanExtra("isCreateEvent", false)) {
            handleCreateEventHobbySelection(received_intent);
        }
    }

    private void setProfileImgOnMenu() {
        refDatabase.child(user.getUid()).child("profile_img").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    img_link = String.valueOf(task.getResult().getValue());
                    if (!img_link.isEmpty()) Picasso.get().load(img_link).into(img_profile);
                    else setDefaultImage();
                } else Log.e("Firebase", "Error getting data");
            }
        });
    }

    private void setFullNameOnMenu() {
        refDatabase = database.getReference("users");
        refDatabase.child(user.getUid()).child("full_name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    full_name = String.valueOf(task.getResult().getValue());
                    tv_username.setText(full_name);
                } else Log.e("Firebase", "Error getting data");
            }
        });
    }

    public void handleCreateEventHobbySelection(Intent intent) {
        String hobbyName = intent.getStringExtra("SelectedHobbyName");
        int hobbyImg = intent.getIntExtra("SelectedHobbyImg", 0);
        Bundle bundle = new Bundle();
        bundle.putString("hobbyName", hobbyName);
        bundle.putInt("hobbyImg", hobbyImg);
        CreateEventFragment fragment = new CreateEventFragment();
        fragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().setReorderingAllowed(true).replace(R.id.home_page_container, fragment, "BackCreateEventFragmentWithLocation").addToBackStack(null).commit();
        fm.executePendingTransactions();

    }

    private void createMenu() {
        drawerLayout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.navigation_menu);
        drawer_toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close);

        drawerLayout.addDrawerListener(drawer_toggle);
        drawer_toggle.syncState();
        nav_view.setCheckedItem(R.id.nav_home_page);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() != nav_view.getCheckedItem().getItemId()) {
                    switch (item.getItemId()) {
                        case R.id.nav_home_page:
                            getSupportFragmentManager().popBackStack();
                            getHomeFragment(fm);
                            break;
                        case R.id.nav_my_profile:
                            getSupportFragmentManager().popBackStack();
                            getMyMyProfileFragment(fm);
                            break;
                        case R.id.nav_my_events:
                            getSupportFragmentManager().popBackStack();
                            getMyEventsFragment(fm);
                            break;
                        case R.id.nav_logout:
                            logoutCo_Bie();
                            break;
                        case R.id.nav_about_us:
                            getSupportFragmentManager().popBackStack();
                            getAboutUsFragment(fm);
                            break;
                    }
                    nav_view.setCheckedItem(item.getItemId());
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void getHomeFragment(FragmentManager fm) {
        fm.beginTransaction().setReorderingAllowed(true).replace(R.id.home_page_container, new HomePageFragment(), "HomePage").addToBackStack(null).commit();
        fm.executePendingTransactions();
    }

    private void getMyEventsFragment(FragmentManager fm) {
        fm.beginTransaction().setReorderingAllowed(true).replace(R.id.home_page_container, new MyEventsFragment(), "MyEvents").addToBackStack(null).commit();
        fm.executePendingTransactions();
    }

    private void getAboutUsFragment(FragmentManager fm) {
        fm.beginTransaction().setReorderingAllowed(true).replace(R.id.home_page_container, new AboutUsFragment(), "AboutUs").addToBackStack(null).commit();
        fm.executePendingTransactions();
    }

    private void getMyMyProfileFragment(FragmentManager fm) {
        fm.beginTransaction().setReorderingAllowed(true).replace(R.id.home_page_container, new MyProfileFragment(), "MyProfile").addToBackStack(null).commit();
        fm.executePendingTransactions();
    }

    private void setDefaultImage() {
        refDatabase.child(user.getUid()).child("gender").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    gender = String.valueOf(task.getResult().getValue());
                    if (gender.equals("Male")) img_profile.setImageResource(R.drawable.ic_boy);
                    else img_profile.setImageResource(R.drawable.ic_girl);
                } else Log.e("Firebase", "Error getting data");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            getImageInImageView();
            uploadImage();
        }
    }

    private void getImageInImageView() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        img_profile.setImageBitmap(bitmap);
    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        pd.setTitle("Uploading Profile Picture");
        pd.show();

        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful())
                                updateProfileImage(task.getResult().toString());
                        }
                    });
                    CustomToast.makeText(getApplicationContext(), "Image Uploaded").show();
                } else
                    CustomToast.makeText(getApplicationContext(), task.getException().getLocalizedMessage()).show();
                pd.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                pd.setMessage("Uploaded " + (int) progress + "%");
            }
        });
    }

    private void updateProfileImage(String url) {
        database.getReference("users/" + mAuth.getUid() + "/profile_img").setValue(url);
    }

    private void logoutCo_Bie() {
        updateUserStatus(FirebaseAuth.getInstance().getUid());
    }

    private void updateUserStatus(String userId) {
        HashMap update_user_status = new HashMap();
        update_user_status.put("status", "Offline");
        refDatabase.child(userId).updateChildren(update_user_status);

        refPhy = database.getReference("events").child("Physical_Events");
        refPhy.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PhysicalEvent pe = ds.getValue(PhysicalEvent.class);
                    if (pe.getParticipants() != null && pe.getParticipants().containsKey(userId))
                        refPhy.child(pe.getID()).child("participants").child(userId).updateChildren(update_user_status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        refVir = database.getReference("events").child("Virtual_Events");
        refVir.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    VirtualEvent ve = ds.getValue(VirtualEvent.class);
                    if (ve.getParticipants() != null && ve.getParticipants().containsKey(userId))
                        refVir.child(ve.getID()).child("participants").child(userId).updateChildren(update_user_status);
                }

                UsefulMethods.deleteTokenForUser();
                FirebaseAuth.getInstance().signOut();
                stopService(serviceIntent);
                CustomToast.makeText(getApplicationContext(), user.getEmail() + " logged out").show();
                Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClickLocationSelect(int option) {
        switch (option) {
            case 1:
                fm.popBackStack();
                break;
            case 2:
                fm.popBackStack();
                getChooseEventTypeFragment();
                break;
            case 3:
                fm.popBackStack();
                getChatFragment();
                break;
            case 4:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
    }

    @Override
    public void EventTypeSelected(Utils.EventType type) {
        Bundle bundle = new Bundle();
        if (type.equals(Utils.EventType.PHYSICAL)) bundle.putString("EventType", "Physical");
        if (type.equals(Utils.EventType.VIRTUAL)) bundle.putString("EventType", "Virtual");
        CreateEventFragment fragment = new CreateEventFragment();
        fragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().setReorderingAllowed(true).replace(R.id.home_page_container, fragment, "CreateEvent").addToBackStack(null).commit();
        fm.executePendingTransactions();
    }

    @Override
    public void handleCrateEventFragment(int request) {
        switch (request) {
            case 1: //location picker
                Bundle bundle = new Bundle();
                bundle.putString("is_show_select_button", "YES");
                MapPhysicalEventFragment fragment = new MapPhysicalEventFragment();
                fragment.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().setReorderingAllowed(true).replace(R.id.home_page_container, fragment, "SelectLocationMap").addToBackStack(null).commit();
                fm.executePendingTransactions();
                break;
        }
    }

    @Override
    public void onClickOptionVirtualFragment(int option) {
        switch (option) {
            case 2:
                fm.popBackStack();
                getChooseEventTypeFragment();
                break;
            case 3:
                fm.popBackStack();
                getChatFragment();
                break;
            case 4:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
    }
}