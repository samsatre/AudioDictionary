package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWord extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addword);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("audiodictionary-41731");

        Post post = new Post("12343", "Author", "Title", "Body");
        String userId = myRef.push().getKey();

        myRef.child("User").setValue(post);


    }
}
