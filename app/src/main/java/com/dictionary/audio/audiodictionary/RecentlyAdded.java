package com.dictionary.audio.audiodictionary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecentlyAdded extends Activity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recently_added);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_languages);

        final List<Language> languages = new ArrayList<>();

        languages.add(new Language("English", "https://cdn.countryflags.com/thumbs/united-states-of-america/flag-800.png"));
        languages.add(new Language("French", "http://cdn.countryflags.com/thumbs/france/flag-800.png"));
        languages.add(new Language("Spanish", "http://cdn.countryflags.com/thumbs/spain/flag-800.png"));

        LanguageAdapter adapter = new LanguageAdapter(languages);
        adapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = mRecyclerView.indexOfChild(v);
                Toast.makeText(RecentlyAdded.this,"Searching " + languages.get(index).getName() + " words", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RecentlyAdded.this, LoadWords.class);
                intent.putExtra("language", languages.get(index).getName());

                startActivity(intent);
            }
        });


        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    public class LanguageAdapter extends
            RecyclerView.Adapter<LanguageAdapter.ViewHolder> {
        private View.OnClickListener mClickListener;

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView languageTextView;
            public ImageView imageView;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                languageTextView = itemView.findViewById(R.id.language_name);
                imageView = itemView.findViewById(R.id.flag_image);
            }
        }


        private List<Language> languages;

        // Pass in the contact array into the constructor
        public LanguageAdapter(List<Language> languages) {
            this.languages = languages;
        }


        @Override
        public LanguageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.language_item, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(contactView);


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onClick(v);
                }
            });

            return viewHolder;
        }

        public void setClickListener(View.OnClickListener callback) {
            mClickListener = callback;
        }


        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(LanguageAdapter.ViewHolder viewHolder, int position) {
            // Get the data model based on position
            Language language = languages.get(position);

            // Set item views based on your views and data model
            TextView textView = viewHolder.languageTextView;
            textView.setText(language.getName());
            ImageView imageView = viewHolder.imageView;
            Picasso.with(getApplicationContext()).load(language.getFlag()).resize(1500,750).into(imageView);
        }

        // Returns the total count of items in the list
        @Override
        public int getItemCount() {
            return languages.size();
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
