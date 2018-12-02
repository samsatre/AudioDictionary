package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LoadWords extends Activity {

    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private RecyclerView mRecyclerView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recently_added);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_languages);

        //WordAdapter adapter = new WordAdapter();

        String language = getIntent().getStringExtra("language");

        querySearched(language+"-French");

    }


    private void querySearched (final String language) {

        databaseReference = firebaseDatabase.getReference(language);
       // DatabaseReference childRef = databaseReference.getRef();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //readData(dataSnapshot);
                    System.out.println();
                    new LoadRV(dataSnapshot, language).execute();

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {  }
        });
    }


    private List<Word> readData(DataSnapshot dataSnapshot){
        List<Word> words = new ArrayList<>();

        Iterator<DataSnapshot> children = dataSnapshot.getChildren().iterator();

        Word w;
        while (children.hasNext()) {
            DataSnapshot current = children.next();
            String word = current.getValue(Word.class).getWord();
            String uid = current.getValue(Word.class).getUid();
            List<String> sentences = current.getValue(Word.class).getSentences();
            List<String> definitions = current.getValue(Word.class).getDefinitions();
            List<Pair<String, Integer>> recordings = current.getValue(Word.class).getRecordings();

            w = new Word(uid, word, sentences, recordings, definitions);
            words.add(w);
        }

        return words;
    }


    public class WordAdapter extends
            RecyclerView.Adapter<WordAdapter.ViewHolder> {

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView wordView, definitionView;
            public ImageView pronounciation;
            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                wordView = itemView.findViewById(R.id.wordText);
                definitionView = itemView.findViewById(R.id.definitionText);
                pronounciation = itemView.findViewById(R.id.speaker);
            }
        }


        private List<Word> words;
        private String language;
        // Pass in the contact array into the constructor
        public WordAdapter(List<Word> words, String language) {
            this.words = words;
            this.language = language;
        }


        @Override
        public WordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.recently_added_item, parent, false);

            // Return a new holder instance
            WordAdapter.ViewHolder viewHolder = new WordAdapter.ViewHolder(contactView);

            return viewHolder;
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            // Get the data model based on position

            String recording = "";
            int maxRating = -1;
            final Word word = words.get(position);

            for (Pair<String, Integer> p: word.getRecordings()){

                int rating = p.getSecond();
                if(rating > maxRating) {
                    maxRating = rating;
                    recording = p.getFirst();
                }
            }

            // Set item views based on your views and data model
            viewHolder.wordView.setText(word.getWord());
            viewHolder.definitionView.setText(word.getDefinitions().get(0));
            final String finalRecording = recording;
            viewHolder.pronounciation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    StorageReference storageRef = firebaseStorage.getReference();
                    StorageReference songsRef = storageRef.child(language +'/'
                            + finalRecording+".3gp");

                    try {
                        File localFile  = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS), finalRecording + ".3gp");

                        localFile .createNewFile();
                        songsRef.getFile(localFile);

                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(localFile.getAbsolutePath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        Toast.makeText(LoadWords.this, "Recording Playing",
                                Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


        }

        // Returns the total count of items in the list
        @Override
        public int getItemCount() {
            return words.size();
        }
    }

    private class LoadRV extends AsyncTask<Void, Void, Void>{

        private DataSnapshot dataSnapshot;
        private List<Word> words;
        String language;
        public LoadRV(DataSnapshot dataSnapshot, String language) {
            this.dataSnapshot = dataSnapshot;
            this.language = language;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            words = readData(dataSnapshot);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            WordAdapter adapter = new WordAdapter(words, language);
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(LoadWords.this));

        }
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
