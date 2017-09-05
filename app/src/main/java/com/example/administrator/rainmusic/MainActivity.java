package com.example.administrator.rainmusic;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.rainmusic.config.MyApplication;
import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.model.Music;
import com.example.administrator.rainmusic.service.PlayerService;
import com.example.administrator.rainmusic.ui.activity.LoginActivity;
import com.example.administrator.rainmusic.ui.activity.MusicCollectionActivity;
import com.example.administrator.rainmusic.ui.fragment.MusicSurfaceFragment;
import com.example.administrator.rainmusic.utils.CollectionFinderUtils;
import com.example.administrator.rainmusic.utils.FindSongsUtils;
import com.example.administrator.rainmusic.weiget.RoundedImageView;
import com.example.administrator.rainmusic.weiget.SlideMenu;

import java.util.List;


public class MainActivity extends Activity implements OnItemClickListener, OnClickListener {
    private ListView listView;
    private FindSongsUtils finder;
    private SlideMenu slideMenu;
    private RemoteViews mRemoteView;
    private NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
    private NotificationManager notificationManager ;

    public static List<Music> musiclist;
    public static int count;
    public static int currentPosition = 0;

    public static List<Music> favoritemusiclist = null;
    public static int collectionMusicPosition = 0;
    public static int colletctionCount = 0;

    public static int currentMusicList = Constants.NORMALLIST;
    public static  MediaPlayer mediaplayer = new MediaPlayer();
    public static int currentPlayModel = Constants.PLAY_MODEL_SEQUENCE;
    public static Music currentMusic;
    public static CollectionFinderUtils finder2;
    public static int notificationCount = 0;

    private bottomMusicInfoBroadcast musicinfo;
    private notifyMusicInfoBroadcast notifyMusicInfo;
    private collectionUpdateBroadcast collectionUpdateBroadcast;
    private Button mskip;
    private Button menuImg;
    private Button mPlayPause;
    private Button mNext;
    private Button playModel;
    private TextView musicName;
    private TextView musicArtist;
    private TextView collection;
    private SensorManager sensorManager;
    private RoundedImageView roundedImageView;
    private View notification;


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
        setTheme(R.style.CustomTheme);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_title_bar);
        mPlayPause = (Button) findViewById(R.id.main_fm_btn_play);
        menuImg = (Button) findViewById(R.id.title_bar_menu_btn);
        mskip = (Button) findViewById(R.id.skip);
        roundedImageView = (RoundedImageView) findViewById(R.id.headImageView);
        collection = (TextView) findViewById(R.id.toolbox_collection);
        playModel = (Button) findViewById(R.id.play_model);
        musicName = (TextView) findViewById(R.id.music_name_bar);
        musicArtist = (TextView) findViewById(R.id.music_artist_bar);
        listView = (ListView) findViewById(R.id.listview);
        slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
        notification=(LinearLayout) findViewById(R.id.notification);
        mNext=(Button)findViewById(R.id.main_fm_btn_next);

        mRemoteView = new RemoteViews(this.getPackageName(), R.layout.activity_notification);
        //初始化播放按钮
        if (mediaplayer.isPlaying())
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
        else
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_pause);

        // 传感器配置
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        // 初始化收藏页面，防止空指针
        finder2 = new CollectionFinderUtils();
        favoritemusiclist = finder2.getCollectionMusic(this);
        colletctionCount = finder2.getcount();


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //加载歌曲
        finder = new FindSongsUtils();
        musiclist = finder.getMusic(MainActivity.this.getContentResolver());
        count = finder.getCount(MainActivity.this.getContentResolver());
        finder.setListAdpter(getApplicationContext(), musiclist, listView);

        //设置当前歌曲
        if (MainActivity.currentMusicList == Constants.NORMALLIST) {
            currentMusic = musiclist.get(currentPosition);
            listView.setSelection(currentPosition);
        } else {
            currentMusic = musiclist.get(collectionMusicPosition);
            listView.setSelection(collectionMusicPosition);
        }

        //底部状态栏设置当前歌曲信息
        musicName.setText(currentMusic.getTitle());
        musicArtist.setText(currentMusic.getArtist());

        //底部状态栏广播注册
        musicinfo = new bottomMusicInfoBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.mainSurface_bottom_music_statement");
        registerReceiver(musicinfo, intentFilter);

        //收藏夹更新广播注册
        collectionUpdateBroadcast = new collectionUpdateBroadcast();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("com.example.update_collection");
        registerReceiver(collectionUpdateBroadcast, intentFilter1);

        //通知栏广播注册
        notifyMusicInfo = new notifyMusicInfoBroadcast();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.example.notify_music_info_update");
        registerReceiver(notifyMusicInfo, intentFilter2);




        listView.setOnItemClickListener(this);
        mPlayPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mskip.setOnClickListener(this);
        menuImg.setOnClickListener(this);
        roundedImageView.setOnClickListener(this);
        collection.setOnClickListener(this);
        playModel.setOnClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        MainActivity.currentMusicList = Constants.COLLECTIONLIST;
        mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
        Intent intent = new Intent(MainActivity.this, PlayerService.class);
        currentPosition = position;
        MainActivity.currentMusicList=Constants.NORMALLIST;
        startService(intent);

        //打开APP第一次播放音乐时设置播放状态栏

            mRemoteView.setTextViewText(R.id.notify_music_name, currentMusic.getTitle());
            mRemoteView.setTextViewText(R.id.notify_artist_name, currentMusic.getArtist());

            Intent intentPlay = new Intent(this, PlayerService.class);
            intentPlay.putExtra("start_type", Constants.START_TYPE_OPERATION);

            intentPlay.putExtra("operation", Constants.OPEARTION_PLAY);
            PendingIntent pi = PendingIntent.getService(this, 1, intentPlay, PendingIntent.FLAG_CANCEL_CURRENT);
            mRemoteView.setOnClickPendingIntent(R.id.notify_btn_play, pi);

            intentPlay.putExtra("operation", Constants.OPEARTION_NEXT_MUSIC);
            pi = PendingIntent.getService(this, 0, intentPlay, PendingIntent.FLAG_CANCEL_CURRENT);
            mRemoteView.setOnClickPendingIntent(R.id.notify_btn_next, pi);

            Intent intent1=new Intent(MyApplication.getContext(),MainActivity.class);
            PendingIntent pi2=PendingIntent.getActivity(this,2,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteView.setOnClickPendingIntent(R.id.notification,pi2);

            mBuilder.setSmallIcon(R.drawable.ic_notification2);
            mBuilder.setContent(mRemoteView);


            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, mBuilder.build());



        notificationCount++;
        //更新下方状态栏歌曲信息

    }


    //点击事件设置
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fm_btn_play:
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                if (mediaplayer.isPlaying()) {
                    mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_pause);
                } else {
                    mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                }
                intent.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent.putExtra("operation", Constants.OPEARTION_PLAY);
                startService(intent);
                break;
            case R.id.main_fm_btn_next:
                mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                Intent intent6 = new Intent(this, PlayerService.class);
                intent6.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent6.putExtra("operation", Constants.OPEARTION_NEXT_MUSIC);
                startService(intent6);
                break;
            case R.id.play_model:
                switch (currentPlayModel) {
                    case Constants.PLAY_MODEL_SEQUENCE:
                        playModel.setBackgroundResource(R.drawable.ic_play_random);
                        Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                        currentPlayModel = Constants.PLAY_MODEL_RAMDOM;
                        break;
                    case Constants.PLAY_MODEL_RAMDOM:
                        playModel.setBackgroundResource(R.drawable.ic_play_single);
                        Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
                        currentPlayModel = Constants.PLAY_MODEL_SINGLE;
                        break;
                    case Constants.PLAY_MODEL_SINGLE:
                        playModel.setBackgroundResource(R.drawable.ic_play_sequence);
                        Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
                        currentPlayModel = Constants.PLAY_MODEL_SEQUENCE;
                        break;
                }
                break;

            case R.id.skip:
                Intent intent1 = new Intent(MainActivity.this, MusicSurfaceFragment.class);
                startActivity(intent1);
                Intent intent2 = new Intent("com.example.mediaplayer.getDurationTime");
                sendBroadcast(intent2);
                break;

            case R.id.title_bar_menu_btn:
                if (slideMenu.isMainScreenShowing()) {
                    slideMenu.openMenu();
                } else {
                    slideMenu.closeMenu();
                }
                break;

            case R.id.headImageView:
                Intent intent3 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent3);

                break;

            case R.id.toolbox_collection:
                Intent intent4 = new Intent(MainActivity.this, MusicCollectionActivity.class);
                startActivity(intent4);
                break;

            case R.id.notification:
                Intent intent5=new Intent(MyApplication.getContext(),MainActivity.class);
                startActivity(intent5);
        }
    }

    //底部播放状态栏歌曲信息广播
    private class bottomMusicInfoBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            musicName.setText(currentMusic.getTitle());
            musicArtist.setText(currentMusic.getArtist());
        }
    }


    //通知栏播放状态广播
    private class notifyMusicInfoBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

                mRemoteView.setTextViewText(R.id.notify_music_name, currentMusic.getTitle());
                mRemoteView.setTextViewText(R.id.notify_artist_name, currentMusic.getArtist());
                if (mediaplayer.isPlaying())
                    mRemoteView.setImageViewResource(R.id.notify_btn_play, R.drawable.note_btn_play);
                else
                    mRemoteView.setImageViewResource(R.id.notify_btn_play, R.drawable.note_btn_pause);
                mBuilder.setContent(mRemoteView);
                notificationManager.notify(1, mBuilder.build());


        }
    }
//更新收藏夹的歌曲
    private class collectionUpdateBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            favoritemusiclist = finder2.getCollectionMusic(MyApplication.getContext());
            colletctionCount = finder2.getcount();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaplayer.isPlaying())
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
        else
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_pause);

        musicName.setText(currentMusic.getTitle());
        musicArtist.setText(currentMusic.getArtist());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null)
            sensorManager.unregisterListener(listener);
        unregisterReceiver(musicinfo);
        unregisterReceiver(notifyMusicInfo);
        unregisterReceiver(collectionUpdateBroadcast);
        Log.d("主界面销毁", "已销毁");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mediaplayer.isPlaying())
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
        else
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_pause);

        musicName.setText(currentMusic.getTitle());
        musicArtist.setText(currentMusic.getArtist());

    }
}