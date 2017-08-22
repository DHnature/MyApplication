package com.example.administrator.rainmusic.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.ListView;


import com.example.administrator.rainmusic.R;
import com.example.administrator.rainmusic.adapter.MusicAdapter;
import com.example.administrator.rainmusic.model.Music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindSongsUtils {

public List<Music> getMusic(ContentResolver contentResolver){
	Cursor cursor=contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	List<Music> musicList=new ArrayList<Music>();
for(int i=0;i<cursor.getCount();i++){
	Music tempMusic=new Music();
	cursor.moveToNext();

  long id = cursor.getLong(cursor
          .getColumnIndex(MediaStore.Audio.Media._ID));

  String title = cursor.getString((cursor
          .getColumnIndex(MediaStore.Audio.Media.TITLE)));

  String artist = cursor.getString(cursor
          .getColumnIndex(MediaStore.Audio.Media.ARTIST));

  int duration = cursor.getInt(cursor
          .getColumnIndex(MediaStore.Audio.Media.DURATION));

  long size = cursor.getLong(cursor
          .getColumnIndex(MediaStore.Audio.Media.SIZE));

  String url = cursor.getString(cursor
          .getColumnIndex(MediaStore.Audio.Media.DATA));

  String album = cursor.getString(cursor
          .getColumnIndex(MediaStore.Audio.Media.ALBUM));

  long album_id = cursor.getLong(cursor
          .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

  int isMusic = cursor.getInt(cursor
          .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));


  if (isMusic != 0 && duration/(1000 * 90) >= 1){
	  tempMusic.setId(id);
	  tempMusic.setTitle(title);
	  tempMusic.setArtist(artist);
	  tempMusic.setDuration(duration);
	  tempMusic.setSize(size);
	  tempMusic.setUrl(url);
	  tempMusic.setAlbum(album);
	  tempMusic.setAlbum_id(album_id);
	  musicList.add(tempMusic);

	}

}
return musicList;


}

public void setListAdpter(Context context, List<Music> mp3Infos, ListView mMusicList) {

    List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
    MusicAdapter mAdapter = new MusicAdapter(context, R.layout.music_item,mp3Infos);
    mMusicList.setAdapter(mAdapter);

}

public  int getCount(ContentResolver contentResolver){
	Cursor cursor=contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	int count=0;
	for(int i=0;i<cursor.getCount();i++){
		Music tempMusic=new Music();
		cursor.moveToNext();
		int isMusic = cursor.getInt(cursor
		          .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
		long duration = cursor.getLong(cursor
		          .getColumnIndex(MediaStore.Audio.Media.DURATION));//???
		if (isMusic != 0 && duration/(1000 * 90) >= 1){
			count++;
		}
	}
            return count;

}
	
	

}
