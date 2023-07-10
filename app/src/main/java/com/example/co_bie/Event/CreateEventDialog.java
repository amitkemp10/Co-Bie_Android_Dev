package com.example.co_bie.Event;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.co_bie.R;

public class CreateEventDialog extends DialogFragment {

    private Activity activity;
    private AlertDialog dialog;

    public CreateEventDialog(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void loadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_event_dialog, null);
        ImageView loadingImage = view.findViewById(R.id.animation_create_event);
        loadingImage.setImageResource(R.drawable.avd_done);

        Drawable drawable = loadingImage.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }

        builder.setView(view);
        setCancelable(false);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }


    void dismissDialog() {
        dialog.dismiss();
    }
}
