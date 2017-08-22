package com.example.administrator.rainmusic.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.rainmusic.db.MyDatabaseHelper;


public class RemoveCollectionBroadcast extends BroadcastReceiver {
	private MyDatabaseHelper databaseHelper;
	@Override
	public void onReceive(Context context, Intent intent) {
		databaseHelper=new MyDatabaseHelper(context, "collection", null, 1);
		SQLiteDatabase db=databaseHelper.getWritableDatabase();
		String title=intent.getStringExtra("title");
		String user=intent.getStringExtra("twb");
		db.delete("Userinformation1", "music_title=?", new String[]{title});

	//	db.delete("Userinformation1", null,null );
		
	}

}
