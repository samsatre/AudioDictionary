package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SearchActivity extends Activity {

    private final String TAG = "AUDIODICTIONARY";

    private SearchView nativeSearch, learningSearch;
    private Button resultButton;
    private TextView resultText;
    private Button addWordButton;

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
        learningSearch = findViewById(R.id.learningSearch);

        resultText = findViewById(R.id.noResult);
        resultText.setVisibility(View.INVISIBLE);

        resultButton = findViewById(R.id.resultButton);
        resultButton.setVisibility(View.INVISIBLE);

        addWordButton = findViewById(R.id.addWordButton);
        addWordButton.setVisibility(Button.INVISIBLE);

        nativeSearch.clearFocus();
        learningSearch.clearFocus();

        // TODO: change to whatever language is selected
        nativeLanguage = "French";
        learningLanguage = "English";

        firebaseDatabase = FirebaseDatabase.getInstance();
       // nativeDatabaseReference = firebaseDatabase.getReference(nativeLanguage);
       // learningDatabaseReference = firebaseDatabase.getReference(learningLanguage);

        Intent searchIntent = getIntent();
        if (searchIntent != null && searchIntent.hasExtra(SearchManager.QUERY)) {
            String search = searchIntent.getStringExtra(SearchManager.QUERY);
            learningSearch.setQuery(search, true);
            language = learningLanguage + "-" + nativeLanguage;
            querySearched(search, language);
        }

        nativeSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                nativeSearch.clearFocus();
                language = nativeLanguage + "-" + learningLanguage;
                querySearched(s, language);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        learningSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
