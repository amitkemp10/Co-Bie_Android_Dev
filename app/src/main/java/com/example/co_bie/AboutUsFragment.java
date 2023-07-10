package com.example.co_bie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class AboutUsFragment extends Fragment {

    private TextView aboutUsTxt;
    private TextView mailTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        aboutUsTxt = view.findViewById(R.id.aboutUsText);
        mailTxt = view.findViewById(R.id.CobieMailText);
        fillText();
        return view;
    }

    private void fillText(){
        String txt = "Welcome to Co-Bie!\nWe're here to connect people with common hobbies and interests.\n" +
                "Our team of enthusiasts understands the challenges of finding like-minded individuals, " +
                "which is why we created this app.\nWith Co-Bie, you can easily join events based on your favorite hobbies.\n" +
                "Our goal is to build a supportive community where everyone feels valued and included.\n" +
                "Thank you for choosing Co-Bie as your go-to app for connecting with others who share your passions.\n\n\n" +
                "For any support or inquiries, please email us at:";
        aboutUsTxt.setText(txt);
        String email = "supp.cobie@gmail.com";
        mailTxt.setText(email);
        mailTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + email)); // set the email address as the recipient
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of the email"); // set the subject of the email (optional)
                startActivity(intent);
            }
        });
    }
}