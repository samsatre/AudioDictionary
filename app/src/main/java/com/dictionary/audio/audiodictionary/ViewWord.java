package com.dictionary.audio.audiodictionary;

import android.content.Intent;
import android.os.Bundle;
import android.app.ListActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ListView;
import java.util.*;


public class ViewWord extends ListActivity {
    TextView wordText, translation1;
    ViewListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //this section will be from the data base using the uid from the intent
        Map recordings = new HashMap();
        List a = new ArrayList();
        List<String> sentences = new ArrayList<String>(5);
        List<String> definitions = new ArrayList<String>(5);
        recordings.put("rec1",1);
        sentences.add("I sat in the chair");
        definitions.add("chair");
        recordings.put("rec2",2);
        sentences.add("He sat in the seat");
        definitions.add("seat");
        recordings.put("rec1",1);
        sentences.add("I sat in the chair");
        definitions.add("chair");
        recordings.put("rec2",2);
        sentences.add("He sat in the seat");
        definitions.add("seat");
        Word my_word = new Word("1","silla",sentences,recordings,definitions);

        //here on shouldn't have to change
        List<Item> items = new ArrayList<>(5);
        int i = 0;
        for (String s : sentences) {
            //items.add(new Item(s,(int)recordings.get("rec".concat(Integer.toString(i))),definitions.get(i)));
            items.add(new Item(s,1,definitions.get(i)));
            i++;
        }
        mAdapter = new ViewListAdapter(this, items);
        //inflate header
        View headerView = findViewById(R.id.WordView);

        LayoutInflater inflater = LayoutInflater.from(ViewWord.this); // 1
        View theInflatedView = inflater.inflate(R.layout.view_word, null);

        this.getListView().setHeaderDividersEnabled(true);
        this.getListView().addHeaderView(theInflatedView);

        wordText = (TextView) this.getListView().findViewById(R.id.wordText);
        wordText.setText(my_word.word);
        // Attach the adapter to a ListView
        this.getListView().setAdapter(mAdapter);
    }
}

