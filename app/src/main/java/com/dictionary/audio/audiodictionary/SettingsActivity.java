package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SettingsActivity extends Activity {

    Button mSaveBtn;
    Button mSavePref;
    Button mSaveLearn;
    Spinner preferSpin;
    Spinner learnSpin;
    SharedPreferences mSp;
    SharedPreferences.Editor mEdit;
    private final String MyPrefs ="DictionaryPrefs";
    private final String STATE_PREFERRED = "preferred";
    private final String STATE_LEARN = "learn";

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSaveBtn = findViewById(R.id.settings_save_button);
        mSavePref = findViewById(R.id.settings_prefer_btn);
        mSaveLearn = findViewById(R.id.settings_learn_btn);
        preferSpin = findViewById(R.id.nativeSpinner);
        learnSpin = findViewById(R.id.learnSpinner);
        mSp = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        mEdit = mSp.edit();
        String nativeLanguage = mSp.getString(STATE_PREFERRED,"not found prefer");
        String learningLanguage = mSp.getString(STATE_LEARN,"not found learn");
        learnSpin.setSelection(getIndex(learnSpin,learningLanguage));
        preferSpin.setSelection(getIndex(preferSpin,nativeLanguage));

        // Fill username into editText
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        TextView username_edit = findViewById(R.id.editUsernameText);
        username_edit.setText(currentUser.getDisplayName());

        mSavePref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEdit.putString(STATE_PREFERRED,preferSpin.getSelectedItem().toString());
                mEdit.commit();
                Toast.makeText(getApplicationContext(),"Preferred language saved!",Toast.LENGTH_LONG).show();

            }
        });
        mSaveLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEdit.putString(STATE_LEARN,learnSpin.getSelectedItem().toString());
                mEdit.commit();
                Toast.makeText(getApplicationContext(),"Language to learn saved!",Toast.LENGTH_LONG).show();


            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPass = ((EditText) findViewById(R.id.editOldPasswordText)).getText().toString();
                if (oldPass == null) {
                    Toast.makeText(SettingsActivity.this,
                            "Please provide your password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(currentUser.getEmail(), oldPass)
                        .addOnCompleteListener(SettingsActivity.this,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            String newPass = ((EditText) findViewById(R.id.editPasswordText)).getText().toString();
                                            String usernm = ((EditText) findViewById(R.id.editUsernameText)).getText().toString();

                                            if (newPass != null && !newPass.equals("")) {
                                                currentUser.updatePassword(newPass);
                                            }
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(usernm).build();
                                            //mAuth.updateCurrentUser(currentUser);
                                            currentUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SettingsActivity.this,
                                                            "Profile updated.", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        } else {
                                            Toast.makeText(SettingsActivity.this,
                                                    "Incorrect password.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


            }
        });

    }

    private int getIndex(Spinner spinner, String target){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(target)){
                index = i;
            }
        }
        return index;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent nextIntent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(nextIntent);
                return true;
            case R.id.action_home:
                Intent nextIntent2 = new Intent(getApplicationContext(),HomeScreenActivity.class);
                startActivity(nextIntent2);
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent nextIntent3 = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(nextIntent3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
