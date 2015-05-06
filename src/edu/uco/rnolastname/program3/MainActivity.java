package edu.uco.rnolastname.program3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static String PICTURES_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getPath();
	static final int REQUEST_IMAGE_CAPTURE = 10;
	static final int REQUEST_MUSIC_PLAYER = 20;
	static final int REQUEST_GALLERY= 40;	
	String[] playlist,names;
	ImageView mImageView;
	String profileSex,name,genre;	
	
	private EditText txtName;
	private AutoCompleteTextView txtGenre;
	private CheckBox chkRock,chkClassic,chkPop,chkRnb,chkJazz,chkMetal;
	private RadioGroup rdoGroup;
	private RadioButton rdoSex;
	private Bitmap imgBitmap;
	
	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent data){
		//Toast.makeText(getApplicationContext(), "reponseCode = " + responseCode + "requestCode = " + requestCode, Toast.LENGTH_SHORT).show();
		if(requestCode == REQUEST_IMAGE_CAPTURE && responseCode == RESULT_OK){
			Bundle extra = data.getExtras();
			imgBitmap = (Bitmap)extra.get("data");
			storeImage(imgBitmap);
			mImageView.setImageBitmap(imgBitmap);
		}
		
		if(requestCode == REQUEST_MUSIC_PLAYER && responseCode == RESULT_OK){
			/* get users playlist */
			ArrayList<String> playlist = data.getStringArrayListExtra("PLAYLIST");
			
			if(playlist.size()!=0){
				/* show the list of music played */
				
				return;
			}		
		}
		
		if(requestCode == REQUEST_GALLERY && responseCode == RESULT_OK){
			Toast.makeText(getApplicationContext(), "Image was selected", Toast.LENGTH_SHORT).show();
			/* set selected bitmap as picture */
			Bundle extra = data.getExtras();
			imgBitmap = (Bitmap)extra.get("IMAGE");
			mImageView.setImageBitmap(imgBitmap);
		}
	}	
	
	public void getRandomPic(View v){
		Random rand = new Random();
		int i = rand.nextInt(((19-1) - 0 + 1) + 0) + 0;
		
		File img = new File(PICTURES_PATH);
		File[] images = img.listFiles();
		imgBitmap = BitmapFactory.decodeFile(images[i].getPath());		
		mImageView.setImageBitmap(imgBitmap);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mImageView = (ImageView)findViewById(R.id.main_image);
		txtName = (EditText) findViewById(R.id.main_txtname);
		txtGenre = (AutoCompleteTextView) findViewById(R.id.main_txtgenre);
		chkRock = (CheckBox) findViewById(R.id.chk_rock);
		chkClassic = (CheckBox) findViewById(R.id.chk_classic);
		chkPop =(CheckBox) findViewById(R.id.chk_pop);
		chkRnb =(CheckBox) findViewById(R.id.chk_rnb);
		chkJazz =(CheckBox) findViewById(R.id.chk_jazz);
		chkMetal = (CheckBox) findViewById(R.id.chk_metal);
		rdoGroup = (RadioGroup)findViewById(R.id.radioSex);

		chkRock.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(chkRock.isChecked()) txtGenre.setText(txtGenre.getText().toString().trim() + " " + chkRock.getText());				
			}
			
		});
		
		chkClassic.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(chkClassic.isChecked()) txtGenre.setText(txtGenre.getText().toString().trim() + " " + chkClassic.getText());					}
			
		});
		
		chkPop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(chkPop.isChecked()) txtGenre.setText(txtGenre.getText().toString().trim() + " " + chkPop.getText());					}
			
		});
		
		chkRnb.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(chkRnb.isChecked()) txtGenre.setText(txtGenre.getText().toString().trim() + " " + chkRnb.getText());					}
			
		});
		
		chkJazz.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(chkJazz.isChecked()) txtGenre.setText(txtGenre.getText().toString().trim() + " " + chkJazz.getText());					}
			
		});
		
		chkMetal.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(chkMetal.isChecked()) txtGenre.setText(txtGenre.getText().toString().trim() + " " + chkMetal.getText());				
			}
			
		});
		
		names = getResources().getStringArray(R.array.music_genre);
		/* setup the auto complete */						
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
		txtGenre.setAdapter(adapter);
				
		/* set ontouchevent so that the keyboard get off the screen when the user press anywhere on the screen */
	    View entire_v = (View)findViewById(R.id.layout_main);
	    entire_v.setOnTouchListener(new OnTouchListener(){
	    	@Override
	    	public boolean onTouch(View v, MotionEvent e){
	    		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
	    		return true;
	    	}			
	    });
	    	    		
	    rdoGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int selectedSex = rdoGroup.getCheckedRadioButtonId();
				
				rdoSex = (RadioButton)findViewById(selectedSex);
				
				profileSex = String.valueOf(rdoSex.getText());
			}
	    	
	    });
	}
	
	public void dispatchTakePictureIntent(){
		Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//Uri savedPic = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/image" + counter));
		if(takePicIntent.resolveActivity(getPackageManager()) != null){
			//takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, savedPic);
			startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private  File getOutputMediaFile(){
	    
	    File mediaStorageDir = new File(PICTURES_PATH); 

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            return null;
	        }
	    } 
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
	    File mediaFile;
        String mImageName="image_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);  
	    return mediaFile;
	} 
	
	private void storeImage(Bitmap image) {
	    File pictureFile = getOutputMediaFile();
	    if (pictureFile == null) {
	        Log.d("DEBUG",
	                "Error creating media file, check storage permissions: ");// e.getMessage());
	        return;
	    } 
	    try {
	        FileOutputStream fos = new FileOutputStream(pictureFile);
	        image.compress(Bitmap.CompressFormat.PNG, 90, fos);
	        fos.close();
	        Toast.makeText(getApplicationContext(), "Image has been saved to: " + pictureFile.getPath(), Toast.LENGTH_LONG).show();
	    } catch (FileNotFoundException e) {
	        Log.d("DEBUG", "File not found: " + e.getMessage());
	    } catch (IOException e) {
	        Log.d("DEBUG", "Error accessing file: " + e.getMessage());
	    }  
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		switch(id){
			case R.id.item_music:
				Intent i = new Intent(MainActivity.this, MusicActivity.class);	
				
				name = txtName.getText().toString();
				genre = txtGenre.getText().toString();
				i.putExtra("NAME", name);
				i.putExtra("GENRE", genre);
				
				int rdoID = rdoGroup.getCheckedRadioButtonId();
				rdoSex = (RadioButton)findViewById(rdoID);				
				profileSex = String.valueOf(rdoSex.getText());
				i.putExtra("REQUESTCODE", "MainActivity");
				i.putExtra("PROFILESEX", profileSex);
				if(imgBitmap != null){
					Bundle extra = new Bundle();
					extra.putParcelable("IMG", imgBitmap);
					i.putExtras(extra);
				}				
				startActivityForResult(i,REQUEST_MUSIC_PLAYER);
				break;
			case R.id.item_camera:
				dispatchTakePictureIntent();
				break;
			case R.id.item_gallery:				
				Intent j = new Intent(MainActivity.this, GalleryActivity.class);
				j.putExtra("NAME", name);
				startActivityForResult(j,REQUEST_GALLERY);
				break;
		}				
		return super.onOptionsItemSelected(item);
	}
}

