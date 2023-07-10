package com.example.co_bie.LoginAndRegistration;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.co_bie.CustomToast;
import com.example.co_bie.R;

import java.util.Calendar;

public class RegistrationFragment extends Fragment {

    ContinueListener contListener;
    private EditText ed_email, ed_password, ed_full_name, ed_username;
    private TextView tv_birth_date;
    private RadioGroup rad_gender;
    private Button btn_continue;
    private DatePickerDialog dialog;

    @Override
    public void onAttach(@NonNull Context context) {
        try {
            this.contListener = (ContinueListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the class " + context.getClass().getName() + " must implements the interface 'ContinueListener'");
        }
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        ed_email = v.findViewById(R.id.ed_registration_email);
        ed_password = v.findViewById(R.id.ed_registration_password);
        ed_full_name = v.findViewById(R.id.ed_registration_full_name);
        ed_username = v.findViewById(R.id.ed_registration_username);
        tv_birth_date = v.findViewById(R.id.tv_birth_date);
        rad_gender = v.findViewById(R.id.radio_grp_gender);
        btn_continue = v.findViewById(R.id.btn_continue);

        tv_birth_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDatePicker();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, full_name, username, birth_date, gender;
                email = ed_email.getText().toString();
                password = ed_password.getText().toString();
                full_name = ed_full_name.getText().toString();
                username = ed_username.getText().toString();
                birth_date = tv_birth_date.getText().toString();
                gender = ((RadioButton) v.findViewById(rad_gender.getCheckedRadioButtonId())).getText().toString();

                if (checkAllFields(email, password, full_name, username, birth_date)) return;
                contListener.onClickContinue(email, full_name, username, password, birth_date, gender);
            }
        });
    }

    private boolean checkAllFields(String email, String password, String full_name, String username, String birth_date) {
        if (!checkEmail(email)) {
            ed_email.setError("Enter your email");
            return true;
        } else ed_email.setError(null);
        if (!checkFullName(full_name)) {
            ed_full_name.setError("Enter your full name");
            return true;
        } else ed_full_name.setError(null);
        if (!checkUsername(username)) {
            ed_username.setError("Enter your username");
            return true;
        } else ed_username.setError(null);
        if (!checkPassword(password)) {
            ed_password.setError("Enter your password");
            return true;
        } else ed_password.setError(null);
        if (!checkBirthDate(birth_date)) {
            tv_birth_date.setError("Enter your birth date");
            return true;
        }
        return false;
    }

    private void initDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 16;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dialog = new DatePickerDialog(getContext(), R.style.DateTimeDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (!checkValidAge(year, month, day)) {
                    CustomToast.makeText(getContext(), "You must be at least 16 years old").show();
                    tv_birth_date.setText(null);
                    return;
                }
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                tv_birth_date.setText(date);
                tv_birth_date.setError(null);
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

    private String makeDateString(int day, int month, int year) {
        return month + " " + day + " " + year;
    }

    public interface ContinueListener {
        public void onClickContinue(String email, String full_name, String username, String password, String birth_date, String gender);
    }

    private boolean checkEmail(String email) {
        if (email.equals("")) return false;
        return true;
    }

    private boolean checkPassword(String password) {
        if (password.equals("")) return false;
        return true;
    }

    private boolean checkFullName(String full_name) {
        if (full_name.equals("")) return false;
        return true;
    }

    private boolean checkUsername(String username) {
        if (username.equals("")) return false;
        return true;
    }

    private boolean checkBirthDate(String birth_date) {
        if (birth_date.equals("")) return false;
        return true;
    }
}