package com.example.administrator.rainmusic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	public static final String CREATE_TABLE="create table Userinformation1("
	+" id integer primary key autoincrement,"		
	+" username text, "
	+" password text, "
	+" music_title text,"
	+" music_artist text,"
	+" music_duration text,"
	+" music_url text,"
	+" music_favorite integer)"; 
	public static final String DROP_TABLE="drop table Userinformation1";
	
	public static final String CREATE_USERTABLE="create table Userinformation("
			+" id integer primary key autoincrement,"		
			+" username text, "
			+" password text)"; 
	
	
	private Context mContext;
	private int  version;
	public MyDatabaseHelper(Context context, String name,
                            CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext=context;
		version=this.version;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*switch(version){
		case 1:db.execSQL(CREATE_TABLE);
		case 2:db.execSQL(CREATE_USERTABLE);
		}*/
		db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_USERTABLE);
		
	}
	
	public void reCreate(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	
	public void delete(SQLiteDatabase db){
		db.execSQL(DROP_TABLE);
		
	}
	

}
