package com.dictionary.audio.audiodictionary;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class FavoritesActivity extends ListActivity {

    FavoritesAdapter mAdapter;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Favorites mFavorites;
    int idx;
    Word myWord;
    String currLanguage;
    ArrayList<String> languages;

    class MyWord{

        String word;
        ArrayList<String> definitions;
        String language;

        public String getWord(){

            return this.word;

        }

        public ArrayList<String> getDefinitions(){


            return this.definitions;
        }

        public String getLanguage(){

            return this.language;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mAdapter = new FavoritesAdapter(getApplicationContext());
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference users = database.getReference("Favorites");
        users.child(currentUser.getUid()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // mFavorites = dataSnapshot.getValue(Favorites.class);
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, String> faves = (HashMap<String, String>) dataSnapshot.getValue();
                    Set<String> keyList = faves.keySet();
                    for (String key : keyList) {
                        String[] temp = key.split(",");
                        MyWord word = new MyWord();
                        word.word = temp[0];
                        word.definitions = new ArrayList<>();
                        word.definitions.add(temp[1]);
                        word.language = faves.get(key);
                        mAdapter.add(word);
                    }
                }
                    setListAdapter(mAdapter);
                    ListView lv = getListView();
                    View footer = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.favorites_footer, null, false);
                    lv.addFooterView(footer);
                    footer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog removealldialog = new AlertDialog.Builder(FavoritesActivity.this)
                                    .setTitle("Warning")
                                    .setMessage("Are you sure you want to clear favorites?")
                                    .setPositiveButton("No", null)
                                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getApplicationContext(), "Clearing favorites!", Toast.LENGTH_LONG).show();
                                            mAdapter.removeAll();
                                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            final DatabaseReference users = database.getReference("Favorites");

                                            users.child(currentUser.getUid()).setValue(new HashMap<String, String>());
                                        }
                                    }).show();
                        }
                    });

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Intent viewintent = new Intent(getApplicationContext(),ViewWord.class);
                            viewintent.putExtra("word",((MyWord)mAdapter.getItem(i)).getWord());
                            viewintent.putExtra("language",((MyWord)mAdapter.getItem(i)).getLanguage());
                            startActivity(viewintent);

                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
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

class Favorites{

    HashMap<String,String> favorites;

    protected Favorites(){

        this.favorites = new HashMap<>();

    }

}
class FavoritesAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ArrayList<FavoritesActivity.MyWord> list = new ArrayList<>();
    private Context mContext;

    public FavoritesAdapter(Context context){

        mContext = context;
        inflater = LayoutInflater.from(mContext);

    }
    @Override
    public int getCount(){return list.size();}
    @Override
    public Object getItem(int position){ return list.get(position);}
    @Override
    public long getItemId(int position){ return position;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View tempView = convertView;
        ViewHolder holder;
        final FavoritesActivity.MyWord curr = list.get(position);
        if(convertView == null){

            holder = new ViewHolder();
            //TODO edit layout file to match theme
            tempView = inflater.inflate(R.layout.favorites_list_view,parent,false);
            holder.word = tempView.findViewById(R.id.favorite_word);
            holder.description = tempView.findViewById(R.id.favorite_description);
            holder.remove = tempView.findViewById(R.id.remove_button);
            tempView.setTag(holder);

        } else {

            holder = (ViewHolder) tempView.getTag();

        }
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference users = database.getReference("Favorites");
                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(curr.word+","+curr.getDefinitions().get(0)).removeValue();
                remove(curr.word);
            }
        });
        holder.language = curr.getLanguage();
        holder.word.setText(curr.getWord());

        String definition = curr.getDefinitions().get(0);
        if(definition == null){
            holder.description.setText(holder.description.getText().toString() + "No Definitions!");
        }else {
            holder.description.setText(holder.description.getText().toString() + curr.getDefinitions().get(0));
        }

        return tempView;

    }

    static class ViewHolder{

        TextView word;
        TextView description;
        String language;
        Button remove;

    }

    public void add(FavoritesActivity.MyWord listItem){

        list.add(listItem);
        notifyDataSetChanged();

    }

    public void remove(String target){

        for(int i = 0; i < list.size(); i++){

            if(list.get(i).getWord().equals(target)){

                list.remove(i);
                return;

            }

        }

    }

    public void removeAll(){

        list.clear();
        notifyDataSetChanged();


    }

}
