package com.dictionary.audio.audiodictionary;

import android.content.Intent;
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
import java.util.*;

public class ViewListAdapter extends ArrayAdapter {
    public ViewListAdapter(Context context, List<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Item my_item = (Item) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_view, parent, false);
        }
        // Lookup view for data population
        TextView tvTranslation = (TextView) convertView.findViewById(R.id.translationText);
        TextView tvSentence = (TextView) convertView.findViewById(R.id.sentenceText);
        Button btnPlay =(Button) convertView.findViewById(R.id.playBtn);
        ImageView upvote = (ImageView) convertView.findViewById(R.id.upVote);
        ImageView downvote = (ImageView) convertView.findViewById(R.id.downVote);
        // Populate the data into the template view using the data object
        tvTranslation.setText(my_item.definition);
        tvSentence.setText(my_item.sentence);

        System.out.println(my_item.getRecordingId() + " ******* RECORDING");
        System.out.println(my_item.getDefinition() + " ******* RECORDING");

        // Return the completed view to render on screen
        return convertView;
    }

}