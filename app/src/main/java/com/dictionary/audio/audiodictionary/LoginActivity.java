package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText mUser = findViewById(R.id.username);
        EditText mPass = findViewById(R.id.password);
        Button mLogin = findViewById(R.id.loginbutton);
        Button mSignup = findViewById(R.id.signupbutton);
        TextView mForgot = findViewById(R.id.forgotPassword);

    }

}
