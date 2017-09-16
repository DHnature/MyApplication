package com.example.administrator.rainmusic.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.example.administrator.rainmusic.MainActivity;
import com.example.administrator.rainmusic.R;
import com.example.administrator.rainmusic.config.MyApplication;
import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.db.MyDatabaseHelper;
import com.example.administrator.rainmusic.model.Music;

import java.util.List;

public class MusicAdapter extends ArrayAdapter<Music> {
    private int resourceId;
    private Context mcontext;
    private List<Music> musiclist;
    private MyDatabaseHelper databaseHelper;


    public MusicAdapter(Context context, int resource, List<Music> objects) {
        super(context, resource, objects);
        mcontext = context;
        resourceId = resource;
        musiclist = objects;
    }

    @Override

// 
    public View getView(final int position, View convertView, ViewGroup parent) {
        Music music = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.music_artist = (TextView) view.findViewById(R.id.music_artist);
            viewHolder.music_title = (TextView) view.findViewById(R.id.music_title);
            viewHolder.music_duration = (TextView) view.findViewById(R.id.music_duration);
            viewHolder.music_favorite = (Button) view.findViewById(R.id.music_favorite);
            viewHolder.music_favorite.setTag(position);

            //收藏按钮点击事件
            viewHolder.music_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Music currentmusic = musiclist.get(Integer.valueOf(v.getTag().toString()));
                    if (searchIfFavorite(currentmusic) == Constants.NOTFAVORITE) {
                        v.setBackgroundResource(R.drawable.ic_like);
                        Intent intent = new Intent("com.example.mediaplayer.collection");
                        intent.putExtra("time", currentmusic.getDuration());
                        intent.putExtra("url", currentmusic.getUrl());
                        intent.putExtra("title", currentmusic.getTitle());
                        intent.putExtra("artist", currentmusic.getArtist());
                        intent.putExtra("iffavorite", Constants.FAVORITE);
                        intent.putExtra("user", "twb");
                        mcontext.sendBroadcast(intent);
                    } else {
                        v.setBackgroundResource(R.drawable.ic_cancle_like);
                        Intent intent = new Intent("com.example.mediaplayer.canclecollection");
                        intent.putExtra("title", currentmusic.getTitle());
                        intent.putExtra("user", "twb");
                        intent.putExtra("iffavorite", Constants.NOTFAVORITE);
                        mcontext.sendBroadcast(intent);
                    }
                    Intent intent = new Intent("com.example.update_collection");
                    mcontext.sendBroadcast(intent);
                }
            });
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
            viewHolder.music_favorite.setTag(position);
        }
//设置每首歌曲是否为收藏	

        if (searchIfFavorite(music) == Constants.FAVORITE) {
            viewHolder.music_favorite.setBackgroundResource(R.drawable.ic_like);
        } else
            viewHolder.music_favorite.setBackgroundResource(R.drawable.ic_cancle_like);

//设置每首歌曲显示的属性
        viewHolder.music_artist.setText(music.getArtist());
        viewHolder.music_title.setText(music.getTitle());
        viewHolder.music_duration.setText(String.valueOf(formatTime(music.getDuration())));
        if (!MainActivity.currentMusic.getTitle().equals(music.getTitle())||!MainActivity.currentMusic.
                getTitle().equals(music.getArtist())) {
            viewHolder.music_artist.setTextColor(MyApplication.getContext().getResources().getColor(R.color.music_item_color));
            viewHolder.music_title.setTextColor(MyApplication.getContext().getResources().getColor(R.color.music_item_color));
            viewHolder.music_duration.setTextColor(MyApplication.getContext().getResources().getColor(R.color.music_item_color));
        }

        return view;

    }

    //缓存控件实例
    class ViewHolder {
        TextView music_artist;
        TextView music_title;
        TextView music_duration;
        Button music_favorite;
    }

    public static String formatTime(int i) {
        String min = i / (1000 * 60) + "";
        String sec = i % (1000 * 60) + "";

        if (min.length() < 2)
            min = "0" + min;
        switch (sec.length()) {
            case 4:
                sec = "0" + sec;
                break;
            case 3:
                sec = "00" + sec;
                break;
            case 2:
                sec = "000" + sec;
                break;
            case 1:
                sec = "0000" + sec;
                break;
        }
        return min + ":" + sec.trim().substring(0, 2);/*Trim is used to delete the space in the String*/
                                                          /*substring is used to cut part String in the String */

    }


    //检查每首歌是否为收藏状态
    int searchIfFavorite(Music music) {
        databaseHelper = new MyDatabaseHelper(MyApplication.getContext(), "collection", null, 1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query("Userinformation1", null, null, null, null, null, null);
        int ifFavorite = Constants.NOTFAVORITE;
        if (cursor.moveToFirst()) {
            do {

                String temp = cursor.getString(cursor.getColumnIndex("music_title"));
                if (temp.equals(music.getTitle())) {
                    ifFavorite = Constants.FAVORITE;
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ifFavorite;
    }


}
