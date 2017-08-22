package com.example.administrator.rainmusic.interfaces;


import com.example.administrator.rainmusic.model.LrcRow;

/**
 * Created by Administrator on 2017/7/30.
 */

public interface ILrcViewListener {
    void onLrcSeeked(int newPosition, LrcRow row);
}
