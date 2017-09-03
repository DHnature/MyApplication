package com.example.administrator.rainmusic.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.administrator.rainmusic.MainActivity;
import com.example.administrator.rainmusic.R;
import com.example.administrator.rainmusic.config.MyApplication;
import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.service.PlayerService;
import com.example.administrator.rainmusic.utils.CollectionFinderUtils;
import com.example.administrator.rainmusic.weiget.RefreshableView;

public class MusicCollectionActivity extends Activity implements OnItemClickListener {
	
	private ListView listView;
	private RefreshableView refreshableView;
	private CollectionFinderUtils finder;

	public static int count;
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
				case Constants.REFRESH:
			     finder=new CollectionFinderUtils();
			     MainActivity.favoritemusiclist=finder.getCollectionMusic(MyApplication.getContext());
				 finder.setListAdpter(MyApplication.getContext(),MainActivity.favoritemusiclist,listView);
			}
		}
		
};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  requestWindowFeature(Window.FEATURE_NO_TITLE);
          setContentView(R.layout.activity_music_collection);
          listView=(ListView)findViewById(R.id.listview);
          refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);  
          finder=new CollectionFinderUtils();
          MainActivity.favoritemusiclist=finder.getCollectionMusic(this);
          count=finder.getcount();
	      finder.setListAdpter(MyApplication.getContext(),MainActivity.favoritemusiclist,listView);
		  listView.setOnItemClickListener(this);

		  
//下拉刷新
		  refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener(){
                  @Override
			public void onRefresh() {
				try {  
                  Thread.sleep(1000);
                  Message message=new Message();
                  message.what=Constants.REFRESH;
                  handler.sendMessage(message);
                 } catch (InterruptedException e) {
                    e.printStackTrace();  
                }  
                refreshableView.finishRefreshing();  
            }  
		},0);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
         MainActivity.currentMusicList=Constants.COLLECTIONLIST;
		 Intent intent=new Intent(this, PlayerService.class);

		 MainActivity.collectionMusicPosition =position;
		 startService(intent);
}
	
	
		
	}
	


