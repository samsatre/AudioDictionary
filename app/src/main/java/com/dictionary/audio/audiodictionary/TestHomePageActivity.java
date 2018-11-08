package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
//TODO DO NOT RUN THIS CLASS WITHOUT INIT CLASS.
public class TestHomePageActivity extends Activity{

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        Intent initIntent = getIntent();
        String loadLearn = initIntent.getStringExtra("learn");
        String loadPref = initIntent.getStringExtra("prefer");
        setContentView(R.layout.activity_testhomepage);
        TextView learnView = findViewById(R.id.testHomeLearn);
        TextView prefView = findViewById(R.id.testHomePref);
        learnView.setText(loadLearn);
        prefView.setText(loadPref);

    }

}
