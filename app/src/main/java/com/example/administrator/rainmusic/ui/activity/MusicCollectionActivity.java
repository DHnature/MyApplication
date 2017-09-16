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
import android.widget.Button;
import android.widget.ListView;

import com.example.administrator.rainmusic.MainActivity;
import com.example.administrator.rainmusic.R;
import com.example.administrator.rainmusic.config.MyApplication;
import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.service.PlayerService;
import com.example.administrator.rainmusic.utils.CollectionFinderUtils;
import com.example.administrator.rainmusic.weiget.RefreshableView;

public class MusicCollectionActivity extends Activity implements OnItemClickListener, View.OnClickListener {

    private ListView listView;
    private RefreshableView refreshableView;
    private CollectionFinderUtils finder;
    private Button mPrevious;
    private Button mPlayPause;
    private Button mNext;

    public static int count;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.REFRESH:
                    finder = new CollectionFinderUtils();
                    MainActivity.favoriteMusicList = finder.getCollectionMusic(MyApplication.getContext());
                    finder.setListAdpter(MyApplication.getContext(), MainActivity.favoriteMusicList, listView);
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomTheme);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_music_collection);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_col_title_bar);
        listView = (ListView) findViewById(R.id.listView);
        mPlayPause=(Button) findViewById(R.id.coll_btn_play_pause);
        mPrevious=(Button)findViewById(R.id.coll_btn_previous);
        mNext=(Button) findViewById(R.id.coll_btn_next);

        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        finder = new CollectionFinderUtils();
        MainActivity.favoriteMusicList = finder.getCollectionMusic(this);
        count = finder.getcount();
        finder.setListAdpter(MyApplication.getContext(), MainActivity.favoriteMusicList, listView);

        listView.setOnItemClickListener(this);
        mPlayPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);

//下拉刷新
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    Thread.sleep(1000);
                    Message message = new Message();
                    message.what = Constants.REFRESH;
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshableView.finishRefreshing();
            }
        }, 0);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        MainActivity.currentMusicList = Constants.COLLECTIONLIST;
        Intent intent = new Intent(this, PlayerService.class);
        MainActivity.collectionMusicPosition = position;
        startService(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.coll_btn_play_pause:
                Intent intent = new Intent(this, PlayerService.class);
                if (MainActivity.mediaplayer.isPlaying()) {
                    mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_pause);
                } else {
                    mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                }
                intent.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent.putExtra("operation", Constants.OPEARTION_PLAY);
                startService(intent);
                break;
            case R.id.coll_btn_previous:
                mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                Intent intent2 = new Intent(this, PlayerService.class);
                intent2.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent2.putExtra("operation", Constants.OPEARTION_PREVIOUS_MUSIC);
                startService(intent2);
                break;
            case R.id.coll_btn_next:
                mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                Intent intent3 = new Intent(this, PlayerService.class);
                intent3.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent3.putExtra("operation", Constants.OPEARTION_NEXT_MUSIC);
                startService(intent3);
                break;
        }


    }
}



