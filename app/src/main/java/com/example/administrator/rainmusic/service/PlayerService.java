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
import com.example.administrator.rainmusic.ui.fragment.LyricFragment;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class PlayerService extends Service {


    public static final int DURATION_TYPE = 0;
    public static final int CURRENT_TIME_TYPE = 1;
    //更新歌词的定时器
    private Timer mTimer;
    //更新歌词的定时任务
    private TimerTask mTask;


    private List<Music> musiclist;
    private int position = 0;


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
                            nextMusic(intent);
                        }
                        break;
                    case Constants.OPEARTION_PREVIOUS_MUSIC:
                        if (MainActivity.mediaplayer != null) {
                            MainActivity.mediaplayer.stop();
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
            Intent intent1= new Intent("com.example.notify_music_info_update");
            sendBroadcast(intent1);
            MusicThread thread = new MusicThread();
            thread.start();
        }
    }


    private void startNewMusic(final Intent intent) {
        if (MainActivity.currentMusicList == Constants.NORMALLIST) {
            musiclist = MainActivity.musiclist;
            position = MainActivity.currentPosition;
        } else {
            musiclist = MainActivity.favoritemusiclist;
            position = MainActivity.collectionMusicPosition;
        }
        MainActivity.currentMusic = musiclist.get(position);
        Intent intent1 = new Intent("com.example.mainSurface_bottom_music_statement");
        sendBroadcast(intent1);
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
                    //发送歌曲时长广播
                    Intent intent = new Intent("com.example.mediaplayer.musictime");
                    intent.putExtra("Mtime", time);
                    intent.putExtra("type", DURATION_TYPE);
                    sendBroadcast(intent);
                    //发送消息栏广播
                    Intent intent1 = new Intent("com.example.notify_music_info_update");
                    sendBroadcast(intent1);

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
                            if (MainActivity.currentMusicList == Constants.NORMALLIST) {
                                if (MainActivity.currentPosition + 1 <= MainActivity.count - 1)
                                    MainActivity.currentPosition += 1;
                                else
                                    MainActivity.currentPosition = 0;
                            } else {
                                if (MainActivity.collectionMusicPosition + 1 <= MainActivity.colletctionCount - 1)
                                    MainActivity.collectionMusicPosition += 1;
                                else
                                    MainActivity.collectionMusicPosition = 0;
                            }
                            break;
                        case Constants.PLAY_MODEL_RAMDOM:
                            Random rnd = new Random();
                            int rndint = rnd.nextInt(MainActivity.count);
                            if (MainActivity.currentMusicList == Constants.NORMALLIST)
                                MainActivity.currentPosition = rndint;
                            else
                                MainActivity.collectionMusicPosition = rndint;
                            break;
                        case Constants.PLAY_MODEL_SINGLE:
                            break;
                    }
                    new Thread(new LyricFragment.lyricThread()).start();
                    Intent intent2 = new Intent("com.example.mediaplayer.changeNameOfMusicInCircle");
                    sendBroadcast(intent2);

                    startNewMusic(intent);
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void previousMusic(Intent intent) {
        if (MainActivity.currentMusicList == Constants.NORMALLIST) {
            switch (MainActivity.currentPlayModel) {
                case Constants.PLAY_MODEL_SEQUENCE:
                    if (MainActivity.currentPosition - 1 >= 0)
                        MainActivity.currentPosition -= 1;
                    else
                        MainActivity.currentPosition = MainActivity.count - 1;
                    break;
                case Constants.PLAY_MODEL_RAMDOM:
                    Random rnd = new Random();
                    MainActivity.currentPosition = rnd.nextInt(MainActivity.count - 1);
                    break;
                case Constants.PLAY_MODEL_SINGLE:
                    break;
            }
        } else {
            switch (MainActivity.currentPlayModel) {
                case Constants.PLAY_MODEL_SEQUENCE:
                    if (MainActivity.collectionMusicPosition - 1 >= 0)
                        MainActivity.collectionMusicPosition -= 1;
                    else
                        MainActivity.collectionMusicPosition = MainActivity.colletctionCount - 1;
                    break;
                case Constants.PLAY_MODEL_RAMDOM:
                    Random rnd = new Random();
                    MainActivity.collectionMusicPosition = rnd.nextInt(MainActivity.colletctionCount - 1);
                    break;
                case Constants.PLAY_MODEL_SINGLE:
                    break;
            }
        }
        startNewMusic(intent);
    }

    public void nextMusic(Intent intent) {
        if (MainActivity.currentMusicList == Constants.NORMALLIST) {
            switch (MainActivity.currentPlayModel) {
                case Constants.PLAY_MODEL_SEQUENCE:
                    if (MainActivity.currentPosition + 1 <= MainActivity.count - 1)
                        MainActivity.currentPosition += 1;

                    else
                        MainActivity.currentPosition = 0;
                    break;
                case Constants.PLAY_MODEL_RAMDOM:
                    Random rnd = new Random();
                    MainActivity.currentPosition = rnd.nextInt(MainActivity.count - 1);
                    break;
                case Constants.PLAY_MODEL_SINGLE:
                    break;
            }
        }
        else {

            switch (MainActivity.currentPlayModel) {
                case Constants.PLAY_MODEL_SEQUENCE:
                    if (MainActivity.collectionMusicPosition + 1 <= MainActivity.colletctionCount - 1)
                        MainActivity.collectionMusicPosition += 1;

                    else
                        MainActivity.collectionMusicPosition = 0;
                    break;
                case Constants.PLAY_MODEL_RAMDOM:
                    Random rnd = new Random();
                    MainActivity.collectionMusicPosition = rnd.nextInt(MainActivity.colletctionCount - 1);
                    break;
                case Constants.PLAY_MODEL_SINGLE:
                    break;
            }
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

