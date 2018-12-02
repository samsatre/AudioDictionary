package com.dictionary.audio.audiodictionary;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Set;

public class FavoritesActivity extends ListActivity {

    FavoritesAdapter mAdapter;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Favorites mFavorites;

    private final String MyPrefs ="DictionaryPrefs";
    SharedPreferences mSp;
    SharedPreferences.Editor mEdit;

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

                        Word word = new Word();
                        word.word = key;
                        word.definitions = new ArrayList<>();
                        word.definitions.add(faves.get(key));
                        mAdapter.add(word);
                    }
                }
                    setListAdapter(mAdapter);
                    ListView lv = getListView();
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            /*
                            TODO implement logic for viewing word in viewword. Also which dictionary
                            did the word come from?
                             */

                        }
                    });
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

class Favorites{

    HashMap<String,String> favorites;

    protected Favorites(){

        this.favorites = new HashMap<>();

    }

}
class FavoritesAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ArrayList<Word> list = new ArrayList<>();
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
        Word curr = list.get(position);
        if(convertView == null){

            holder = new ViewHolder();
            //TODO make and change layout file for this.
            tempView = inflater.inflate(R.layout.favorites_list_view,parent,false);
            holder.word = tempView.findViewById(R.id.favorite_word);
            holder.description = tempView.findViewById(R.id.favorite_description);
            tempView.setTag(holder);

        } else {

            holder = (ViewHolder) tempView.getTag();

        }
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

    }

    public void add(Word listItem){

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
