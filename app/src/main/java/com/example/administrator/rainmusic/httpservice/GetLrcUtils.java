package com.example.administrator.rainmusic.httpservice;

/**
 * Created by Administrator on 2017/7/28.
 */

import com.example.administrator.rainmusic.httpservice.LrcGetUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class GetLrcUtils {
    private static List<String> sentList = null;

    // 获得歌词及歌词时间的map集合
    public static List<String> getLrc(String title, String artist){

        sentList = new ArrayList<String>();

        String string = LrcGetUtils.query(title, artist);

        if (null == string) {

            return null;

        }

        ByteArrayInputStream input = new ByteArrayInputStream(string.getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        try {

            String str = reader.readLine();

            int i = 0;

            while (str != null) {

                i++;

                if (i > 3) {
                    addToSentList(str);
                }

                str = reader.readLine();
            }

            input.close();

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentList;

    }
    public static void addToSentList(String readLine) {

        readLine = readLine.trim();

        if (readLine.equals("")) {

            return;

        }

        String[] pite = readLine.split("]");

        int m = pite.length;

        String lrcContent = "";

        if (readLine.lastIndexOf("]") != readLine.length() - 1) {

            lrcContent = pite[pite.length - 1];

            m = pite.length - 1;
        }

        for (int i = 0; i < m; i++) {

            if(lrcContent.length()!=0)
                sentList.add(pite[i]+"]"+lrcContent);
        }
    }
}


