package edu.uco.rnolastname.program3;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

public class MusicManager {
	private String MUSIC_PATH;
	private ArrayList<HashMap<String,String>> musicList = new ArrayList<HashMap<String,String>>();
	
	public MusicManager(String path){		
		this.MUSIC_PATH = path;
	}	
	
	/* read all mp3 and store them in ArrayList */	
	public ArrayList<HashMap<String,String>> getMusicList(){
		File home = new File(MUSIC_PATH);
		
		if(home.listFiles(new FileExtensionFilter()).length > 0){
			for(File file : home.listFiles(new FileExtensionFilter())){
				//Log.d("MUSICMANAGER_DEBUG","Song name: "+file.getName());
				HashMap<String,String> music = new HashMap<String,String>();
				music.put("music_title", file.getName().substring(0,(file.getName().length()-4)));
				music.put("music_path", file.getPath());
				
				musicList.add(music);
				//Log.d("MUSICMANAGER_DEBUG","array list size: "+musicList.toString());
			}
		}
		
		return musicList;
	}
	
	class FileExtensionFilter implements FilenameFilter{
		public boolean accept(File dir, String name){
			return (name.endsWith(".mp3")||name.endsWith(".MP3")||name.endsWith(".wav")||name.endsWith(".WAV"));
		}
	}
}
