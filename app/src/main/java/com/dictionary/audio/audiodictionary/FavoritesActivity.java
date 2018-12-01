package com.dictionary.audio.audiodictionary;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FavoritesActivity extends ListActivity {

    FavoritesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        mAdapter = new FavoritesAdapter(getApplicationContext());
        setListAdapter(mAdapter);
        ListView lv = getListView();
        View footer = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.favorites_footer,null,false);
        lv.addFooterView(footer);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog removealldialog = new AlertDialog.Builder(FavoritesActivity.this)
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to clear favorites?")
                        .setPositiveButton("No",null)
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"Clearing favorites!",Toast.LENGTH_LONG).show();
                                mAdapter.removeAll();
                            }
                        }).show();


            }
        });

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
            tempView = inflater.inflate(R.layout.activity_addword,parent,false);
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
