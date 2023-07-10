package com.example.co_bie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

    public static Toast makeText(Context context, String message) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // Set the message text and icon
        TextView textView = layout.findViewById(R.id.custom_toast_text);
        textView.setText(message);
        ImageView imageView = layout.findViewById(R.id.custom_toast_icon);
        imageView.setImageResource(R.drawable.custom_toast);

        // Create and return the Toast object
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        return toast;
    }
}

