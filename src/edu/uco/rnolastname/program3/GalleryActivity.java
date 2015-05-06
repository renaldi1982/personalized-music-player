package edu.uco.rnolastname.program3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryActivity extends Activity {
	private static String PICTURES_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getPath();
	private GridView gridView;	
	private Bitmap image;
	private String name;
	private List<Item> items = new ArrayList<Item>();
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		
		switch(id){
			case android.R.id.home:
				Intent i = new Intent(getApplicationContext(),MainActivity.class);
				setResult(RESULT_CANCELED,i);							
				finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onBackPressed(){
		Intent i = new Intent(getApplicationContext(),MainActivity.class);
		setResult(RESULT_CANCELED,i);							
		finish();
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_gallery);
	    
	    Intent i = getIntent();	   
	    if(i.getStringExtra("NAME") != null){
	    	name = i.getStringExtra("NAME").equals("null") || i.getStringExtra("NAME").equals("") ? "unknown" : i.getStringExtra("NAME");
	    }
	    
	    if(name == null) name = "unknown";	    
	    
	    ActionBar action = getActionBar();
	    action.setDisplayHomeAsUpEnabled(true);
	    action.setTitle("Hi "+ name +"!\nPick your profile picture");   
	    
	    gridView = (GridView) findViewById(R.id.gridView);
	    gridView.setAdapter(new MyAdapter(this));		
				
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),MainActivity.class);
				image = BitmapFactory.decodeFile(items.get(position).getPath(name));
				
				i.putExtra("IMAGE", image);
				setResult(RESULT_OK,i);
				finish();
			}
		});		
	}	

	private class MyAdapter extends BaseAdapter
    {
        
        private LayoutInflater inflater;
        

        public MyAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);

            getData();                       
        }
        
        private void getData() {
    		File dir = new File(PICTURES_PATH);
    		File[] files = dir.listFiles();
    		ArrayList<String> locs = new ArrayList<String>();
    		for(int i=0;i<files.length;i++){
    			locs.add(files[i].getPath());
    		}
    		    		
    		// retrieve images array
    		for (int i = 0; i < files.length; i++) {
    			BitmapFactory.Options opt = new BitmapFactory.Options();
    			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;    			    			
       			Bitmap bitmap = BitmapFactory.decodeFile(locs.get(i));
    			items.add(new Item("Image_"+i,bitmap,locs.get(i)));
    		}
    	}
        
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i)
        {
            return items.get(i);
        }

        @Override
        public long getItemId(int i)
        {
        	return 1;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View v = view;
            ImageView picture;
            TextView name;

            if(v == null)
            {
               v = inflater.inflate(R.layout.gallery_gridviewitem, viewGroup, false);
               v.setTag(R.id.picture, v.findViewById(R.id.picture));
               v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView)v.getTag(R.id.picture);
            name = (TextView)v.getTag(R.id.text);

            Item item = (Item)getItem(i);

            picture.setImageBitmap(item.bitmap);
            name.setText(item.name);

            return v;
        }       
    }
	 private class Item
	 {
	     final String name;
	     final Bitmap bitmap;
	     final String location;
	
	     Item(String name, Bitmap bmp, String path)
	     {
	         this.name = name;
	         this.bitmap = bmp;
	         this.location = path;
	     }
	     
	     public String getPath(String name){
	     	return location;
	     }
	     	
	 }
}
