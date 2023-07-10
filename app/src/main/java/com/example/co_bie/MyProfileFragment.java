package com.example.co_bie;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.co_bie.Hobby.HobbiesActivity;
import com.example.co_bie.LoginAndRegistration.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyProfileFragment extends Fragment {

    private ImageButton btnEditName;
    private ImageButton btnEditBirthdate;
    private ImageButton btnEditHobbies;
    private ImageButton btnEditGender;
    private TextView etName;
    private TextView etBirthdate;
    private EditText etHobbies;
    private TextView etGender;
    private CircleImageView profile_pic;
    private FireBaseQueries fireBaseQueries;
    private User currUser;
    private DatabaseReference databaseRef;
    private DatabaseReference userRef;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        fireBaseQueries = new FireBaseQueries();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        userRef = databaseRef.child("users").child(fireBaseQueries.getCurrentUserUUID());
        etName = view.findViewById(R.id.name_value_profile);
        etBirthdate = view.findViewById(R.id.birthdate_value_profile);
        etHobbies = view.findViewById(R.id.hobbies_value_profile);
        etGender = view.findViewById(R.id.gender_value_profile);
        btnEditName = view.findViewById(R.id.edit_name_btn_profile);
        btnEditBirthdate = view.findViewById(R.id.edit_bd_btn_profile);
        btnEditHobbies = view.findViewById(R.id.edit_hobbies_btn_profile);
        btnEditGender = view.findViewById(R.id.edit_gender_btn_profile);
        profile_pic = view.findViewById(R.id.current_user_pic_profile);

        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEditName();
            }
        });

        btnEditBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEditBirthDate();
            }
        });

        btnEditHobbies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEditHobbies();
            }
        });

        btnEditGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEditGender();
            }
        });

        fireBaseQueries.getUserByUUID(fireBaseQueries.getCurrentUserUUID(), new FireBaseQueries.getUserByUUIDCallback() {
            @Override
            public void onCallback(User user) {
                currUser = user;
                fillData();
            }
        });

        return view;
    }

    private void fillData() {
        String hobbiesNames = "";

        for (int i = 0; i < currUser.getHobbiesList().size(); i++) {
            hobbiesNames = hobbiesNames += currUser.getHobbiesList().get(i).getHobby_name() + "\n";
        }
        if (!currUser.getProfile_img().equals(""))
            Picasso.get().load(currUser.getProfile_img()).into(profile_pic);
        else if (currUser.getGender().equals("Male"))
            profile_pic.setImageResource(R.drawable.ic_boy);
        else profile_pic.setImageResource(R.drawable.ic_girl);
        etName.setText(currUser.getFull_name());
        etBirthdate.setText(currUser.getBirth_date().toString());
        etHobbies.setText(hobbiesNames);
        etGender.setText(currUser.getGender());
    }

    private void handleEditName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), R.style.PurpleDialog);
        final EditText edittext = new EditText(getContext());
        edittext.setHint("Enter your name");
        alert.setView(edittext);
        edittext.setText("  " + currUser.getFull_name());

        alert.setPositiveButton("Set   ✔", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newName = edittext.getText().toString().trim();
                if (newName.isEmpty()) {
                    CustomToast.makeText(getContext(), "Please enter a valid name").show();
                } else {
                    currUser.setFull_name(newName);
                    userRef.child("full_name").setValue(newName);
                    etName.setText(newName);
                    CustomToast.makeText(getContext(), "Updated Name").show();
                }
            }
        });
        alert.setNegativeButton("Cancel   ❌", null);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.WHITE);
        gradientDrawable.setCornerRadius(15);

        Window window = alert.show().getWindow();
        window.setBackgroundDrawable(gradientDrawable);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = 900;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(layoutParams);
    }


    public void handleEditBirthDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        int year = calendar.get(Calendar.YEAR) - 16;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.DateTimeDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (!checkValidAge(year, month, day)) {
                    CustomToast.makeText(getContext(), "You must be at least 16 years old").show();
                    etBirthdate.setText(null);
                    return;
                }
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                etBirthdate.setText(date);
                etBirthdate.setTextColor(Color.parseColor("#000000"));
                currUser.setBirth_date(date);
                userRef.child("birth_date").setValue(date);
                CustomToast.makeText(getContext(), "Updated Birth Date").show();
            }
        }, year, month, day);
        dialog.show();
    }

    private boolean checkValidAge(int year, int month, int day) {
        // Check if the selected birth date is less than 16 years ago
        Calendar birthDate = Calendar.getInstance();
        birthDate.set(year, month, day);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -16);
        if (birthDate.compareTo(minDate) > 0)
            // Birth date is valid (i.e., age is at least 16 years)
            return false;
        // Birth date is not valid (i.e., age is less than 16 years)
        return true;
    }

    public void handleEditGender() {
        // Create a new LinearLayout with vertical orientation
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // Create a new RadioGroup element
        final RadioGroup radioGroup = new RadioGroup(getContext());
        int purple = Color.parseColor("#8692f7"); // red color
        ColorStateList colorStateList = ColorStateList.valueOf(purple);

        // Create two new RadioButton elements and add them to the RadioGroup
        RadioButton maleRadioButton = new RadioButton(getContext());
        maleRadioButton.setText("  Male");
        maleRadioButton.setButtonTintList(colorStateList);
        radioGroup.addView(maleRadioButton);

        RadioButton femaleRadioButton = new RadioButton(getContext());
        femaleRadioButton.setText("  Female");
        femaleRadioButton.setButtonTintList(colorStateList);
        radioGroup.addView(femaleRadioButton);
        maleRadioButton.setChecked(true);

        // Set the margin for the RadioGroup in the LinearLayout
        LinearLayout.LayoutParams radioGroupLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        radioGroupLayoutParams.setMargins(dpToPx(10), dpToPx(10), 0, 0);
        radioGroup.setLayoutParams(radioGroupLayoutParams);

        // Add the RadioGroup to the LinearLayout
        linearLayout.addView(radioGroup);

        // Create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.PurpleDialog);
        builder.setView(linearLayout);

        // Set the positive button action
        builder.setPositiveButton("Set   ✔", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = (RadioButton) radioGroup.findViewById(selectedId);
                String selectedGender = selectedRadioButton.getText().toString();
                currUser.setGender(selectedGender);
                userRef.child("gender").setValue(selectedGender);
                etGender.setText(selectedGender);
                CustomToast.makeText(getContext(), "Updated Gender").show();
            }
        });

        // Set the negative button action
        builder.setNegativeButton("Cancel   ❌", null);

        // Create and show the dialog
        AlertDialog dialog = builder.create();

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.WHITE);
        gradientDrawable.setCornerRadius(15);

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(gradientDrawable);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = 400;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(layoutParams);

        dialog.show();
    }

    // Helper method to convert dp to pixels
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    private void handleEditHobbies() {
        Intent intent = new Intent(getActivity(), HobbiesActivity.class);
        intent.putExtra("isEditHobbies", true);
        startActivityForResult(intent, 3);
    }


}

