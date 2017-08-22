package com.example.administrator.rainmusic.model;

/**
 * Created by Administrator on 2017/7/30.
 */

public class LrcRow {

  //strTime表示该行歌词要开始播放的时间，
    public String strTime;
    //time表示将strTime转换为long型之后的数值
    public long time;
    //content表示该行歌词的内容
    public String content;


    public LrcRow() {
    }

    public LrcRow(String strTime, long time, String content) {
        this.strTime = strTime;
        this.time = time;
        this.content = content;
    }



    @Override
    public String toString() {
        return "[" + strTime + " ]" + content;
    }

}

