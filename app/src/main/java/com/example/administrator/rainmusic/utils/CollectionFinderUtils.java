package com.example.administrator.rainmusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;


import com.example.administrator.rainmusic.R;
import com.example.administrator.rainmusic.adapter.MusicAdapter;
import com.example.administrator.rainmusic.config.MyApplication;
import com.example.administrator.rainmusic.db.MyDatabaseHelper;
import com.example.administrator.rainmusic.model.Music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CollectionFinderUtils {
	
	
	
	public List<Music> getCollectionMusic(Context context){
		
	  List<Music> musicList=new ArrayList<Music>();
	  
	  MyDatabaseHelper databaseHelper;
	  databaseHelper=new MyDatabaseHelper(context, "collection", null, 1);
	  databaseHelper.getWritableDatabase();
	  SQLiteDatabase db=databaseHelper.getWritableDatabase();
	 
	  Cursor cursor=db.query("Userinformation1",null, null, null, null, null, null);
	  if(cursor.moveToFirst()){
		  do{  Music tempmusic=new Music();
			   String queryUsername=cursor.getString(cursor.getColumnIndex("username"));
			   String queryTitle=cursor.getString(cursor.getColumnIndex("music_title"));
			   String queryArtist=cursor.getString(cursor.getColumnIndex("music_artist"));
			   int queryDuration=(int) cursor.getLong(cursor.getColumnIndex("music_duration"));
			   String queryURL=cursor.getString(cursor.getColumnIndex("music_url"));
		   if(queryUsername.equals("twb")){
				      tempmusic.setTitle(queryTitle);
					  tempmusic.setArtist(queryArtist);
					  tempmusic.setDuration(queryDuration);
					  tempmusic.setUrl(queryURL);
					  musicList.add(tempmusic);
	}
		  }while(cursor.moveToNext());
	  }
	 cursor.close();
	return musicList;
	}
	
	public void setListAdpter(Context context, List<Music> mp3Infos, ListView mMusicList) {

	    List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
	    MusicAdapter mAdapter = new MusicAdapter(context, R.layout.music_item,mp3Infos);
	    mMusicList.setAdapter(mAdapter);
	    
	}
	public int getcount(){
		int count=0;
		MyDatabaseHelper databaseHelper;
		databaseHelper=new MyDatabaseHelper(MyApplication.getContext(), "collection", null, 1);
		databaseHelper.getWritableDatabase();
		SQLiteDatabase db=databaseHelper.getWritableDatabase();
		Cursor cursor=db.query("Userinformation",null, null, null, null, null, null);
		  if(cursor.moveToFirst()){
			  do
				 count++;
				while(cursor.moveToNext());
		  }
		
		
		return count;
	}
	
	
}
