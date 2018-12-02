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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class HomeScreenActivity extends Activity {

    private final String STATE_ADDED = "wordsAddedCount";
    Button addWordButton;
    Button searchButton;
    Button flashButton;
    TextView stat_msg;
    Button favoritesButton;

    private final String MyPrefs ="DictionaryPrefs";
    SharedPreferences mSp;
    SharedPreferences.Editor mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        addWordButton = findViewById(R.id.contributeButton);
        searchButton = findViewById(R.id.searchButton);
        //flashButton = findViewById(R.id.flashButton);
        stat_msg = findViewById(R.id.statText);

        mSp = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        mEdit = mSp.edit();

        int wordsAddedCount = mSp.getInt(STATE_ADDED, 0);
       // stat_msg.setText(R.string.stat_msg1 + wordsAddedCount + R.string.stat_msg2);

        addWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextIntent = new Intent(getApplicationContext(), AddWord.class);
                startActivity(nextIntent);

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(nextIntent);

            }
        });

        favoritesButton = findViewById(R.id.favoritesButton);
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent(getApplicationContext(),FavoritesActivity.class);
                startActivity(nextIntent);
            }
        });

    }

    @Override
    public void onResume(){

        super.onResume();

        stat_msg = findViewById(R.id.statText);

        mSp = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        mEdit = mSp.edit();

        int wordsAddedCount = mSp.getInt(STATE_ADDED, 0);
       // stat_msg.setText(R.string.stat_msg1 + wordsAddedCount + R.string.stat_msg2);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
