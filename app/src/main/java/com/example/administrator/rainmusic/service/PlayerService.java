package com.example.administrator.rainmusic.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;


import com.example.administrator.rainmusic.MainActivity;
import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.model.Music;
import com.example.administrator.rainmusic.ui.activity.MusicCollectionActivity;
import com.example.administrator.rainmusic.ui.fragment.LyricFragment;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class PlayerService extends Service {


    public static final int DURATION_TYPE = 0;
    private int count;
    public static final int CURRENT_TIME_TYPE = 1;
    //更新歌词的定时器
    private Timer mTimer;
    //更新歌词的定时任务
    private TimerTask mTask;


    private List<Music> musiclist;
    private SeekBarControl seekBarControl;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int startType = intent.getIntExtra("start_type", Constants.START_TYPE_NEW_MUSIC);
        musiclist = (List<Music>) intent.getSerializableExtra("musicList");
        seekBarControl = new SeekBarControl();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.example.mediaplayer.getDurationTime");
        registerReceiver(seekBarControl, intentFilter2);

        switch (startType) {
            case Constants.START_TYPE_NEW_MUSIC:
                startNewMusic(intent);
                break;
            case Constants.START_TYPE_NEW_MUSIC_COLLECTION:
                startNewMusicOfCollection(intent);
                break;
            case Constants.START_TYPE_SEEK_TO:
                if (MainActivity.mediaplayer != null) {
                    int progress = intent.getIntExtra("progress", 0);
                    MainActivity.mediaplayer.seekTo(progress);
                }
                break;
            case Constants.START_TYPE_OPERATION: {
                int operation = intent.getIntExtra("operation", Constants.OPEARTION_PLAY);
                switch (operation) {
                    case Constants.OPEARTION_PLAY:
                        playOrPauseMusic(intent);
                        break;
                    case Constants.OPEARTION_NEXT_MUSIC:
                        if (MainActivity.mediaplayer != null) {
                            MainActivity.mediaplayer.stop();
                            MainActivity.mediaplayer = null;
                            nextMusic(intent);
                        }
                        break;
                    case Constants.OPEARTION_PREVIOUS_MUSIC:
                        if (MainActivity.mediaplayer != null) {
                            MainActivity.mediaplayer.stop();
                            MainActivity.mediaplayer = null;
                            previousMusic(intent);
                        }
                        break;
                }
            }
            break;
            default:
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }


    private void playOrPauseMusic(Intent intent) {
        if (MainActivity.mediaplayer == null) {
            startNewMusic(intent);
        } else {
            if (MainActivity.mediaplayer.isPlaying()) {
                MainActivity.mediaplayer.pause();
            } else {
                MainActivity.mediaplayer.start();

            }
            MusicThread thread = new MusicThread();
            thread.start();
        }
    }


    private void startNewMusic(Intent intent) {

        final Intent intentCircle = new Intent();
        musiclist = (List<Music>) intent.getSerializableExtra("musicList");
        intentCircle.putExtra("musicList", (Serializable) musiclist);
        MainActivity.currentMusic = musiclist.get(MainActivity.currentPosition);
        if (MainActivity.mediaplayer == null) {
            MainActivity.mediaplayer = new MediaPlayer();
        }
        MainActivity.mediaplayer.reset();
        try {
            MainActivity.mediaplayer.setDataSource(MainActivity.currentMusic.getUrl());
            MainActivity.mediaplayer.prepare();
            MainActivity.mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    MainActivity.mediaplayer.start();
                    int time = MainActivity.mediaplayer.getDuration();
                    Intent intent = new Intent("com.example.mediaplayer.musictime");
                    intent.putExtra("Mtime", time);
                    intent.putExtra("type", DURATION_TYPE);
                    sendBroadcast(intent);
                    MusicThread thread = new MusicThread();
                    thread.start();
                }
            });
            //回调接口，播放完一首歌曲自动播放下一首
            MainActivity.mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    switch (MainActivity.currentPlayModel) {
                        case Constants.PLAY_MODEL_SEQUENCE:
                            if (MainActivity.currentPosition + 1 <= MainActivity.count - 1)
                                MainActivity.currentPosition += 1;
                            else
                                MainActivity.currentPosition = 0;
                            break;
                        case Constants.PLAY_MODEL_RAMDOM:
                            Random rnd = new Random();
                            int rndint = rnd.nextInt(MainActivity.count);
                            MainActivity.currentPosition = rndint;
                            break;
                        case Constants.PLAY_MODEL_SINGLE:
                            break;
                    }
                    new Thread(new LyricFragment.lyricThread()).start();
                    Intent intent2 = new Intent("com.example.mediaplayer.changeNameOfMusicInCircle");
                    sendBroadcast(intent2);
                    startNewMusic(intentCircle);
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //该方法是为在收藏夹中播放歌曲
    private void startNewMusicOfCollection(Intent intent) {
        final Intent intentCircle = new Intent();
        musiclist = (List<Music>) intent.getSerializableExtra("musicList");
        intentCircle.putExtra("musicList", (Serializable) musiclist);
        MainActivity.currentMusic = musiclist.get(MusicCollectionActivity.currentCollectionMusicPosition);
        if (MainActivity.mediaplayer == null) {
            MainActivity.mediaplayer = new MediaPlayer();
        }
        MainActivity.mediaplayer.reset();
        try {
            MainActivity.mediaplayer.setDataSource(MainActivity.currentMusic.getUrl());
            MainActivity.mediaplayer.prepare();
            MainActivity.mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    MainActivity.mediaplayer.start();
                    //播放界面进度条显示情况
                    int time = MainActivity.mediaplayer.getDuration();
                    Intent intent = new Intent("com.example.mediaplayer.musictime");
                    intent.putExtra("Mtime", time);
                    intent.putExtra("type", DURATION_TYPE);
                    sendBroadcast(intent);
                    MusicThread thread = new MusicThread();
                    thread.start();
                }
            });
            //回调接口，播放完一首歌曲自动播放下一首
            MainActivity.mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    switch (MainActivity.currentPlayModel) {
                        case Constants.PLAY_MODEL_SEQUENCE:
                            if (MusicCollectionActivity.currentCollectionMusicPosition + 1 <= MusicCollectionActivity.count - 1)
                                MusicCollectionActivity.currentCollectionMusicPosition += 1;
                            else
                                MusicCollectionActivity.currentCollectionMusicPosition = 0;
                            break;
                        case Constants.PLAY_MODEL_RAMDOM:
                            Random rnd = new Random();
                            int rndint = rnd.nextInt(MusicCollectionActivity.count);
                            MusicCollectionActivity.currentCollectionMusicPosition = rndint;
                            break;
                        case Constants.PLAY_MODEL_SINGLE:
                            break;
                    }
                    //刷新歌曲名广播
                    Intent intent2 = new Intent("com.example.mediaplayer.changeNameOfMusicInCircle");
                    sendBroadcast(intent2);
                    //自动播放下一首歌
                    startNewMusic(intentCircle);
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void previousMusic(Intent intent) {
        count = intent.getIntExtra("count", 1);
        switch (MainActivity.currentPlayModel) {
            case Constants.PLAY_MODEL_SEQUENCE:
                if (MainActivity.currentPosition - 1 >= 0)
                    MainActivity.currentPosition -= 1;

                else
                    MainActivity.currentPosition = count - 1;

                break;
            case Constants.PLAY_MODEL_RAMDOM:
                Random rnd = new Random();
                MainActivity.currentPosition = rnd.nextInt(count - 1);

                break;
            case Constants.PLAY_MODEL_SINGLE:
                break;
        }

        startNewMusic(intent);
    }

    public void nextMusic(Intent intent) {

        count = intent.getIntExtra("count", 1);
        switch (MainActivity.currentPlayModel) {
            case Constants.PLAY_MODEL_SEQUENCE:
                if (MainActivity.currentPosition + 1 <= count - 1)
                    MainActivity.currentPosition += 1;

                else
                    MainActivity.currentPosition = 0;
                break;
            case Constants.PLAY_MODEL_RAMDOM:
                Random rnd = new Random();
                MainActivity.currentPosition = rnd.nextInt(count - 1);
                break;
            case Constants.PLAY_MODEL_SINGLE:
                break;
        }
        startNewMusic(intent);
    }

    class MusicThread extends Thread {
        @Override
        public void run() {

            while (MainActivity.mediaplayer.isPlaying()) {
                int time = MainActivity.mediaplayer.getCurrentPosition();
                Intent intent = new Intent("com.example.mediaplayer.musictime");
                intent.putExtra("time", time);
                intent.putExtra("type", CURRENT_TIME_TYPE);
                sendBroadcast(intent);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DurationThread extends Thread {

        @Override
        public void run() {
            int time = MainActivity.mediaplayer.getDuration();
            Intent intent = new Intent("com.example.mediaplayer.musictime");
            intent.putExtra("Mtime", 50);
            intent.putExtra("type", DURATION_TYPE);
            sendBroadcast(intent);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public class SeekBarControl extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DurationThread Thread = new DurationThread();
            Thread.start();


        }

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}

