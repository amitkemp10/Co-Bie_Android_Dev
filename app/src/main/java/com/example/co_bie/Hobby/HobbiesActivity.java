package com.example.co_bie.Hobby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.co_bie.CustomToast;
import com.example.co_bie.FireBaseQueries;
import com.example.co_bie.LoginAndRegistration.StartActivity;
import com.example.co_bie.HomePage.MainActivity;
import com.example.co_bie.R;
import com.example.co_bie.LoginAndRegistration.User;
import com.example.co_bie.UsefulMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HobbiesActivity extends AppCompatActivity implements HobbiesAdapter.SelectedItemListener {

    private ProgressBar progress_bar;
    private TextView tv_choose_hobbies;
    private CardView cv_hobbies;
    Button btn_register;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapterHobbies viewPagerAdapterHobbies;
    User user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase;
    private ArrayList<Hobby> selected_hobbies;
    private boolean isCreateEvent;
    private boolean isEditHobbies;
    private User editor;
    private String editorID;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbies);

        Intent intent = getIntent();
        String email = intent.getStringExtra(StartActivity.user_email);
        String full_name = intent.getStringExtra(StartActivity.user_full_name);
        String username = intent.getStringExtra(StartActivity.user_username);
        String password = intent.getStringExtra(StartActivity.user_password);
        String birth_date = intent.getStringExtra(StartActivity.user_birth_date);
        String gender = intent.getStringExtra(StartActivity.user_gender);
        selected_hobbies = new ArrayList<>();
        isCreateEvent = intent.getBooleanExtra("isCreateEvent", false);
        isEditHobbies = intent.getBooleanExtra("isEditHobbies", false);

        tabLayout = findViewById(R.id.tab_categories);
        tv_choose_hobbies = findViewById(R.id.tv_choose_hobbies);
        cv_hobbies = findViewById(R.id.card_view_hobbies);
        btn_register = findViewById(R.id.btn_register);
        progress_bar = findViewById(R.id.progressBar);
        viewPager2 = findViewById(R.id.view_pager_categories);
        cardView = findViewById(R.id.card_view_hobbies);
        viewPagerAdapterHobbies = new ViewPagerAdapterHobbies(this);
        viewPager2.setAdapter(viewPagerAdapterHobbies);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        refDatabase = database.getReference("users");

        if (isCreateEvent) {
            btn_register.setVisibility(View.GONE);
            tv_choose_hobbies.setText("Choose Hobby");
            marginTopCardView(80);
        }

        if (isEditHobbies) {
            btn_register.setText("Done");
            FireBaseQueries fireBaseQueries = new FireBaseQueries();
            editorID = fireBaseQueries.getCurrentUserUUID();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditHobbies) {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference userRef = databaseRef.child("users").child(editorID);
                    userRef.child("hobbiesList").setValue(selected_hobbies);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("isEditHobbies", true);
                    finishAffinity();
                    startActivity(intent);
                } else {
                    progress_bar.getIndeterminateDrawable().setColorFilter(0xFF8692f7, android.graphics.PorterDuff.Mode.MULTIPLY);
                    progress_bar.bringToFront();
                    progress_bar.setVisibility(View.VISIBLE);
                    createNewUser(email, password, full_name, username, birth_date, gender);
                }
            }
        });
    }

    private void createNewUser(String email, String password, String full_name, String username, String birth_date, String gender) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progress_bar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    user = new User(email, full_name, username, password, birth_date, gender, "", selected_hobbies, "Online", null);
                    refDatabase.child(mAuth.getUid()).setValue(user);
                    selected_hobbies.clear();
                    UsefulMethods.getTokenForUser();
                    CustomToast.makeText(getApplicationContext(), "Account Created").show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    finishAffinity();
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    CustomToast.makeText(getApplicationContext(), "Creation Failed").show();
                    selected_hobbies.clear();
                }
            }

        });
    }

    @Override
    public void onSelectedItem(Hobby selected_hobbie, String state) {
        if (state.equals("YES")) {
            if (isCreateEvent) {
                Intent intent = new Intent();
                intent.putExtra("isCreateEvent", true);
                intent.putExtra("SelectedHobbyName", selected_hobbie.getHobby_name());
                intent.putExtra("SelectedHobbyImg", selected_hobbie.getImg_hobby());
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else selected_hobbies.add(selected_hobbie);
        } else selected_hobbies.remove(selected_hobbie);
    }

    private void marginTopCardView(int dp) {
        int marginInDp = dp; // desired margin in dp
        float density = getResources().getDisplayMetrics().density;
        int marginInPx = (int) (marginInDp * density + 0.5f); // convert dp to px
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        params.topMargin = marginInPx;
        cardView.setLayoutParams(params);
    }

    private void marginTopBtn(int dp) {
        int marginInDp = dp; // desired margin in dp
        float density = getResources().getDisplayMetrics().density;
        int marginInPx = (int) (marginInDp * density + 0.5f); // convert dp to px
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
        params.topMargin = marginInPx;
        btn_register.setLayoutParams(params);
    }
}