package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
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
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference myRef = database.getReference("AccountData");
                    DatabaseReference childRef = myRef.child(mUser.getText().toString());
                    ValueEventListener postListener = new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot){

                            Account account = dataSnapshot.getValue(Account.class);
                            if(account == null){

                                Toast.makeText(getApplicationContext(),
                                        "Username not found!",Toast.LENGTH_LONG).show();

                            }else if(hash.equals(account.Password)){
                                Toast.makeText(getApplicationContext(),
                                        "Log in successful!",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),
                                        InitScreenActivity.class);
                                startActivity(intent);

                            } else {

                                Toast.makeText(getApplicationContext(),
                                        "Wrong username and password combination!",
                                        Toast.LENGTH_LONG).show();

                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError){

                            System.out.println("Read failed: " + databaseError.getCode());

                        }

                    };
                    childRef.addListenerForSingleValueEvent(postListener);
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
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            md.update(temppw.getBytes(),0,temppw.length());
                            hash = new BigInteger(1,md.digest()).toString();
                        }catch(NoSuchAlgorithmException e){}
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference accountRef = database.getReference("AccountData");
                        //TODO check for prexisting accounts and other edge cases.
                        Map<String,String> toAdd = new HashMap<>();
                        System.out.println("signup hash: " + hash);
                        toAdd.put("Email",signupEmail.getText().toString());
                        toAdd.put("Password",hash);
                        toAdd.put("Username",signupUser.getText().toString());
                        accountRef.child(signupUser.getText().toString()).setValue(toAdd);
                        signupDialog.dismiss();
                    }
                });
                signupDialog.show();

            }
        });

    }

    public static class Account {

        public String Email;
        public String Username;
        public String Password;

        public Account(String Email, String Username, String Password){

            this.Email = Email;
            this.Username = Username;
            this.Password = Password;

        }
        public Account(){

            this.Email = null;
            this.Username = null;
            this.Password = null;

        }

    }

}
