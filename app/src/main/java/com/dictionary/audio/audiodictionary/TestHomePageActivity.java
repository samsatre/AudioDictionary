package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
public class TestHomePageActivity extends Activity{

    private final String STATE_PREFERRED = "preferred";
    private final String STATE_LEARN = "learn";

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        Intent initIntent = getIntent();
        String loadLearn = initIntent.getStringExtra(STATE_LEARN);
        String loadPref = initIntent.getStringExtra(STATE_PREFERRED);
        setContentView(R.layout.activity_testhomepage);
        TextView learnView = findViewById(R.id.testHomeLearn);
        TextView prefView = findViewById(R.id.testHomePref);
        learnView.setText(loadLearn);
        prefView.setText(loadPref);
    }

}
