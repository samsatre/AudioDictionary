package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

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

        mSavePref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEdit.putString(STATE_PREFERRED,preferSpin.getSelectedItem().toString());
                mEdit.commit();
                Toast.makeText(getApplicationContext(),"Prefered language saved!",Toast.LENGTH_LONG).show();

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

        /*
        TODO implement change username and password
         */




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
