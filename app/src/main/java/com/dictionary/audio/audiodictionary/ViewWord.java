package com.dictionary.audio.audiodictionary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.ListActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ViewWord extends ListActivity {
    TextView wordText, translation1;
    ViewListAdapter mAdapter;
    private DatabaseReference mBase;
    private DatabaseReference mTable;
    private static String word;
    List<String> definitions;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private final String MyPrefs ="DictionaryPrefs";
    SharedPreferences mSp;
    SharedPreferences.Editor mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        // String tableName = "English";
        // word = "water";

        final String tableName = intent.getStringExtra("language");
        word = intent.getStringExtra("word");
        mBase = FirebaseDatabase.getInstance().getReference();
        DataSnapshot dataSnapshot;
        mTable = mBase.child(tableName);
        //mWord = mTable.child("apple");
        //dataSnapshot = DataSnapshot(mWord,1).;
        //word = DataSnapshotmWord.child("word")..toString();
        //word = "test";
        mTable.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        readData(dataSnapshot, tableName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {Log.i("TAG", "Cancelled");}

        });
        //this section will be from the data base using the uid from the intent

    }

    private void readData(DataSnapshot dataSnapshot, String language){
        //word = dataSnapshot.child(word).getValue(Word.class).getWord();

        List a = new ArrayList();
        List<String> sentences = dataSnapshot.child(word).getValue(Word.class).getSentences();
        String uid =  dataSnapshot.child(word).getValue(Word.class).getUid();
        definitions = dataSnapshot.child(word).getValue(Word.class).getDefinitions();
        List<Pair<String, Integer>> recordings = dataSnapshot.child(word).getValue(Word.class).getRecordings();
        //Word my_word = new Word("1",word,sentences,recordings,definitions);

        //here on shouldn't have to change
        List<Item> items = new ArrayList<>(5);
        int i = 0;
        for (String s : sentences) {
            //items.add(new Item(s,(int)recordings.get("rec".concat(Integer.toString(i))),definitions.get(i)));
            Pair<String, Integer> pair = recordings.get(i);
            String def = definitions.get(i);
            items.add(new Item(s, i, pair.getSecond(),def, pair.getFirst(), uid));
            i++;
        }

        Collections.sort(items);

        mAdapter = new ViewListAdapter(this, items, language, word);
        //inflate header
        View headerView = findViewById(R.id.WordView);
        View footer = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_word_footer,null,false);
        LayoutInflater inflater = LayoutInflater.from(ViewWord.this); // 1
        View theInflatedView = inflater.inflate(R.layout.view_word, null);

        this.getListView().setHeaderDividersEnabled(true);
        this.getListView().addHeaderView(theInflatedView);
        this.getListView().addFooterView(footer);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference users = database.getReference("Favorites");
                users.child(currentUser.getUid()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null){

                            HashMap<String,String> insert = new HashMap<String,String>();
                            insert.put(word,definitions.get(0));
                            FirebaseDatabase.getInstance().getReference("Favorites").child(currentUser.getUid()).setValue(insert);
                            Toast.makeText(getApplicationContext(),"Word added to favorites!",Toast.LENGTH_LONG).show();
                        } else {

                            HashMap<String,String> insert = (HashMap<String,String>)dataSnapshot.getValue();
                            if(insert.containsKey(word)) {

                                Toast.makeText(getApplicationContext(),"Already in favorites!",Toast.LENGTH_LONG).show();

                            } else {
                                insert.put(word, definitions.get(0));
                                FirebaseDatabase.getInstance().getReference("Favorites").child(currentUser.getUid()).setValue(insert);
                                Toast.makeText(getApplicationContext(),"Word added to favorites!",Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        wordText = (TextView) this.getListView().findViewById(R.id.wordText);
        wordText.setText(word);
        // Attach the adapter to a ListView
        this.getListView().setAdapter(mAdapter);
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

