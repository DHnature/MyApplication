package com.example.administrator.rainmusic.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Window;


import com.example.administrator.rainmusic.MainActivity;
import com.example.administrator.rainmusic.R;
import com.example.administrator.rainmusic.adapter.MyPagerAdapter;
import com.example.administrator.rainmusic.config.MyApplication;
import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.httpservice.HttpUtil;
import com.example.administrator.rainmusic.interfaces.LyricHttpCallBackListener;

import java.net.URLEncoder;
import java.util.ArrayList;


public class MusicSurfaceFragment extends FragmentActivity {


    private ViewPager mViewPager;
    private ArrayList<Fragment> fragmentList;
    private LyricFragment lyricFragment;
    private MusicPlayFragment musicPlayFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_music_main_surface);

        musicPlayFragment = new MusicPlayFragment();
        lyricFragment = new LyricFragment();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(musicPlayFragment);
        fragmentList.add(lyricFragment);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragmentList));
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mViewPager.setCurrentItem(0);

    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            //切换第一页面时自动刷新歌手专辑图片以及歌曲名
            if (arg0 == 0) {
                Intent intent = new Intent("com.example.mediaplayer.changeNameOfMusicInCircle");
                sendBroadcast(intent);

                HttpUtil.SearchMusic(MyApplication.getContext(), URLEncoder.encode(MainActivity.currentMusic.getTitle()),
                        10, 1, 0, Constants.SEARCH_PICURL, new LyricHttpCallBackListener() {
                            @Override
                            public void onFinish(String response) {
                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        });

            }
            //切换第二页面时自动刷新歌词
            if (arg0 == 1) {

                new Thread(new LyricFragment.lyricThread()).start();
            }


        }


    }

}

		
		
      
		
		
		
		
		