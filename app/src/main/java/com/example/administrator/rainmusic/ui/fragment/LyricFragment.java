package com.example.administrator.rainmusic.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.rainmusic.MainActivity;
import com.example.administrator.rainmusic.R;
import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.httpservice.GetLrcUtils;
import com.example.administrator.rainmusic.httpservice.LyricHandleUtils;
import com.example.administrator.rainmusic.interfaces.ILrcView;
import com.example.administrator.rainmusic.interfaces.ILrcViewListener;
import com.example.administrator.rainmusic.model.LrcRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LyricFragment extends Fragment {
    //自定义LrcView，用来展示歌词
    public static ILrcView mLrcView;
    //更新歌词的频率，每秒更新一次
    private int mPalyTimerDuration = 1000;
    //更新歌词的定时器
    private Timer mTimer;
    //更新歌词的定时任务
    private TimerTask mTask;
    private lyricUpdate lyricUpdate;
    private static List<LrcRow> rows =new ArrayList<LrcRow>();
    public static String lyricId;
    public static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //控制歌词滚动
                case Constants.LYRIC_SCROLL:
                    rows.clear();
                    rows = (List<LrcRow>) msg.obj;
                    mLrcView.setLrc(rows);
                    break;
                //歌词滑动控制进度
                case Constants.LYRIC_SKIP:
                    long timePassed = Long.valueOf(msg.obj.toString());
                    if(rows!=null&&rows.size()!=0)
                    mLrcView.seekLrcToTime(timePassed);
            }
        }

    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_lyric_surface, container, false);
        //ILrcView为自定义歌曲滑动View
        mLrcView = (ILrcView) rootview.findViewById(R.id.lrcView);
        //设置自定义的LrcView上下拖动歌词时监听
        mLrcView.setListener(new ILrcViewListener() {
            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
            public void onLrcSeeked(int newPosition, LrcRow row) {
                if (MainActivity.mediaplayer != null) {
                    MainActivity.mediaplayer.seekTo((int) row.time);
                }
            }
        });

        //歌词滑动
            if (mTimer == null) {
                mTimer = new Timer();
                mTask = new LrcTask();
                mTimer.scheduleAtFixedRate(mTask, 0, mPalyTimerDuration);
            }
     /*开启线程加载当前歌曲的歌词	*/
        new Thread(new lyricThread()).start();

        lyricUpdate = new lyricUpdate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.lyricUpdate");
        getActivity().registerReceiver(lyricUpdate, intentFilter);

        return rootview;
    }


//搜索歌词并加载
    public static class lyricThread implements Runnable {
        @Override
        public void run() {
            ArrayList<String> currentLyric = new ArrayList<String>();
            ArrayList<LrcRow> finalLyric = new ArrayList<LrcRow>();
            String currentMusicName = MainActivity.currentMusic.getTitle();
            String currentArtist = MainActivity.currentMusic.getArtist();
            LyricHandleUtils tool = new LyricHandleUtils();
            if (tool.LyricCache(currentMusicName, currentArtist)) {
                currentLyric = (ArrayList<String>) tool.getLrcFile(currentMusicName, currentArtist);

            } else {
                //从网络上加载歌词
                currentLyric = (ArrayList<String>) GetLrcUtils.getLrc(currentMusicName, currentArtist);
                if (currentLyric != null)
                    tool.saveLrcFile(currentMusicName, currentArtist, currentLyric);
                else {
                    currentLyric = new ArrayList<>();
                }
            }
           if(currentLyric.size()!=0) {
               finalLyric.add(tool.convertToRows(currentLyric.get(0)));
               for (int i = 0; i < currentLyric.size() - 1; i++) {
                   LrcRow lrcRow1 = tool.convertToRows(currentLyric.get(i));
                   LrcRow lrcRow2 = tool.convertToRows(currentLyric.get(i + 1));
                   if (lrcRow2.time - lrcRow1.time <= 60000) {
                       finalLyric.add(lrcRow2);
                   }
               }
               finalLyric.add(tool.convertToRows(currentLyric.get(currentLyric.size() - 1)));
           }
            Message message = new Message();
            message.what = Constants.LYRIC_SCROLL;
            message.obj = finalLyric;
            handler.sendMessage(message);
        }
 }

    //歌词滑动功能实现
    class LrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            final long timePassed = MainActivity.mediaplayer.getCurrentPosition();
            Message msg = new Message();
            msg.what = Constants.LYRIC_SKIP;
            msg.obj = timePassed;
            handler.sendMessage(msg);
        }
    }
    public class lyricUpdate extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            new Thread(new lyricThread()).start();
        }
    }


}








