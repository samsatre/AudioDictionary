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
import com.google.android.gms.tasks.OnSuccessListener;
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
/*
User can signup (signup leads to successful login), login, or request a "forgotten password".
Usually goes to the "init activity" as the next screen, unless the user has already set this data.
 */
public class LoginActivity extends Activity {

    EditText mUser;
    EditText mPass;
    Button mLogin;
    Button mSignup;
    TextView mForgot;
    EditText signupUser;
    EditText signupPass;
    EditText signupEmail;
    EditText confirmPass;
    EditText forgotEmail;
    Dialog signupDialog;
    Dialog forgotDialog;
    Button signupSubmit;
    Button forgotSubmit;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
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
                        //System.out.println("login hash: " + hash);
                    mAuth.signInWithEmailAndPassword(mUser.getText().toString(),mPass.getText().toString())
                            .addOnCompleteListener(LoginActivity.this,
                                    new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                     currentUser = mAuth.getCurrentUser();
                                    Intent initIntent = new Intent(getApplicationContext(),InitScreenActivity.class);
                                    startActivity(initIntent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this,
                                            "Authentication failed: ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            });//End login code

            mForgot.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view){

                    forgotDialog = new Dialog(LoginActivity.this);
                    forgotDialog.setContentView(R.layout.forgotpassword_popup);
                    forgotSubmit = forgotDialog.findViewById(R.id.forgotten_submit_btn);
                    forgotEmail = forgotDialog.findViewById(R.id.forgotten_email);

                    forgotSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mAuth.sendPasswordResetEmail(forgotEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(getApplicationContext(),"Password reset email sent to: " + forgotEmail.getText().toString(),Toast.LENGTH_LONG).show();

                                    } else {

                                        Toast.makeText(getApplicationContext(),"No such email exists!",Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                            forgotDialog.dismiss();
                        }
                    });

                    forgotDialog.show();

                }

            });

            mSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    signupDialog = new Dialog(LoginActivity.this);
                    signupDialog.setContentView(R.layout.signup_popup);
                    signupUser = signupDialog.findViewById(R.id.signup_username);
                    signupEmail = signupDialog.findViewById(R.id.signup_email);
                    signupPass = signupDialog.findViewById(R.id.signup_password);
                    signupSubmit = signupDialog.findViewById(R.id.signup_submit_btn);
                    confirmPass = signupDialog.findViewById(R.id.signup_confirm_password);

                    signupSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!signupPass.getText().toString().equals(confirmPass.getText().toString())){
                                Toast.makeText(getApplicationContext(),
                                        "Passwords do not match!",Toast.LENGTH_LONG).show();
                            } else {
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference accountRef = database.getReference("Users");
                                String cleanedEmail = signupEmail.getText().toString().replaceAll("(\\.)", ",");

                                //TODO check for prexisting accounts and other edge cases.
                                mAuth.createUserWithEmailAndPassword(signupEmail.getText().toString(), signupPass.getText().toString())
                                        .addOnCompleteListener(LoginActivity.this,
                                                new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                                        if (task.isSuccessful()) {

                                                            Toast.makeText(getApplicationContext(), "Signup successful!",
                                                                    Toast.LENGTH_LONG).show();
                                                            mAuth.signInWithEmailAndPassword(signupEmail.getText().toString(),signupPass.getText().toString())
                                                                    .addOnCompleteListener(LoginActivity.this,
                                                                            new OnCompleteListener<AuthResult>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        // Sign in success, update UI with the signed-in user's information
                                                                                        currentUser = mAuth.getCurrentUser();
                                                                                        System.out.println("is current user null? " + currentUser == null);
                                                                                        System.out.println(currentUser.getUid());
                                                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                                                .setDisplayName(signupUser.getText().toString()).build();
                                                                                        currentUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                System.out.println(currentUser.getDisplayName());
                                                                                                Intent initIntent = new Intent(getApplicationContext(),InitScreenActivity.class);
                                                                                                startActivity(initIntent);
                                                                                            }
                                                                                        });

                                                                                    }
                                                                                }
                                                                            });
                                                        } else {

                                                            Toast.makeText(getApplication(), "Signup failed!", Toast.LENGTH_LONG).show();

                                                        }
                                                    }
                                                });

                                signupDialog.dismiss();
                            }
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
