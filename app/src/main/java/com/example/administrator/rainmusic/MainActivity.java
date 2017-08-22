package com.example.administrator.rainmusic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.model.Music;
import com.example.administrator.rainmusic.service.PlayerService;
import com.example.administrator.rainmusic.ui.activity.LoginActivity;
import com.example.administrator.rainmusic.ui.activity.MusicCollectionActivity;
import com.example.administrator.rainmusic.ui.fragment.MusicSurfaceFragment;
import com.example.administrator.rainmusic.utils.CollectionFinderUtils;
import com.example.administrator.rainmusic.utils.FindSongsUtils;
import com.example.administrator.rainmusic.weiget.SlideMenu;

import java.io.Serializable;
import java.util.List;


public class MainActivity extends Activity implements OnItemClickListener, OnClickListener {
    private List<Music> musiclist;
    private ListView listView;
    private FindSongsUtils finder;
    private SlideMenu slideMenu;

    public static int count;
    public static MediaPlayer mediaplayer = new MediaPlayer();
    public static int currentPlayModel = Constants.PLAY_MODEL_SEQUENCE;
    public static Music currentMusic;
    public static int currentPosition = 0;
    public static List<Music> favoritemusiclist = null;
    public static CollectionFinderUtils finder2;

    private Button menuImg;
    private Button login;
    private Button theme;
    private Button collection;
    private Button mPlayPause;
    private Button mNext;
    private Button mPrevious;
    private Button mskip;
    private Button playModel;
    private SensorManager sensorManager;


    //传感器监听设置
    private SensorEventListener listener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            float xValue = Math.abs(event.values[0]);
            float yValue = Math.abs(event.values[1]);
            float zValue = Math.abs(event.values[2]);
            if (xValue > 20 || yValue > 20 || zValue > 20) {
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                intent.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent.putExtra("operation", Constants.OPEARTION_NEXT_MUSIC);
                intent.putExtra("currentPosition", currentPosition);
                intent.putExtra("musicList", (Serializable) musiclist);
                intent.putExtra("count", count);
                startService(intent);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_title_bar);
        mPlayPause = (Button) findViewById(R.id.main_fm_btn_play);
        mNext = (Button) findViewById(R.id.main_btn_next);
        mPrevious = (Button) findViewById(R.id.main_btn_previous);
        mskip = (Button) findViewById(R.id.skip);
        login = (Button) findViewById(R.id.login);
        theme = (Button) findViewById(R.id.theme);
        collection = (Button) findViewById(R.id.collection);
        listView = (ListView) findViewById(R.id.listview);
        slideMenu = (SlideMenu) findViewById(R.id.slide_menu);

        menuImg = (Button) findViewById(R.id.title_bar_menu_btn);
        playModel = (Button) findViewById(R.id.play_model);

        if (mediaplayer.isPlaying())
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
        else
            mPlayPause.setBackgroundResource(R.drawable.fm_btn_pause);

        // 传感器配置
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        // 初始化收藏页面，防止空指针
        finder2 = new CollectionFinderUtils();
        favoritemusiclist = finder2.getCollectionMusic(this);

        //加载歌曲
        finder = new FindSongsUtils();
        musiclist = finder.getMusic(MainActivity.this.getContentResolver());
        count = finder.getCount(MainActivity.this.getContentResolver());
        finder.setListAdpter(getApplicationContext(), musiclist, listView);
        //设置当前歌曲
        currentMusic = musiclist.get(currentPosition);
        listView.setSelection(currentPosition);

        listView.setOnItemClickListener(this);
        mPlayPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mskip.setOnClickListener(this);
        login.setOnClickListener(this);
        theme.setOnClickListener(this);
        collection.setOnClickListener(this);
        menuImg.setOnClickListener(this);
        playModel.setOnClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
        Intent intent = new Intent(MainActivity.this, PlayerService.class);
        intent.putExtra("musicList", (Serializable) musiclist);
        intent.putExtra("position", position);
        currentPosition = position;
        startService(intent);
    }


    //播放状态栏设置
    private class playstatementBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
        }
    }

    //点击事件设置
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fm_btn_play:
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                if (mediaplayer.isPlaying()) {
                    mPlayPause.setBackgroundResource(R.drawable.fm_btn_pause);
                    intent.putExtra("state", true);

                } else {
                    mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                    intent.putExtra("state", false);

                }

                intent.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent.putExtra("operation", Constants.OPEARTION_PLAY);
                intent.putExtra("currentPosition", currentPosition);
                intent.putExtra("musicList", (Serializable) musiclist);
                startService(intent);

                break;

            case R.id.main_btn_previous:
                mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                Intent intent2 = new Intent(MainActivity.this, PlayerService.class);
                intent2.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent2.putExtra("operation", Constants.OPEARTION_PREVIOUS_MUSIC);
                intent2.putExtra("currentPosition", currentPosition);
                intent2.putExtra("count", count);
                intent2.putExtra("musicList", (Serializable) musiclist);
                startService(intent2);


                break;

            case R.id.main_btn_next:
                mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                Intent intent3 = new Intent(MainActivity.this, PlayerService.class);
                intent3.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent3.putExtra("operation", Constants.OPEARTION_NEXT_MUSIC);
                intent3.putExtra("currentPosition", currentPosition);
                intent3.putExtra("musicList", (Serializable) musiclist);
                intent3.putExtra("count", count);
                startService(intent3);


                break;
            case R.id.play_model:
                Intent intent6 = new Intent("com.example.mediaplayer.MusicPlayModel");
                switch (currentPlayModel) {
                    case Constants.PLAY_MODEL_SEQUENCE:
                        playModel.setBackgroundResource(R.drawable.ic_play_random);
                        Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                        intent6.putExtra("MusicPlayModel", Constants.PLAY_MODEL_RAMDOM);
                        sendBroadcast(intent6);
                        currentPlayModel = Constants.PLAY_MODEL_RAMDOM;

                        break;
                    case Constants.PLAY_MODEL_RAMDOM:
                        playModel.setBackgroundResource(R.drawable.ic_play_single);
                        Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
                        intent6.putExtra("MusicPlayModel", Constants.PLAY_MODEL_SINGLE);
                        sendBroadcast(intent6);
                        currentPlayModel = Constants.PLAY_MODEL_SINGLE;

                        break;
                    case Constants.PLAY_MODEL_SINGLE:
                        playModel.setBackgroundResource(R.drawable.ic_play_sequence);
                        Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
                        intent6.putExtra("MusicPlayModel", Constants.PLAY_MODEL_SEQUENCE);
                        sendBroadcast(intent6);
                        currentPlayModel = Constants.PLAY_MODEL_SEQUENCE;

                        break;

                }
                break;

            case R.id.skip:
                Intent intent4 = new Intent(MainActivity.this, MusicSurfaceFragment.class);
                intent4.putExtra("currentPosition", currentPosition);
                intent4.putExtra("MusicList", (Serializable) musiclist);
                intent4.putExtra("count", count);

                startActivity(intent4);
                Intent intent44 = new Intent("com.example.mediaplayer.getDurationTime");
                sendBroadcast(intent44);
                break;

            case R.id.title_bar_menu_btn:
                if (slideMenu.isMainScreenShowing()) {
                    slideMenu.openMenu();
                } else {
                    slideMenu.closeMenu();
                }
                break;

            case R.id.login:

                Intent intent5 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent5);

                break;
            case R.id.theme:
                break;
            case R.id.collection:
                Intent intent7 = new Intent(MainActivity.this, MusicCollectionActivity.class);
                startActivity(intent7);
                break;
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null)
            sensorManager.unregisterListener(listener);
        Log.d("主界面销毁","已销毁");
    }


}