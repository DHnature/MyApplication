package com.example.administrator.rainmusic.broadcast;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.db.MyDatabaseHelper;


public class AddCollectionBroadcast extends BroadcastReceiver {
	
	private MyDatabaseHelper databaseHelper;

	
	@Override
	public void onReceive(Context context, Intent intent) {
		databaseHelper=new MyDatabaseHelper(context, "collection", null, 1);
		SQLiteDatabase db=databaseHelper.getWritableDatabase();
		String title=intent.getStringExtra("title");
		String artist=intent.getStringExtra("artist");
		String url=intent.getStringExtra("url");
		String user=intent.getStringExtra("twb");
		int    time=intent.getIntExtra("time",0);
		int iffavorite=intent.getIntExtra("iffavorite", Constants.NOTFAVORITE);

		
		
		
		ContentValues values=new ContentValues();
		values.put("username", "twb");
		values.put("music_title",title);
		values.put("music_artist",artist);
		values.put("music_url",url);
		values.put("music_duration",time);
		values.put("music_favorite",iffavorite);
		db.insert("Userinformation1", null, values);
		values.clear();
		
	}

}
