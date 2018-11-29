package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    private final String ENCOURAGE_MSG = "You have contributed 5 words!";
    private final String CONTRIBUTE_TXT = "Contribute More Words";
    private final String SEARCH_TXT = "Search New Words";
    private final String FLASH_TXT = "Flash Cards";

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView encourageView = findViewById(R.id.encourageText);
        Button contributeButton = findViewById(R.id.contributeButton);
        Button searchButton = findViewById(R.id.searchButton);
        Button flashButton = findViewById(R.id.flashButton);
        encourageView.setText(ENCOURAGE_MSG);
        contributeButton.setText(CONTRIBUTE_TXT);
        searchButton.setText(SEARCH_TXT);
        flashButton.setText(FLASH_TXT);

    }

}
