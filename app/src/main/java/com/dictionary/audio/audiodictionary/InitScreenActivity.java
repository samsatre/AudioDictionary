package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class InitScreenActivity extends Activity {

    private final String STATE_PREFERRED = "preferred";
    private final String STATE_LEARN = "learn";
    private final String MyPrefs ="DictionaryPrefs";
    SharedPreferences mSp;
    SharedPreferences.Editor mEdit;
    Button mSubmitBtn;
    Spinner mPreferSpin;
    Spinner mLearnSpin;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //If users have selected preferred languages they shouldnt have to choose
        //their language again.
        mSp = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        mEdit = mSp.edit();
        if (mSp.contains(STATE_PREFERRED)) {
            //Get preferred and learning language from bundle.
            //Pass to next activity
            String loadLearn = mSp.getString(STATE_LEARN,"Not found");
            String loadPrefer = mSp.getString(STATE_PREFERRED,"not found preferred");
            Intent nextIntent = new Intent(getApplicationContext(),HomeScreenActivity.class);
            nextIntent.putExtra(STATE_LEARN, loadLearn);
            nextIntent.putExtra(STATE_PREFERRED,loadPrefer);
            startActivity(nextIntent);

        } else {
            setContentView(R.layout.activity_initial);
            mSubmitBtn = findViewById(R.id.initialSubmitButton);
            mPreferSpin = findViewById(R.id.initialSpinnerPreferred);
            mLearnSpin = findViewById(R.id.initialSpinnerLearn);
            mSubmitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mPreferSpin.getSelectedItemPosition() == 0
                            || mLearnSpin.getSelectedItemPosition() == 0) {

                        if (mPreferSpin.getSelectedItemPosition() == 0 &&
                                mLearnSpin.getSelectedItemPosition() == 0) {

                            Toast.makeText(getApplicationContext(),
                                    "Please choose preferred language and language to learn",
                                    Toast.LENGTH_LONG).show();

                        } else if (mPreferSpin.getSelectedItemPosition() == 0) {

                            Toast.makeText(getApplicationContext(),
                                    "Please choose a preferred language", Toast.LENGTH_LONG).show();

                        } else if (mLearnSpin.getSelectedItemPosition() == 0) {

                            Toast.makeText(getApplicationContext(),
                                    "Please choose the language you are learning", Toast.LENGTH_LONG).show();

                        }

                    } else {
                        //Save user choices
                        mEdit.putString(STATE_LEARN, mLearnSpin.getSelectedItem().toString());
                        mEdit.putString(STATE_PREFERRED,mPreferSpin.getSelectedItem().toString());
                        mEdit.commit();
                        //From here we will go to the next activity.
                        Intent nextIntent = new Intent(getApplicationContext(),HomeScreenActivity.class);
                        nextIntent.putExtra(STATE_LEARN, mLearnSpin.getSelectedItem().toString());
                        nextIntent.putExtra(STATE_PREFERRED,mPreferSpin.getSelectedItem().toString());
                        startActivity(nextIntent);
                        // which is probably the search bar screen.
                    }
                }
            });
        }
    }
}
