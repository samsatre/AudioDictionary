package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddWord extends Activity {

    public static final int RequestPermissionCode = 1;

    Button submit, recordAudio, replayBtn, retryBtn;
    EditText word, definition, sentence;
    Spinner translateTo, translateFrom;
    LinearLayout replay;


    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;

    String audioPath = null;
    String fileName = null;
    String language = null;
    Word wordData = null;
    boolean recording, addNewRow;

    private final String STATE_ADDED = "wordsAddedCount";
    private final String MyPrefs ="DictionaryPrefs";
    SharedPreferences mSp;
    SharedPreferences.Editor mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startingIntent = getIntent();

        setContentView(R.layout.activity_addword);

        word = (EditText) findViewById(R.id.word);
        definition = (EditText) findViewById(R.id.definition);
        sentence = (EditText) findViewById(R.id.sentence);
        submit = (Button) findViewById(R.id.submit);
        recordAudio = (Button) findViewById(R.id.record);
        replay = (LinearLayout) findViewById(R.id.replay_view);
        replayBtn = (Button) findViewById(R.id.replay);
        retryBtn = (Button) findViewById(R.id.rerecord);
        translateFrom = (Spinner) findViewById(R.id.translateFrom);
        translateTo = (Spinner) findViewById(R.id.translateTo);

        mSp = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        mEdit = mSp.edit();


//        language = "English";
//        language = startingIntent.getStringExtra("language");

        translateFrom.setSelection(1);
        translateFrom.setSelection(2);

        String[] options = getResources().getStringArray(R.array.translateFrom);

        int toIndex = 0, fromIndex = 0;

        for(int i=0; i<options.length; i++){

            if (options[i].equalsIgnoreCase(mSp.getString("learn", "English")))
                toIndex = i;
            if (options[i].equalsIgnoreCase(mSp.getString("preferred", "English")))
                fromIndex = i;
        }

        translateFrom.setSelection(fromIndex);
        translateTo.setSelection(toIndex);

        recording = false;
        mediaRecorder = new MediaRecorder();

        String wordFromIntent = startingIntent.getStringExtra("word");
        if (wordFromIntent != null) {
            word.setText(wordFromIntent);
        }


        recordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!recording) {

                    if(checkPermission()) {
                        recording = true;
                        Toast.makeText(AddWord.this, "Recording Started",
                                Toast.LENGTH_SHORT).show();

                        fileName = UUID.randomUUID().toString();
                        fileName = fileName.replace("-", "0");
                        audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                                "/" + fileName;

                        MediaRecorderReady();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
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
                    recordAudio.setBackgroundColor
                            (getResources().getColor(R.color.buttons, null));
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String w = word.getText().toString();
                final String d = definition.getText().toString();
                final String s = sentence.getText().toString();
                if (mSp.contains(STATE_ADDED)) {
                    int wordsAddedCount = mSp.getInt(STATE_ADDED, 0);
                    mEdit.putInt(STATE_ADDED, (wordsAddedCount+1));
                    mEdit.commit();
                } else {
                    mEdit.putInt(STATE_ADDED, 1);
                    mEdit.commit();
                }
                if(w.length() == 0) {
                    Toast.makeText(AddWord.this, "Please add a Word",
                            Toast.LENGTH_LONG).show();
                } else if (d.length() == 0) {
                    Toast.makeText(AddWord.this, "Please add a Definition",
                            Toast.LENGTH_LONG).show();
                } else if (s.length() == 0) {
                    Toast.makeText(AddWord.this, "Please add a Sentence",
                            Toast.LENGTH_LONG).show();
                } else if(audioPath == null) {
                    Toast.makeText(AddWord.this, "Please add an Audio Recording",
                            Toast.LENGTH_LONG).show();
                } else if (translateTo.getSelectedItem().toString().equalsIgnoreCase("Translate To")
                        || translateFrom.getSelectedItem().toString().equalsIgnoreCase("Translate From")) {
                    Toast.makeText(AddWord.this, "Please Select Languages",
                            Toast.LENGTH_LONG).show();
                } else {
                    addNewRow = true;
                    if(translateFrom.getSelectedItem().toString().equals(translateTo.getSelectedItem().toString())){

                        Toast.makeText(getApplicationContext(),"Make sure you two different languages are selected!",Toast.LENGTH_LONG).show();

                    }else{
                    language = translateFrom.getSelectedItem().toString() + "-" + translateTo.getSelectedItem().toString();
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference myRef = database.getReference(language);

                    final Uri file = Uri.fromFile(new File(audioPath));
                    final StorageReference riversRef = mStorageRef.child
                            (language + "/" + fileName + ".3gp");

                    DatabaseReference childRef = myRef.child(w);
                    childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if (!dataSnapshot.exists()) {
                                String uid = UUID.randomUUID().toString();
                                //Map<String, Integer> recordings = new HashMap<>();
                                List<Pair<String, Integer>> recordings = new ArrayList<>();
                                List<String> sentences = new ArrayList<>();
                                List<String> definitions = new ArrayList<>();

                                sentences.add(s);
                                definitions.add(d);
                                //recordings.put(fileName, 0);
                                recordings.add(new Pair<String, Integer>(fileName, 0));

                                wordData = new Word(uid, w, sentences, recordings, definitions);

                                myRef.child(w).setValue(wordData);
                            } else {
                                wordData = new Word(
                                        (String) dataSnapshot.child("uid").getValue(),
                                        (String) dataSnapshot.child("word").getValue(),
                                        (List<String>) dataSnapshot.child("sentences").getValue(),
                                        (List<Pair<String, Integer>>) dataSnapshot.child("recordings").getValue(),
                                        (List<String>) dataSnapshot.child("definitions").getValue()
                                );

                                wordData.definitions.add(d);
                                wordData.recordings.add(new Pair<String, Integer>(fileName, 0));
                                wordData.sentences.add(s);

                                myRef.child(w).setValue(wordData);
                            }

                            riversRef.putFile(file)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Get a URL to the uploaded content
                                            Toast.makeText(AddWord.this, "Successful Upload",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle unsuccessful uploads
                                            Toast.makeText(AddWord.this,
                                                    "Failed to Upload. Please Try Again",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                }
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
                        Toast.makeText(AddWord.this,"Permission Denied",
                                Toast.LENGTH_LONG).show();
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
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
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
                FirebaseAuth.getInstance().signOut();
                Intent nextIntent3 = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(nextIntent3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}