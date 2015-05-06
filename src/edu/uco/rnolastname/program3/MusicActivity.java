package edu.uco.rnolastname.program3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MusicActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener  {
	static final int REQUEST_PLAYLIST = 30;
	
	private static String MUSIC_PATH = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC).getPath();
	private String requestCode;
	
	private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnPrevious;
    private ImageButton btnNext;
    private ImageButton btnPlaylist;
    private ToggleButton tglShuffle;
    private Switch switchRepeat;
    
    private static SeekBar musicProgressBar;
    private TextView musicTitleLabel;
    private TextView musicCurrentDurationLabel;
    private TextView musicTotalDurationLabel;
    private TextView profile;
    private ImageView img;
    
    private static MediaPlayer mp;
    
    private Handler mHandler = new Handler();;
    private static MusicManager musicManager;
    private static Utilities utils;
    
    private int seekForwardTime = 5000; 
    private int seekBackwardTime = 5000; 
    private static int currentMusicIndex = -1;
    private boolean isShuffle = false;
    private boolean isRepeat = false;    
    private static boolean isPaused = true;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    
    private static Bitmap bitmap;
    private static String name,genre,sex;
    
    private static ArrayList<HashMap<String, String>> musicList = new ArrayList<HashMap<String,String>>();
	private static ArrayList<String> playlist = new ArrayList<String>();
	
	private Random rand;
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data){
    	super.onActivityResult(requestCode, responseCode, data);
    	if(requestCode == REQUEST_PLAYLIST && responseCode == RESULT_OK){
    		currentMusicIndex = data.getExtras().getInt("music_index");
    		
    		playMusic(currentMusicIndex);
    	}
    }                
    
    @Override
    protected void onSaveInstanceState(Bundle outState){
    	if(mp.isPlaying()){
    		//Toast.makeText(getApplicationContext(), "get to save instance state", Toast.LENGTH_SHORT).show();
    		outState.putInt("INDEX", currentMusicIndex);
    		outState.putInt("POSITION", mp.getCurrentPosition());
    		outState.putBoolean("ISPAUSED", isPaused);
    		outState.putInt("PROGRESS", musicProgressBar.getProgress());    		    		
        	        	
        	super.onSaveInstanceState(outState);
    	}
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){   
    	//Toast.makeText(getApplicationContext(), "Restore instance state", Toast.LENGTH_SHORT).show();
    	/* in case of event got destroyed ie: user change screen landscape orientation */    	    	    		    	    	
    	currentMusicIndex = savedInstanceState.getInt("INDEX");    	     	
    	int position = savedInstanceState.getInt("POSITION");    	
    	int progress = savedInstanceState.getInt("PROGRESS");
    	boolean isPaused = savedInstanceState.getBoolean("ISPAUSED");
    	//Toast.makeText(getApplicationContext(), "is paused after restore: " + isPaused, Toast.LENGTH_SHORT).show();
    	if(currentMusicIndex != -1 && !isPaused){
    		Toast.makeText(getApplicationContext(), "Resuming your music...", Toast.LENGTH_SHORT).show();
    		String title = musicList.get(currentMusicIndex).get("music_title");
    		musicTitleLabel.setText(title);
    	}
    	
    	if(progress != 0 && mp != null && !isPaused){
    		//Toast.makeText(getApplicationContext(), "progressBar " + progress, Toast.LENGTH_LONG).show();
    		musicProgressBar.setProgress(progress);
    		updateProgressBar();
    	}
    	
    	if(position != 0 && mp != null && !isPaused){    	
    		/*int a = mp.getCurrentPosition();
    		Toast.makeText(getApplicationContext(), "progress millis: " + position, Toast.LENGTH_LONG).show();
    		Toast.makeText(getApplicationContext(), "current pos: " + a, Toast.LENGTH_LONG).show();*/
    		
    		mp.seekTo(mp.getCurrentPosition());
    		mp.start();    		    		
    		isPaused = false;
    	}
    	super.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    public void onBackPressed(){    	
    	confirmBack();
    	return;
    }
    
    @Override 
    public boolean onMenuItemSelected(int featureid, MenuItem item){
    	int id = item.getItemId();
    	switch(id){
    		case android.R.id.home:    			
    			confirmBack();
    			break;    		    			
    	}
    	return true;
    }
    
    private void confirmBack(){
    	builder = new AlertDialog.Builder(this);
    	builder.setMessage(R.string.music_backbtn)
	    .setPositiveButton(R.string.music_backyes, new DialogInterface.OnClickListener(){	    			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(getApplicationContext(),MainActivity.class);
				i.putExtra("PLAYLIST", playlist);
				setResult(RESULT_OK,i);
				finish();
			}
		}).setNegativeButton(R.string.music_backno, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(mp!= null){
					mHandler.removeCallbacks(mUpdateTimeTask);					
					mp.reset();		
					mp.release();
					mp=null;
					currentMusicIndex = -1;
				}
				Intent i = new Intent(getApplicationContext(),MainActivity.class);
				i.putExtra("PLAYLIST", playlist);				
				setResult(RESULT_OK,i);
				finish();
			}					
		}).setNeutralButton(R.string.music_backcancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				return;
			}
		});    	
    	dialog = builder.create();
    	dialog.show();
    }
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_music);	    

	    Log.d("DEBUG","on create called, index: " + currentMusicIndex);	    

	    ActionBar action = getActionBar();
	    action.setDisplayHomeAsUpEnabled(true);	    	    	    	   
	    	    
    	/* reference all the objects from the XML */
	    btnPlay = (ImageButton) findViewById(R.id.btnPlay);
	    btnNext = (ImageButton) findViewById(R.id.btnNext);
	    btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
	    btnForward = (ImageButton) findViewById(R.id.btnForward);
	    btnBackward = (ImageButton) findViewById(R.id.btnBackward);
	    btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
	    tglShuffle = (ToggleButton) findViewById(R.id.btn_toggle);
	    switchRepeat = (Switch) findViewById(R.id.btn_switch);
	    
	    musicProgressBar = (SeekBar) findViewById(R.id.music_progressbar);
	    musicTitleLabel = (TextView) findViewById(R.id.music_title);
	    musicCurrentDurationLabel = (TextView) findViewById(R.id.music_currentdurationlabel);
	    musicTotalDurationLabel = (TextView) findViewById(R.id.music_totaldurationlabel);	    	    
	    img = (ImageView) findViewById(R.id.artist_image);
	    profile = (TextView) findViewById(R.id.profile);	    
	    
	    Intent i = getIntent();
	    	    	    
	    requestCode = i.getStringExtra("REQUESTCODE");
    	if(requestCode != null && requestCode.equalsIgnoreCase("MainActivity")){
	    	name = i.getStringExtra("NAME").equals("")||i.getStringExtra("NAME").equals(null) ? "anonymous" : i.getStringExtra("NAME");
		    genre = i.getStringExtra("GENRE").equals("")||i.getStringExtra("GENRE").equals(null) ? "nothing" : i.getStringExtra("GENRE");
		    sex = i.getStringExtra("PROFILESEX").equals("")||i.getStringExtra("PROFILESEX").equals(null) ? "not sure of gender" : i.getStringExtra("PROFILESEX");
		    profile.setText("Hi " + name + "\n" + "You like " + genre + "\n" + "and you are " + sex );
		    Bundle extras = i.getExtras();
		    bitmap = (Bitmap)extras.getParcelable("IMG");
		    
		    if(bitmap != null){
		    	img.setImageBitmap(bitmap);
		    }	    	    	    		    		    		    
	    }
	    	    	    
    	if(mp == null){
    		Log.d("DEBUG", "mp is null");
    		mp = new MediaPlayer();    		
    	}
	    if(savedInstanceState == null){	    		    		    			   
		    musicManager = new MusicManager(MUSIC_PATH);
		    utils = new Utilities();
		    		    		    
		    /* getting song list */
		    musicList = musicManager.getMusicList();
	    }
	   
	    /* listener */
	    musicProgressBar.setOnSeekBarChangeListener(this);
	    mp.setOnCompletionListener(this);
	    	    	    	      	    	   	    	    	   
	    btnPlaylist.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View v){	    		 		
	    		Intent i = new Intent(getApplicationContext(),PlaylistActivity.class);
	    		i.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
	    		i.putExtra("NAME", name);
	    		i.putExtra("MUSIC_PATH", MUSIC_PATH);
	    		startActivityForResult(i,REQUEST_PLAYLIST);
	    	}
	    });
	    
	    btnPlay.setOnClickListener(new OnClickListener(){	    	
	    	@Override
	    	public void onClick(View v){	    			    		
	    		if(!isPaused && currentMusicIndex != -1){		    			
	    			Log.d("DEBUG","trying to pause");
	    			Log.d("DEBUG",String.valueOf(currentMusicIndex));
	    			btnPlay.setImageResource(R.drawable.ic_music_play);	    				    		
	    			isPaused = true;
	    			mp.pause();	    			
	    		}else{
	    			if(currentMusicIndex == -1){
	    				currentMusicIndex += 1;
	    				playMusic(currentMusicIndex);    					
	    			}else{
	    				Toast.makeText(getApplicationContext(),"Music is resume",Toast.LENGTH_SHORT).show();
    					btnPlay.setImageResource(R.drawable.ic_music_pause);
	    				mp.start();
	    				isPaused = false;
	    				/* update the progress bar */
						updateProgressBar();
	    			}	    				
	    		}	    			    		
	    	}
	    });
	    
	    btnForward.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View v){
	    		int currentPos = mp.getCurrentPosition();
	    		if(currentPos + seekForwardTime <= mp.getDuration()){
	    			mp.seekTo(currentPos + seekForwardTime);
	    		}else{
	    			mp.seekTo(0);
	    		}
	    	}
	    });
	    
	    btnBackward.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View v){
	    		int currentPos = mp.getCurrentPosition();
	    		if(currentPos - seekBackwardTime >= 0){
	    			mp.seekTo(seekBackwardTime);
	    		}else{
	    			mp.seekTo(0);
	    		}
	    	}
	    });
	    
	    btnNext.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View v){
	    		if(isRepeat){
	    			playMusic(currentMusicIndex);
	    		}else if(isShuffle){
	    			//Toast.makeText(getApplicationContext(), "shuffle active btn next", Toast.LENGTH_SHORT).show();
    				rand = new Random();    				
    				currentMusicIndex = rand.nextInt((musicList.size()-1) - 0 + 1) + 0;
    				playMusic(currentMusicIndex);	    			    			
	    		}else if(currentMusicIndex < (musicList.size()-1)){
	    			playMusic(currentMusicIndex + 1);
	    			currentMusicIndex += 1;
	    		}else{
	    			playMusic(0);
	    			currentMusicIndex = 0;
	    		}
	    	}
	    });
	    
	    btnPrevious.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View v){
	    		if(isRepeat){
	    			playMusic(currentMusicIndex);
	    		}else if(isShuffle){
	    			//Toast.makeText(getApplicationContext(), "shuffle active btn next", Toast.LENGTH_SHORT).show();
    				rand = new Random();    				
    				currentMusicIndex = rand.nextInt((musicList.size()-1) - 0 + 1) + 0;
    				playMusic(currentMusicIndex);	    			    			
	    		}else if(currentMusicIndex - 1 > -1){
	    			playMusic(currentMusicIndex - 1);
	    			currentMusicIndex -= 1;
	    		}else{
	    			playMusic(0);
	    			currentMusicIndex = 0;
	    		}
	    	}
	    });
	    
	    tglShuffle.setOnClickListener(new OnClickListener(){
	    	@Override
	    	public void onClick(View v){
	    		if(isShuffle){
	    			isShuffle = false;
	    			Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
	    			tglShuffle.setChecked(false);
	    			switchRepeat.setClickable(true);
	    		}else{
	    			isShuffle = true;
	    			isRepeat = false;
	    			Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
	    			tglShuffle.setChecked(true);
	    			switchRepeat.setClickable(false);
	    		}
	    	}
	    });
	    
	    switchRepeat.setOnCheckedChangeListener(new OnCheckedChangeListener(){
	    	@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
	    		if(isRepeat){
	    			isRepeat = false;	    			
	    			Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
	    			switchRepeat.setChecked(false);
	    			tglShuffle.setClickable(true);
	    		}else{
	    			isRepeat = true;	
	    			isShuffle = false;
	    			Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
	    			switchRepeat.setChecked(true);
	    			tglShuffle.setClickable(false);
	    			
	    			
	    		}
				
			}
	    });
	}
	
	public void playMusic(int musicIndex){
		/* Play music */
				
		try{			
			Log.d("DEBUG","playing index: " + musicIndex);
			Log.d("DEBUG","is mp null: " + String.valueOf(mp==null));
			mp.reset();					
			mp.setDataSource(musicList.get(musicIndex).get("music_path"));
			mp.prepare();
			mp.start();					
			
			/* display song title */
			String title = musicList.get(musicIndex).get("music_title");
			musicTitleLabel.setText(title);
			
			btnPlay.setImageResource(R.drawable.ic_music_pause);
			musicProgressBar.setProgress(0);
			musicProgressBar.setMax(100);			
			isPaused = false;
			
			playlist.add(title);			
			/* update the progress bar */
			updateProgressBar();
																												
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void updateProgressBar(){
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}
	
	private Runnable mUpdateTimeTask = new Runnable(){
		public void run(){
			if(mp != null){
				long totalDuration = mp.getDuration();
				long currentDuration = mp.getCurrentPosition();
				
				/* displaying the current position in time */
				musicCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
				
				/* displaying the total time */
				musicTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
				
				/* update progress bar */
				int progress = (int)(utils.getProgressPercentage(currentDuration,totalDuration));
				musicProgressBar.setProgress(progress);
				
				/* running this thread after 100 milliseconds */
				mHandler.postDelayed(this,100);
			}
		}		
	};
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if(isRepeat){
			playMusic(currentMusicIndex);			
		}else if(isShuffle){
			rand = new Random();
			currentMusicIndex = rand.nextInt((musicList.size()-1) - 0 + 1) + 0;	
			playMusic(currentMusicIndex);
		}else{
			if(currentMusicIndex < (musicList.size()-1)){
				playMusic(currentMusicIndex + 1);
				currentMusicIndex += 1;				
			}else{
				playMusic(0);
				currentMusicIndex = 0;
			}
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
		/* forward or backward to certain time */
		mp.seekTo(currentPosition);
		
		/* update timer progress */
		updateProgressBar();
	}

}

