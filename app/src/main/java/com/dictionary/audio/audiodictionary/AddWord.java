package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddWord extends Activity {

    public static final int RequestPermissionCode = 1;

    Button addWord, recordAudio, replayBtn, retryBtn;
    EditText word, definition;
    LinearLayout replay;


    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;

    String audioPath = null;
    boolean recording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addword);

        word = (EditText) findViewById(R.id.word);
        definition = (EditText) findViewById(R.id.definition);
        addWord = (Button) findViewById(R.id.submit);
        recordAudio = (Button) findViewById(R.id.record);
        replay = (LinearLayout) findViewById(R.id.replay_view);
        replayBtn = (Button) findViewById(R.id.replay);
        retryBtn = (Button) findViewById(R.id.rerecord);

        recording = false;
        mediaRecorder = new MediaRecorder();

        /*
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Words");

        String uid = UUID.randomUUID().toString();
        String word = "Computer";
        List<String> sentences = new ArrayList<>();
        Map<String, Integer> recordings = new HashMap<>();

        sentences.add("A Computer was invented by Bill Gates");
        sentences.add("Computers are very, very fast");
        sentences.add("What started big but now are small are computers");
        recordings.put("someUrlKey1", 3);
        recordings.put("someUrlKey2", 9);
        String definition = "Computers are..... fun";


        Word post = new Word(uid, word, sentences, recordings, definition);

        myRef.child("English").setValue(post);
        */

        recordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!recording) {

                    if(checkPermission()) {
                        recording = true;
                        Toast.makeText(AddWord.this, "Recording Started", Toast.LENGTH_SHORT).show();

                        audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                UUID.randomUUID().toString() + "AudioRecording.3gp";

                        MediaRecorderReady();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        recordAudio.setText(R.string.record_stop);
                        recordAudio.setBackgroundColor(Color.RED);
                    } else {
                        requestPermission();
                    }

                } else {
                    recording = false;
                    mediaRecorder.stop();

                    recordAudio.setText(R.string.record_start);
                    recordAudio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    recordAudio.setVisibility(View.GONE);
                    replay.setVisibility(View.VISIBLE);
                }
            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replay.setVisibility(View.GONE);
                recordAudio.setVisibility(View.VISIBLE);
            }
        });

        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioPath);
                    mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(AddWord.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audioPath);
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(AddWord.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(AddWord.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();

                        recordAudio.performClick();

                    } else {
                        Toast.makeText(AddWord.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

}
