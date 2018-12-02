package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SearchActivity extends Activity {

    private final String TAG = "AUDIODICTIONARY";
    private final int ENGLISH = 1;
    private final int SPANISH = 2;
    private final int FRENCH = 3;

    private SearchView nativeSearch, learningSearch;
    private Button resultButton;
    private TextView resultText;
    private Button addWordButton;
    private Spinner preferredSpinner;
    private Spinner learningSpinner;
    private final String STATE_PREFERRED = "preferred";
    private final String STATE_LEARN = "learn";
    private final String MyPrefs ="DictionaryPrefs";
    SharedPreferences mSp;
    SharedPreferences.Editor mEdit;

    private String learningLanguage = null;
    private String nativeLanguage = null;

    private String language;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference nativeDatabaseReference, learningDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_word);

        nativeSearch = findViewById(R.id.nativeSearch);
        //learningSearch = findViewById(R.id.learningSearch);

        resultText = findViewById(R.id.noResult);
        resultText.setVisibility(View.INVISIBLE);

        resultButton = findViewById(R.id.resultButton);
        resultButton.setVisibility(View.INVISIBLE);

        addWordButton = findViewById(R.id.addWordButton);
        addWordButton.setVisibility(Button.INVISIBLE);

        preferredSpinner = findViewById(R.id.search_choose_preferred);
        learningSpinner = findViewById(R.id.search_choose_learning);


        nativeSearch.clearFocus();
       // learningSearch.clearFocus();

        mSp = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        mEdit = mSp.edit();
        nativeLanguage = mSp.getString(STATE_PREFERRED,"not found prefer");
        learningLanguage = mSp.getString(STATE_LEARN,"not found learn");
        learningSpinner.setSelection(getIndex(learningSpinner,learningLanguage));
        preferredSpinner.setSelection(getIndex(preferredSpinner,nativeLanguage));


        firebaseDatabase = FirebaseDatabase.getInstance();
       // nativeDatabaseReference = firebaseDatabase.getReference(nativeLanguage);
       // learningDatabaseReference = firebaseDatabase.getReference(learningLanguage);

        Intent searchIntent = getIntent();
        if (searchIntent != null && searchIntent.hasExtra(SearchManager.QUERY)) {
            String search = searchIntent.getStringExtra(SearchManager.QUERY);
            nativeSearch.setQuery(search, true);
            language =  preferredSpinner.getSelectedItem().toString() + "-"
                    + learningSpinner.getSelectedItem().toString();
            querySearched(search, language);
        }

        nativeSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                nativeSearch.clearFocus();
                language = preferredSpinner.getSelectedItem().toString() + "-"
                        + learningSpinner.getSelectedItem().toString();
                querySearched(s, language);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        /*learningSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                learningSearch.clearFocus();
                language = learningLanguage + "-" + nativeLanguage;
                querySearched(s, language);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/
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

    private void querySearched (final String queryString, final String language) {

        databaseReference = firebaseDatabase.getReference(language);
        DatabaseReference childRef = databaseReference.child(queryString);

        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i(TAG, "word exists");

                    resultButton.setText(queryString);
                    resultButton.setVisibility(TextView.VISIBLE);

                    addWordButton.setVisibility(Button.INVISIBLE);
                    resultText.setVisibility(View.INVISIBLE);

                    resultButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent viewWordIntent = new Intent
                                    (SearchActivity.this, ViewWord.class);
                            viewWordIntent.putExtra("language", language);
                            viewWordIntent.putExtra("word", queryString);

                            startActivity(viewWordIntent);
                        }
                    });


                } else {
                    // no results in the database
                    Log.i(TAG, "word doesn't exist");

                    resultButton.setVisibility(Button.INVISIBLE);
                    resultButton.setOnClickListener(null);

                    resultText.setVisibility(TextView.VISIBLE);
                    addWordButton.setVisibility(Button.VISIBLE);

                    addWordButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent addWordIntent = new Intent
                                    (SearchActivity.this, AddWord.class);

                            addWordIntent.putExtra("language", language);
                            addWordIntent.putExtra("word", queryString);

                            startActivity(addWordIntent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {  }
        });
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
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                mSp = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
                mEdit = mSp.edit();
                mEdit.clear();
                mEdit.commit();
                Intent nextIntent3 = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(nextIntent3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
