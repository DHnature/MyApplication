package com.example.administrator.rainmusic.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.rainmusic.MainActivity;
import com.example.administrator.rainmusic.R;
import com.example.administrator.rainmusic.config.MyApplication;
import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.httpservice.HttpUtil;
import com.example.administrator.rainmusic.httpservice.PicHandleUtil;
import com.example.administrator.rainmusic.interfaces.LyricHttpCallBackListener;
import com.example.administrator.rainmusic.interfaces.PicHttpCallBackListener;
import com.example.administrator.rainmusic.service.PlayerService;
import com.example.administrator.rainmusic.weiget.MusicPlayView;

import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MusicPlayFragment extends Fragment implements OnClickListener {

    private static MusicPlayView mPlayView;
    private static int mResource;
    private static int flag = 0;
    private static int finish = 0;
    private Button mPrevious;
    private Button mPlayPause;
    private Button mNext;
    private SeekBar mSeekBar;
    private TextView mTextViewDuration;
    private TextView mTextViewCurrentTime;
    private TextView title;
    private MusicBroadcast musicBroadcast;
    private picUpdateBroadcast  picUpdateBroadcast;
    private musicTitleUpdate musicName;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewroot = inflater.inflate(R.layout.fragment_music_play_surface, container, false);
        title = (TextView) viewroot.findViewById(R.id.title);
        mPlayPause = (Button) viewroot.findViewById(R.id.btn_play_pause);
        mNext = (Button) viewroot.findViewById(R.id.btn_next);
        mPrevious = (Button) viewroot.findViewById(R.id.btn_previous);
        mPlayView = (MusicPlayView) viewroot.findViewById(R.id.layout_media_play_view);
        mSeekBar = (SeekBar) viewroot.findViewById(R.id.seekbar);
        mTextViewDuration = (TextView) viewroot.findViewById(R.id.duration_time);
        mTextViewCurrentTime = (TextView) viewroot.findViewById(R.id.current_time);

        title.setText(MainActivity.currentMusic.getTitle());

//设置播放状态图标以及旋转动画
        if (MainActivity.mediaplayer.isPlaying()) {
            mPlayView.play();
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
        } else {
            mPlayView.pause();
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_pause);

        }

        new Thread(new pictureThread()).start();


/*旋转盘内设置专辑图片，若搜索错误，则使用默认图片*/



//歌曲时长
        Date dateDuration = new Date(MainActivity.currentMusic.getDuration());
        SimpleDateFormat formatDuration = new SimpleDateFormat("mm:ss");
        mTextViewDuration.setText(formatDuration.format(dateDuration));

        //设置进度条长度
        mSeekBar.setMax(MainActivity.currentMusic.getDuration());

        //歌曲播放时长监听广播注册
        musicBroadcast = new MusicBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.mediaplayer.musictime");
        getActivity().registerReceiver(musicBroadcast, intentFilter);


        //自动更新歌曲名广播注册
        musicName = new musicTitleUpdate();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.example.mediaplayer.changeNameOfMusicInCircle");
        getActivity().registerReceiver(musicName, intentFilter2);

        //
        picUpdateBroadcast=new picUpdateBroadcast();
        IntentFilter intentFilter3=new IntentFilter();
        intentFilter3.addAction("com.example.picUpdate");
        getActivity().registerReceiver(picUpdateBroadcast,intentFilter3);

        mPlayPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        //进度条滑动控制
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent = new Intent(getActivity(), PlayerService.class);
                intent.putExtra("start_type", Constants.START_TYPE_SEEK_TO);
                intent.putExtra("progress", seekBar.getProgress());
                getActivity().startService(intent);
            }
        });
        return viewroot;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause:
                Intent intent = new Intent(getActivity(), PlayerService.class);
                if (MainActivity.mediaplayer.isPlaying()) {
                    mPlayView.pause();
                    mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_pause);
                } else {
                    mPlayView.play();
                    mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                }
                intent.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent.putExtra("operation", Constants.OPEARTION_PLAY);
                getActivity().startService(intent);

                break;

            case R.id.btn_previous:
                mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                Intent intent2 = new Intent(getActivity(), PlayerService.class);
                intent2.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent2.putExtra("operation", Constants.OPEARTION_PREVIOUS_MUSIC);
                getActivity().startService(intent2);

                break;

            case R.id.btn_next:
                mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
                Intent intent3 = new Intent(getActivity(), PlayerService.class);
                intent3.putExtra("start_type", Constants.START_TYPE_OPERATION);
                intent3.putExtra("operation", Constants.OPEARTION_NEXT_MUSIC);
                getActivity().startService(intent3);



                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(musicName);
        getActivity().unregisterReceiver(musicBroadcast);
        getActivity().unregisterReceiver(picUpdateBroadcast);
        super.onDestroy();
    }

    //歌曲当前播放进度及歌曲总时长监听广播
    private class MusicBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);

            switch (type) {
                case PlayerService.DURATION_TYPE:
                    int time = intent.getIntExtra("Mtime", 50);
                    Date dateDuration = new Date(time);
                    SimpleDateFormat formatDuration = new SimpleDateFormat("mm:ss");
                    mSeekBar.setMax(time);
                    mTextViewDuration.setText(formatDuration.format(dateDuration));
                    break;
                case PlayerService.CURRENT_TIME_TYPE:
                    int currentTime = intent.getIntExtra("time", 50);
                    Date dateCurrentTime = new Date(currentTime);
                    SimpleDateFormat formatCurrent = new SimpleDateFormat("mm:ss");
                    mSeekBar.setProgress(currentTime);
                    mTextViewCurrentTime.setText(formatCurrent.format(dateCurrentTime));
                    break;
                default:
                    break;

            }
        }
 }
 //更新歌图片广播
    private class picUpdateBroadcast extends BroadcastReceiver{
     @Override
     public void onReceive(Context context, Intent intent) {
         new Thread(new pictureThread()).start();
     }
 }


    //播放页面标题更新
    private  class musicTitleUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPlayPause.setBackgroundResource(R.drawable.btn_ctrl_play);
            title.setText(MainActivity.currentMusic.getTitle());
 }
    }
//加载图片
    private class pictureThread implements Runnable {
        PicHandleUtil p1=new PicHandleUtil();
        @Override
        public void run() {
            if (p1.picCache(MainActivity.currentMusic.getTitle(), MainActivity.currentMusic.getArtist())) {
                Drawable drawable = p1.getFicFile(MainActivity.currentMusic.getTitle(), MainActivity.currentMusic.getArtist());
                mPlayView.switchImage(drawable);
            } else {
                HttpUtil.SearchMusic(MyApplication.getContext(), URLEncoder.encode(MainActivity.currentMusic.getTitle()), 10, 1, 0, new
                        LyricHttpCallBackListener() {
                            @Override
                            public void onFinish(String response) {
                                ArrayList<String> picUrl = PicHandleUtil.getPicUrl(response);
                                flag = 0;
                                for (int k = 0; k < picUrl.size() - 1 && flag == 0; k++) {
                                    String path = picUrl.get(k);
                                    //finish为了同步插入图片和循环
                                    finish = 0;
                                    PicHandleUtil.loadImageFromNetwork(path, new PicHttpCallBackListener() {
                                        @Override
                                        public void onFinish(Drawable drawable) {
                                            if (drawable != null) {
                                                p1.savePicFile(MainActivity.currentMusic.getTitle(),
                                                        MainActivity.currentMusic.getArtist(), drawable);
                                                mPlayView.switchImage(drawable);
                                                flag = 1;
                                            }
                                            finish = 1;
                                        }
                                        @Override
                                        public void onError(Exception e) {
                                            finish = 1;
                                        }
                                    });
                                    while (finish == 0) {
                                    }
                                }
                                if (flag == 0) {
                                    mResource = R.drawable.bg_default2;
                                    mPlayView.previous(mResource);
                                    if (MainActivity.mediaplayer.isPlaying())
                                        mPlayView.play();
                                }
                            }
                            @Override
                            public void onError(Exception e) {
                            }
                        });


            }
        }
    }






}
