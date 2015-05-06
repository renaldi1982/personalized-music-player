package edu.uco.rnolastname.program3;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PlaylistActivity extends ListActivity {
	private Intent i;
	public ArrayList<HashMap<String,String>> musicList = new ArrayList<HashMap<String,String>>();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_playlist); 
	    
	    Intent intent = getIntent();
	    
	    String MUSIC_PATH=intent.getStringExtra("MUSIC_PATH");
	    String name = intent.getStringExtra("NAME");
	    
	    ActionBar action = getActionBar();
	    action.setDisplayHomeAsUpEnabled(true);
	    action.setTitle("Hi " + name + "!\n here's your playlist");
	    	    	    	    	    
	    ArrayList<HashMap<String,String>> musicListData = new ArrayList<HashMap<String,String>>();
	    
	    MusicManager mm = new MusicManager(MUSIC_PATH);
	    
	    this.musicList = mm.getMusicList();
	    
	    /* loop through playlist */
	    for(int i=0; i<musicList.size();i++){
	    	HashMap<String,String> music = musicList.get(i);
	    	
	    	musicListData.add(music);
	    	//Log.d("MUSICMANAGER_DEBUG","song name: "+musicList.get(i));
	    }
	    
	    /* Adding menu items to ListView */
	    ListAdapter adapter = new SimpleAdapter(this,musicListData,
	    		R.layout.activity_playlist_item, new String[]{ "music_title"}, new int[] {R.id.music_title});
	    setListAdapter(adapter);
	    
	    /* selecting single LitsView item */
	    ListView lv = getListView();	    	   
	    
	    /* listening to click on ListView */
	    lv.setOnItemClickListener(new OnItemClickListener(){
	    	@Override
	    	public void onItemClick(AdapterView<?> parent, View v, int pos, long id){
	    		int mIndex = pos;
	    		i = new Intent();	    			    			    	
	    		i.putExtra("music_index", mIndex);	    		
	    		setResult(RESULT_OK,i);
	    		
	    		/* close the playlits */
	    		finish();
	    	}
	    });	    	   
	}
	
	public void onBackPressed(){    	
		/* if the user dont choose anything and just hit back button */ 
		i = new Intent();
	    
	    i.putExtra("RESPONSE","null");
	    setResult(RESULT_CANCELED,i);	    
    	finish();
    }
	
	@Override 
    public boolean onMenuItemSelected(int featureid, MenuItem item){
    	int id = item.getItemId();
    	switch(id){
    		case android.R.id.home:    			
    			i = new Intent();
    		    
    		    i.putExtra("RESPONSE","null");
    		    setResult(RESULT_CANCELED,i);
    			finish();   		    			
    	}
    	return true;
    }
}
