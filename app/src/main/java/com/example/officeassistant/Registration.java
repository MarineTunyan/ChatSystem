package com.example.officeassistant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.officeassistant.services.AuthService;
import com.example.officeassistant.services.DBHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

import java.text.BreakIterator;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class Registration extends AppCompatActivity {

    private DatePicker datePicker;
    Button birtdhay;
    DatePickerDialog datePickerDialog;



    private EditText email, password, againPassword, fullName, Phone, Company;
    TextInputLayout textemail, textPassword;
    private AuthService auth = new AuthService();
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    DBHandler dbHandler = new DBHandler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        birtdhay=findViewById(R.id.birthday);

        setHideKeyboardOnTouch(this, findViewById(R.id.scrollView));
        email = findViewById(R.id.email);
        textemail = findViewById(R.id.text_input_email);
        password = findViewById(R.id.password);
        againPassword = findViewById(R.id.again_password);
        textPassword = findViewById(R.id.text_password);
        fullName = findViewById(R.id.full_name);
        Phone = findViewById(R.id.phonenumber);
        Company = findViewById(R.id.company);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (loginCompareToBoolean(email.getText().toString())) {
                    textemail.setError(null);
                } else {
                    textemail.setError("Please Enter Valid Email");
                }
            }
        });

        againPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (!againPassword.getText().toString().equals(password.getText().toString())) {
                    textPassword.setError("Passwords not equals");
                } else {
                    textPassword.setError(null);
                }

            }
        });

    }


    public void sign_up(View view) {
        if (loginCompareToBoolean(email.getText().toString())) {
            // final ProgressDialog progressDialog= ProgressDialog.show(Registration.this,"Please Wait...","Processing",true);
            auth.getAuth().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        Map<String, String> user = new HashMap<>();
                        user.put("email", email.getText().toString());
                        user.put("fullName", fullName.getText().toString());
                        user.put("phone", Phone.getText().toString());
                        user.put("company", Company.getText().toString());
                        user.put("birthday", birtdhay.getText().toString());
                        dbHandler.addUser(user, auth.getUId());
                        LoginPage.setUserId(auth.getUId());
                        Toast.makeText(Registration.this, "Registration successful", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(Registration.this, HomePage.class);
                        startActivity(i);
                    } else {
                        Log.e("ERROR", task.getException().toString());
                        Toast.makeText(Registration.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    public boolean loginCompareToBoolean(String mail) {
        boolean compare;
        if (mail.isEmpty()) {
            compare = false;
        } else {
            if (mail.trim().toLowerCase().matches(emailPattern)) {
                compare = true;
            } else {
                compare = false;
            }
        }
        return compare;
    }


    public static void setHideKeyboardOnTouch(final Context context, View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        try {
            //Set up touch listener for non-text box views to hide keyboard.
            if (!(view instanceof EditText || view instanceof ScrollView)) {

                view.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        return false;
                    }

                });
            }
            //If a layout container, iterate over children and seed recursion.
            if (view instanceof ViewGroup) {

                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                    View innerView = ((ViewGroup) view).getChildAt(i);

                    setHideKeyboardOnTouch(context, innerView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setDate(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Registration.this,android.R.style.Theme_Holo_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        if(day<10 && month<10) {
                            birtdhay.setText(("0"+day + "/" + "0"+(month + 1) + "/" + year));
                        }
                        else{
                            birtdhay.setText((day + "/" + (month + 1) + "/" + year));
                        }
                    }
                }, day, (month +1), year);

        datePickerDialog.show();
    }
}
