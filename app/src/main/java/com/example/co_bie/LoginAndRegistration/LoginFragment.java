package com.example.co_bie.LoginAndRegistration;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.co_bie.CustomToast;
import com.example.co_bie.Event.Physical.PhysicalEvent;
import com.example.co_bie.Event.Virtual.VirtualEvent;
import com.example.co_bie.R;
import com.example.co_bie.UsefulMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class LoginFragment extends Fragment {

    LoginListener loginListener;
    SignUpListener signUpListener;
    UserIsConnected userIsConnected;
    private EditText ed_email, ed_password;
    private Button btn_login;
    private ProgressBar progress_bar;
    TextView tv_signup, tv_forgot_password;
    FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference refDatabase, refPhy, refVir;

    @Override
    public void onAttach(@NonNull Context context) {
        try {
            this.signUpListener = (SignUpListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " + context.getClass().getName() + " must implements the interface 'SignUpListener'");
        }
        try {
            this.loginListener = (LoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " + context.getClass().getName() + " must implements the interface 'LoginListener'");
        }
        try {
            this.userIsConnected = (UserIsConnected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " + context.getClass().getName() + " must implements the interface 'UserIsConnected'");
        }
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userIsConnected.userConnected();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ed_email = view.findViewById(R.id.ed_login_email);
        ed_password = view.findViewById(R.id.ed_login_password);
        progress_bar = view.findViewById(R.id.progressBar);
        btn_login = view.findViewById(R.id.btn_login);
        tv_signup = view.findViewById(R.id.tv_for_signup);
        tv_forgot_password = view.findViewById(R.id.tv_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        refDatabase = database.getReference("users");

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpListener.onClickSignUp();
            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgot_password_Co_Bie();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_Co_Bie();
            }
        });
    }

    private void login_Co_Bie() {

        progress_bar.getIndeterminateDrawable().setColorFilter(0xFF8692f7, android.graphics.PorterDuff.Mode.MULTIPLY);
        progress_bar.bringToFront();
        progress_bar.setVisibility(View.VISIBLE);
        String email, password;
        email = ed_email.getText().toString();
        password = ed_password.getText().toString();
        if (!checkEmail(email)) {
            ed_email.setError("Enter your email");
            progress_bar.setVisibility(View.GONE);
            return;
        }
        if (!checkPassword(password)) {
            ed_password.setError("Enter your password");
            progress_bar.setVisibility(View.GONE);
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progress_bar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    refDatabase = database.getReference("users").child(mAuth.getUid());
                    refDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.getValue(User.class).getReported_by() != null && snapshot.getValue(User.class).getReported_by().size() == 5) {
                                    UsefulMethods.accountDisabledMsg(getActivity());
                                    mAuth.signOut(); // Sign out the user
                                } else {
                                    refDatabase = database.getReference("users");
                                    updateUserPassword(password);
                                    updateUserStatus(mAuth.getUid()); // Call the method here
                                    UsefulMethods.getTokenForUser();
                                    loginListener.onClickLogin();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event
                        }
                    });
                } else {
                    CustomToast.makeText(getContext(), "Authentication Failed").show();
                }
            }
        });
    }

    private void updateUserPassword(String password) {
        HashMap update_pass_if_needed = new HashMap();
        update_pass_if_needed.put("password", password);
        refDatabase.child(mAuth.getUid()).updateChildren(update_pass_if_needed);
    }

    private void updateUserStatus(String userId) {
        HashMap update_user_status = new HashMap();
        update_user_status.put("status", "Online");
        refDatabase.child(userId).updateChildren(update_user_status);

        refPhy = database.getReference("events").child("Physical_Events");

        refPhy.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PhysicalEvent pe = ds.getValue(PhysicalEvent.class);
                    if (pe.getParticipants() != null && pe.getParticipants().containsKey(userId)) {
                        refPhy.child(pe.getID()).child("participants").child(userId).updateChildren(update_user_status);
                    }
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
                    if (ve.getParticipants() != null && ve.getParticipants().containsKey(userId)) {
                        refVir.child(ve.getID()).child("participants").child(userId).updateChildren(update_user_status);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void forgot_password_Co_Bie() {
        progress_bar.getIndeterminateDrawable().setColorFilter(0xFF8692f7, android.graphics.PorterDuff.Mode.MULTIPLY);
        progress_bar.bringToFront();
        progress_bar.setVisibility(View.VISIBLE);
        String email;
        email = ed_email.getText().toString();
        if (!checkEmail(email)) {
            ed_email.setError("Enter your email");
            progress_bar.setVisibility(View.GONE);
            return;
        }
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                CustomToast.makeText(getContext(), "Email sent").show();
                progress_bar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CustomToast.makeText(getContext(), "Email doesn't exist").show();
                progress_bar.setVisibility(View.GONE);
            }
        });
    }

    private boolean checkEmail(String email) {
        if (email.equals("")) return false;
        return true;
    }

    private boolean checkPassword(String password) {
        if (password.equals("")) return false;
        return true;
    }

    public interface LoginListener {
        public void onClickLogin();
    }

    public interface SignUpListener {
        public void onClickSignUp();
    }

    public interface UserIsConnected {
        public void userConnected();
    }
}