package com.example.co_bie.Event.MyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.co_bie.CustomToast;
import com.example.co_bie.Event.ChooseEventTypeFragment;
import com.example.co_bie.Event.Event;
import com.example.co_bie.Event.JoinEventDialogFragment;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Utils;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.Notification.MyFirebaseInstanceNotifications;
import com.example.co_bie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    TextView event_title, event_date, event_time, event_des, event_participants;
    ImageView event_hobby, event_virtual_location, add_friend;
    CircleImageView event_profile;
    String locationPlatformName, img_link, event_id, code, virtual_physical, event_hobby_name, event_manager;
    private Uri imagePath;
    FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    FirebaseUser user;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapterEventFeature viewPagerAdapterEventFeature;
    Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        event_title = findViewById(R.id.tv_event_title);
        event_date = findViewById(R.id.tv_event_date);
        event_time = findViewById(R.id.tv_event_time);
        event_des = findViewById(R.id.tv_event_des);
        event_participants = findViewById(R.id.tv_event_participants);
        event_hobby = findViewById(R.id.img_event_hobby);
        event_virtual_location = findViewById(R.id.img_event_virtual_location);
        event_profile = findViewById(R.id.iv_event_profile);
        add_friend = findViewById(R.id.iv_add_friend);


        SpannableString content = new SpannableString(getIntent().getStringExtra("event_title"));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        event_title.setText(content);
        event_date.setText(getIntent().getStringExtra("event_date"));
        event_time.setText(getIntent().getStringExtra("event_time"));
        event_id = getIntent().getStringExtra("event_id");
        event_des.setText(getIntent().getStringExtra("event_description"));
        event_participants.setText(getIntent().getStringExtra("event_participants"));
        event_hobby_name = getIntent().getStringExtra("event_hobby");
        event_hobby.setImageResource(Utils.appropriateImage(event_hobby_name));
        locationPlatformName = getIntent().getStringExtra("event_platform_location");
        event_manager = getIntent().getStringExtra("event_manager");

        code = getIntent().getStringExtra("code");
        if (code.equals("2")) add_friend.setVisibility(View.GONE);

        if (locationPlatformName.equals("TEAMS") || locationPlatformName.equals("ZOOM") || locationPlatformName.equals("SKYPE"))
            virtual_physical = "Virtual_Events";
        else virtual_physical = "Physical_Events";

        EventFeaturesFragment fragment = new EventFeaturesFragment(getDataFragEventUsers(event_id, locationPlatformName));
        getSupportFragmentManager().beginTransaction().replace(R.id.event_feature_container, fragment).commit();

        event_virtual_location.setImageResource(Utils.appropriatePlatformImage(locationPlatformName));
        event_virtual_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar;
                if (locationPlatformName.equals("TEAMS") || locationPlatformName.equals("ZOOM") || locationPlatformName.equals("SKYPE"))
                    snackbar = Snackbar.make(view, locationPlatformName + " Platform", Snackbar.LENGTH_SHORT);
                else snackbar = Snackbar.make(view, locationPlatformName, Snackbar.LENGTH_SHORT);
                snackbar.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snackbar.dismiss();
                    }
                }, 2000);
            }
        });
        event_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event_profile != null && event_profile.getDrawable() != null && event_profile.getDrawable().getConstantState() != null) {
                    // Create a new dialog or small screen view
                    Dialog dialog = new Dialog(EventActivity.this);
                    dialog.setContentView(R.layout.dialog_full_screen_image);

                    ImageView imageView = dialog.findViewById(R.id.full_screen_image_view);
                    Button downloadButton = dialog.findViewById(R.id.download_button);
                    downloadButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(EventActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                // Request the permission
                                ActivityCompat.requestPermissions((Activity) EventActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                            } else {
                                // Permission has already been granted
                                // Save the image to external storage
                                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                                // Save the image
                                saveImage(bitmap);
                            }
                        }
                    });

                    setImageForDownload(imageView);

                    // Set the layout parameters to fill the available space
                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    window.setAttributes(layoutParams);

                    // Show the dialog or small screen view
                    dialog.show();
                } else if (code.equals("1")) {
                    Intent photoIntent = new Intent(Intent.ACTION_PICK);
                    photoIntent.setType("image/*");
                    startActivityForResult(photoIntent, 1);
                }
            }
        });
        setEventImg();

        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddUsersFragment(event_hobby_name, virtual_physical, event_id, event_title.getText().toString(), event_manager);
            }
        });
    }

    private void getAddUsersFragment(String event_hobby_name, String event_virtual_location, String event_id, String event_title, String event_manager) {
        AddUsersDialogFragment addUsersDialogFragment = new AddUsersDialogFragment(event_hobby_name, event_virtual_location, event_id, event_title, event_manager);
        addUsersDialogFragment.show(getSupportFragmentManager(), "addUsers");
    }

    private Bundle getDataFragEventUsers(String event_id, String locationPlatformName) {
        Bundle bundle = new Bundle();
        if (locationPlatformName.equals("TEAMS") || locationPlatformName.equals("ZOOM") || locationPlatformName.equals("SKYPE"))
            bundle.putString("event_type", "Virtual_Events");
        else bundle.putString("event_type", "Physical_Events");
        bundle.putString("event_id", event_id);
        bundle.putString("code", code);
        return bundle;
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
        event_profile.setImageBitmap(bitmap);
    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        pd.setTitle("Uploading Event Image");
        pd.show();

        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) updateEventImage(task.getResult().toString());
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

    private void setEventImg() {
        if (locationPlatformName.equals("TEAMS") || locationPlatformName.equals("ZOOM") || locationPlatformName.equals("SKYPE"))
            refDatabase = database.getReference("events").child("Virtual_Events").child(event_id).child("event_img");
        else
            refDatabase = database.getReference("events").child("Physical_Events").child(event_id).child("event_img");
        refDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    img_link = String.valueOf(task.getResult().getValue());
                    if (!img_link.isEmpty()) Picasso.get().load(img_link).into(event_profile);
                } else Log.e("Firebase", "Error getting data");
            }
        });
    }

    private void setImageForDownload(ImageView imageView) {
        if (locationPlatformName.equals("TEAMS") || locationPlatformName.equals("ZOOM") || locationPlatformName.equals("SKYPE"))
            refDatabase = database.getReference("events").child("Virtual_Events").child(event_id).child("event_img");
        else
            refDatabase = database.getReference("events").child("Physical_Events").child(event_id).child("event_img");
        refDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    img_link = String.valueOf(task.getResult().getValue());
                    if (!img_link.isEmpty()) Picasso.get().load(img_link).into(imageView);
                } else Log.e("Firebase", "Error getting data");
            }
        });
    }

    private void updateEventImage(String url) {
        if (locationPlatformName.equals("TEAMS") || locationPlatformName.equals("ZOOM") || locationPlatformName.equals("SKYPE"))
            database.getReference("events/" + "Virtual_Events/" + event_id + "/event_img").setValue(url);
        else
            database.getReference("events/" + "Physical_Events/" + event_id + "/event_img").setValue(url);
    }

    private void saveImage(Bitmap bitmap) {
        // Get the directory for the app's private external storage
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Create a file name for the image
        String fileName = String.valueOf(UUID.randomUUID() + ".jpg");

        // Create a file path for the image
        File file = new File(dir, fileName);

        try {
            // Open a file output stream to write the image to the file
            FileOutputStream fos = new FileOutputStream(file);

            // Compress the bitmap to JPEG format and write it to the file output stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // Flush and close the file output stream
            fos.flush();
            fos.close();

            // Display a toast message indicating that the image was saved
            CustomToast.makeText(getApplicationContext(), "Image saved").show();
        } catch (IOException e) {
            // Display a toast message indicating that an error occurred
            CustomToast.makeText(getApplicationContext(), "Error saving image: " + e.getMessage()).show();
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                // Log a message indicating the media scan is complete
                Log.d("ImagePagerAdapter", "Media scan complete");
            }
        });
    }
}