package com.example.co_bie;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class UsefulMethods {

    public static void accountDisabledMsg(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Your account has been disabled. Please contact support.");
        builder.setPositiveButton("Contact Us", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:supp.cobie@gmail.com"));
                PendingIntent pendingIntent = getActivity(activity, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }).setNegativeButton("Dismiss", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // set text color for positive button
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(ContextCompat.getColor(activity, R.color.purple));
                // set text color for negative button
                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setTextColor(ContextCompat.getColor(activity, R.color.purple));
            }
        });
        alertDialog.show();
    }

    public static void getTokenForUser() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // Get the FCM token
                    String token = task.getResult();

                    // Save the FCM token for the user in the database
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                    databaseReference.child("fcmToken").setValue(token);
                } else {
                    // Handle the error
                }
            }
        });
    }

    public static void deleteTokenForUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Delete the FCM token for the user in the database
                        String uid = user.getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                        databaseReference.child("fcmToken").removeValue();
                    } else {
                        // Handle the error
                    }
                }
            });
        }
    }

}
