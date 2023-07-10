package com.example.co_bie.LoginAndRegistration;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.co_bie.CustomToast;
import com.example.co_bie.Hobby.HobbiesActivity;
import com.example.co_bie.HomePage.MainActivity;
import com.example.co_bie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity implements LoginFragment.SignUpListener, LoginFragment.LoginListener, LoginFragment.UserIsConnected, RegistrationFragment.ContinueListener {

    public static final String user_email = "Email";
    public static final String user_full_name = "Full Name";
    public static final String user_username = "Username";
    public static final String user_password = "Password";
    public static final String user_birth_date = "Birth Date";
    public static final String user_gender = "Gender";

    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_start);
    }

    @Override
    public void onClickSignUp() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().setReorderingAllowed(true).replace(R.id.start_container, new RegistrationFragment(), "SignUp").addToBackStack(null).commit();
        fm.executePendingTransactions();
    }

    @Override
    public void onClickLogin() {
        CustomToast.makeText(this, "Login Successful").show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClickContinue(String email, String full_name, String username, String password, String birth_date, String gender) {
        Intent intent = new Intent(getApplicationContext(), HobbiesActivity.class);
        intent.putExtra(user_email, email);
        intent.putExtra(user_full_name, full_name);
        intent.putExtra(user_username, username);
        intent.putExtra(user_password, password);
        intent.putExtra(user_birth_date, birth_date);
        intent.putExtra(user_gender, gender);
        startActivity(intent);
    }

    @Override
    public void userConnected() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        refDatabase = database.getReference("users").child(mAuth.getUid());
        refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getReported_by() != null && user.getReported_by().size() == 5) {
                        mAuth.signOut(); // Sign out the user
                        CustomToast.makeText(getApplicationContext(), "Your account has been disabled. Please contact support.").show();
                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                        startActivity(intent);
                        finishAffinity();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled events
            }
        });
    }
}