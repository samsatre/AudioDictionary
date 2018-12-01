package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.*;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    private final String temp = "f8i:^%x%ux)g)s$=5zhn?j8:e,%t_{^[.";
    EditText mUser;
    EditText mPass;
    Button mLogin;
    Button mSignup;
    TextView mForgot;
    String hash;
    EditText signupUser;
    EditText signupPass;
    EditText signupEmail;
    Dialog signupDialog;
    Button signupSubmit;
    Boolean canSignup = null;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null){

            setContentView(R.layout.activity_login);

            mUser = findViewById(R.id.username);
            mPass = findViewById(R.id.password);
            mLogin = findViewById(R.id.loginbutton);
            mSignup = findViewById(R.id.signupbutton);
            mForgot = findViewById(R.id.forgotPassword);

            //Login code
            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String temppw = mPass.getText().toString() + temp;
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        md.update(temppw.getBytes(),0,temppw.length());
                        hash = new BigInteger(1,md.digest()).toString();
                        System.out.println("login hash: " + hash);
                        mAuth.signInWithEmailAndPassword(mUser.getText().toString(),hash)
                                .addOnCompleteListener(LoginActivity.this,
                                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent initIntent = new Intent(getApplicationContext(),InitScreenActivity.class);
                                    startActivity(initIntent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this,
                                            "Authentication failed: ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }catch(NoSuchAlgorithmException e){}
                }
            });//End login code

            mSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    signupDialog = new Dialog(LoginActivity.this);
                    signupDialog.setContentView(R.layout.signup_popup);
                    signupUser = signupDialog.findViewById(R.id.signup_username);
                    signupEmail = signupDialog.findViewById(R.id.signup_email);
                    signupPass = signupDialog.findViewById(R.id.signup_password);
                    signupSubmit = signupDialog.findViewById(R.id.signup_submit_btn);

                    signupSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String temppw = signupPass.getText().toString() + temp;
                            try {
                                MessageDigest md = MessageDigest.getInstance("SHA-256");
                                md.update(temppw.getBytes(),0,temppw.length());
                                hash = new BigInteger(1,md.digest()).toString();
                            }catch(NoSuchAlgorithmException e){}
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference accountRef = database.getReference("AccountData");
                            String cleanedEmail = signupEmail.getText().toString().replaceAll("(\\.)",",");
                            DatabaseReference usernameRef = accountRef.child(signupUser.getText().toString());
                            //TODO check for prexisting accounts and other edge cases.
                            mAuth.createUserWithEmailAndPassword(signupEmail.getText().toString(),hash)
                                    .addOnCompleteListener(LoginActivity.this,
                                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(getApplicationContext(),"Signup successful!",
                                                Toast.LENGTH_LONG).show();
                                        currentUser = mAuth.getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(signupUser.getText().toString()).build();
                                        currentUser.updateProfile(profileUpdates);

                                    } else {

                                        Toast.makeText(getApplication(),"Signup failed!",Toast.LENGTH_LONG).show();


                                    }
                                }
                            });

                            signupDialog.dismiss();
                        }
                    });
                    signupDialog.show();

                }
            });

        } else{

            Intent initIntent = new Intent(getApplicationContext(),InitScreenActivity.class);
            startActivity(initIntent);

        }


    }

    public static class Account {

        public String photoURL;
        public String displayName;

        public Account(String photoURL, String displayName){

            this.photoURL = photoURL;
            this.displayName = displayName;

        }
        public Account(){

            this.photoURL = null;
            this.displayName = null;

        }

    }

}
