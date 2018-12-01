package com.dictionary.audio.audiodictionary;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ViewListAdapter extends ArrayAdapter {
    private String language, word;

    public ViewListAdapter(Context context, List<Item> items, String language, String word) {
        super(context, 0, items);
        this.language = language;
        this.word = word;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Item my_item = (Item) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_view, parent, false);
        }
        // Lookup view for data population
        TextView tvTranslation = (TextView) convertView.findViewById(R.id.translationText);
        TextView tvSentence = (TextView) convertView.findViewById(R.id.sentenceText);
        Button btnPlay =(Button) convertView.findViewById(R.id.playBtn);
        final ImageView upvote = (ImageView) convertView.findViewById(R.id.upVote);
        ImageView downvote = (ImageView) convertView.findViewById(R.id.downVote);
        final TextView rating = (TextView) convertView.findViewById(R.id.rating_view);
        // Populate the data into the template view using the data object
        tvTranslation.setText(my_item.definition);
        tvSentence.setText(my_item.sentence);
        rating.setText(my_item.getVotes() + "");


        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerVote(rating,1, my_item.getIndex());

            }
        });

        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerVote(rating,-1, my_item.getIndex());
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                Log.i("TAG",language + "/" + my_item.getRecordingId()+".3gp");
                StorageReference audioRef = storageRef.child(language + "/" + my_item.getRecordingId()+".3gp");
                final long ONE_MEGABYTE = 1024 * 1024;
                //audioRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                try {
                    final File localFile = File.createTempFile("audio", "mp3");
                    audioRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Local temp file has been created
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(localFile.getPath());
                                mediaPlayer.prepare();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            mediaPlayer.start();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    public void registerVote(TextView rating, final int vote, final int position) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(language);
        final DatabaseReference childRef = myRef.child(word);

        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    List<HashMap<String, Integer>> hashMapList = (List<HashMap<String, Integer>>) dataSnapshot.child("recordings").getValue();
                    int rating = ((Number) hashMapList.get(position).get("second")).intValue();

                    hashMapList.get(position).put("second", rating+vote);

                    List<Pair<String, Integer>> scores =  new ArrayList<>();
                    for (HashMap<String, Integer> k: hashMapList){
                        scores.add(new Pair(k.get("first"), k.get("second")));
                    }

                    Word wordData = new Word(
                            (String) dataSnapshot.child("uid").getValue(),
                            (String) dataSnapshot.child("word").getValue(),
                            (List<String>) dataSnapshot.child("sentences").getValue(),
                            scores,
                            (List<String>) dataSnapshot.child("definitions").getValue());

                    myRef.child(word).setValue(wordData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        int r = Integer.parseInt(rating.getText().toString());
        rating.setText(r+vote + "");
    }

}