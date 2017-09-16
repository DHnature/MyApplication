package com.example.administrator.rainmusic.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.rainmusic.MainActivity;
import com.example.administrator.rainmusic.R;

import java.io.File;

/**
 * Created by Administrator on 2017/9/8.
 */

public class SettingActivity extends Activity implements View.OnClickListener {
    private TextView cacheSize;
    private String root = Environment.getExternalStorageDirectory().toString();
    private RelativeLayout shakeChangeMusic;
    private RelativeLayout setting_clean_cache_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        cacheSize = (TextView) findViewById(R.id.cache_size);
        setting_clean_cache_layout = (RelativeLayout) findViewById(R.id.setting_clean_cache_layout);
        shakeChangeMusic=(RelativeLayout) findViewById(R.id.setting_shake_to_change_music_layout);

        cacheSize.setText(getFolderSize() + "KB");

        shakeChangeMusic.setOnClickListener(this);
        setting_clean_cache_layout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_clean_cache_layout:
                AlertDialog.Builder dialog=new AlertDialog.Builder(SettingActivity.this);
                dialog.setTitle("清除缓存");
                dialog.setMessage("确定清除缓存吗");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delCache();
                        cacheSize.setText(getFolderSize() + "KB");
                    }
                });
              dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                  }
              });
                dialog.show();
                break;
            case R.id.setting_shake_to_change_music_layout:
                AlertDialog.Builder dialog2=new AlertDialog.Builder(SettingActivity.this);
                dialog2.setMessage("需要设置摇一摇切换歌曲吗？");
                dialog2.setCancelable(false);
                dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.sensorSetting=true;
                        dialog.dismiss();
                    }
                });
                dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.sensorSetting=false;
                        dialog.dismiss();
                    }
                });
                dialog2.show();
                break;




        }

    }

    private int getFolderSize() {

        //size = size + getFolderSize(fileList[i]);

        long size = 0;
        File file1 = new File(root + "/PlayerCache/" + "/picture/");
        File[] fs1 = file1.listFiles();
        for (int i = 0; i < fs1.length ; i++)
            size += fs1[i].length();

        File file2 = new File(root + "/PlayerCache/" + "/lyric/");
        File[] fs2 = file2.listFiles();
        for (int i = 0; i < fs2.length; i++)
            size += fs2[i].length();

        return (int) size / 1024 + 1;
    }

    private void delCache() {
        File file1 = new File(root + "/PlayerCache/" + "/picture/");
        File[] fs1 = file1.listFiles();
        for (int i = 0; i < fs1.length ; i++)
            fs1[i].delete();
        File file2 = new File(root + "/PlayerCache/" + "/lyric/");
        File[] fs2 = file2.listFiles();
        for (int i = 0; i < fs2.length ; i++)
            fs2[i].delete();
    }

}
