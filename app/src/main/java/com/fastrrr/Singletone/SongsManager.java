package com.fastrrr.Singletone;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class SongsManager {
	// SDCard Path
	//final String MEDIA_PATH = new String("/externalSdCard/");
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	Context mContext;
	// Constructor
	public SongsManager(Context mContext){
		this.mContext=mContext;
	}
	
	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList(){
		ArrayList<HashMap<String, String>> mSongsList = new ArrayList<HashMap<String, String>>();
		Cursor mCursor = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.DATA }, null, null, null);

		int count = mCursor.getCount();
		System.out.println("total no of songs are=" + count);
		HashMap<String, String> songMap;
		while (mCursor.moveToNext()) {
			songMap = new HashMap<String, String>();
			songMap.put(
					"songTitle",
					mCursor.getString(mCursor
							.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
			songMap.put("songPath", mCursor.getString(mCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
			mSongsList.add(songMap);
		}
		mCursor.close();
		return mSongsList;
	}
	
	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}
