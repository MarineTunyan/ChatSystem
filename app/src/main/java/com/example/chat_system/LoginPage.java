package com.example.chat_system;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.example.chat_system.services.AuthService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final boolean USER_LOGGED_IN = false;
    private static final String USER_Id= "id";

    public static void setUserLoggedIn(boolean value) {
        String TAG = "AppPref:setUserLoggedIn";
        try{
            editor.putBoolean(String.valueOf(USER_LOGGED_IN), value);
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
    }


    public static boolean getUserLoggedIn() {
        return sharedPreferences.getBoolean(String.valueOf(USER_LOGGED_IN), false);
    }

    public static void setUserId(String value) {
        String TAG = "AppPref:setUserLoggedIn";
        try{
            editor.putString(USER_Id,value );
            editor.commit();
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
    }

    public static String getUserId() {
        return sharedPreferences.getString(USER_Id,null );
    }

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    EditText email, password;
    Button signIn;
    TextInputLayout textemail;
    AuthService authService = new AuthService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = this.getSharedPreferences("Login", 0);
        if (sharedPreferences != null)
        {
            editor = sharedPreferences.edit();
        }

        if(getUserLoggedIn()){
              Intent intent = new Intent(LoginPage.this, HomePage.class);
              startActivity(intent);
          }
          else {


              setContentView(R.layout.page_login);

              setHideKeyboardOnTouch(this, findViewById(R.id.rel_layout));
              signIn = findViewById(R.id.sign_in);
              textemail = findViewById(R.id.text_input_email);
              email = findViewById(R.id.email);
              password = findViewById(R.id.password);
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
              signIn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      if (email.getText().toString().length() > 0 && password.getText().toString().length() > 0)
                          if (loginCompareToBoolean(email.getText().toString())) {
                              authService.getAuth().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                  @Override
                                  public void onComplete(@NonNull Task<AuthResult> task) {
                                      if (task.isSuccessful()) {
                                          // Sign in success, update UI with the signed-in User's information

                                          Log.d("Mash", "signInWithEmail:success");
                                          Intent intent = new Intent(LoginPage.this, HomePage.class);
                                          setUserLoggedIn(true);
                                          setUserId(authService.getUId());
                                          startActivity(intent);
                                      } else {

                                          Log.w("Mash", "signInWithEmail:failure", task.getException());
                                      }
                                  }
                              });
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

    public void sign_up(View view) {
        Intent intent1 = new Intent(LoginPage.this, Registration.class);
        startActivity(intent1);
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
}