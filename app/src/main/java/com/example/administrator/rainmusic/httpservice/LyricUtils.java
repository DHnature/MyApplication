package com.example.administrator.rainmusic.httpservice;

import android.os.Environment;


import com.example.administrator.rainmusic.model.LrcRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/7/29.
 */

public class LyricUtils {
    private static List<String> lrcList = new ArrayList<String>();
    private static boolean cache = false;
    public static String root = Environment.getExternalStorageDirectory().toString(); //获取存储卡目录

    //缓存歌词
    public void saveLrcFile(String musicName, String artist, List<String> lrcList) {  // 参数名是歌曲名称，如 ：幻听.mp3
        try {
            //检查文件夹是否存在，若无，则新建文件夹
            File playerCache = new File(root + "/PlayerCache/");
            File lyricFile = new File(root + "/PlayerCache/" + "/lyric/");
            if (!playerCache.exists())
                playerCache.mkdir();
            if (!lyricFile.exists())
                lyricFile.mkdir();
            FileWriter fw = new FileWriter(root + "/PlayerCache/" + "/lyric/" + musicName + "-" + artist + ".lrc");
            for (int i = 0; i < lrcList.size() && lrcList.size() != 0; i++) {
                fw.write(lrcList.get(i));
                fw.write("\n");
            }

            fw.close();
        } catch (IOException e1) {

        }
    }


    //获取歌词
    public List<String> getLrcFile(String musicName, String artist) {
        lrcList.clear();
        String path = root + "/PlayerCache/" + "/lyric/" + musicName + "-" + artist + ".lrc";
        File file = new File(path);
        if (file.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    lrcList.add(temp);
                }
            } catch (Exception e) {

            }

        }
        return lrcList;
    }

    //检查歌词是否缓存，若果
    public boolean LyricCache(String musicName, String artist) {
        String path = root + "/PlayerCache/" + "/lyric/" + musicName + "-" + artist + ".lrc";
        //检查是否有SD卡
        //如果有SD卡，则搜索文件夹中是否有歌词文件
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File f = new File(path);
            if (f.exists())
                cache = true;
            else
                cache = false;
        } else {
            //如果没有SD卡，逻辑等下写。。。。
        }
        return cache;
    }

    //拆分每行的歌词的内容和时间，返回一个LrcRow对象而不是一个队列
    public LrcRow convertToRows(String standardLrcLine) {
        if (!standardLrcLine.equals("当前无歌词")) {
            String tempContent = standardLrcLine.substring(standardLrcLine.indexOf("]") + 1, standardLrcLine.length());
            String times = standardLrcLine.substring(standardLrcLine.indexOf("[") + 1, standardLrcLine.indexOf("]") - 1);
            LrcRow lrcRow = new LrcRow(times, timeConvert(times), tempContent);
            return lrcRow;
        }
        return null;
    }


    private long timeConvert(String timeString) {
        //因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单位
        //将字符串 XX:XX.XX 转换为 XX:XX:XX
        timeString = timeString.replace('.', ':');
        //将字符串 XX:XX:XX 拆分
        String[] times = timeString.split(":");
        // mm:ss:SS
        if (times.length == 3&&isNumeric(times[0])&&isNumeric(times[1])&&isNumeric(times[2])) {
            return Integer.valueOf(times[0]) * 60 * 1000 +//分
                    Integer.valueOf(times[1]) * 1000 +//秒
                    Integer.valueOf(times[2]);//毫秒
        } else if (times.length == 2&&isNumeric(times[0])&&isNumeric(times[1]))
            return Integer.valueOf(times[0]) * 60 * 1000 +//分
                    Integer.valueOf(times[1]) * 1000 ;//秒
        else
            return 0;
    }
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
