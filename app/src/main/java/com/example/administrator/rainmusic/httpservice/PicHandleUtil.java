package com.example.administrator.rainmusic.httpservice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.interfaces.PicHttpCallBackListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class PicHandleUtil {
    private  String root = Environment.getExternalStorageDirectory().toString();
    private  boolean cache = false;


    //从网络中获得图片
    public static void loadImageFromNetwork(final String imageUrl, final PicHttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Drawable drawable ;
                try {
                    drawable = Drawable.createFromStream(
                            new URL(imageUrl).openStream(), "image.jpg");
                    if (listener != null)
                        listener.onFinish(drawable);
                } catch (IOException e) {
                    if (listener != null)
                        listener.onError(e);

                }
            }
        }).start();
        }

    //处理图片信息，返回图片的URL
    public static  ArrayList<String> getPicUrl(String s) {
        ArrayList<String> picUrl=new ArrayList<>();
        String ss[] = s.split("\"picUrl\"" + ":");
        for (int j = 0; j < ss.length - 1; j++) {
          String temp = (String) ss[j].subSequence(Constants.defaultbeign, Constants.defaultend);
            if (temp.equals("null") == false) {
             int   endindex = ss[j].indexOf("\"", 1);
                 picUrl.add((ss[j].substring(Constants.defaultbeign + 1, endindex)).replace("\\", ""));
            }
        }
        return picUrl;
    }


    public void savePicFile(String musicName, String artist, Drawable drawable) {
            //检查文件夹是否存在，若无，则新建文件夹
            File playerCache = new File(root + "/PlayerCache/");
            File pictureFile = new File(root + "/PlayerCache/" + "/picture/");
            if (!playerCache.exists())
                playerCache.mkdir();
            if (!pictureFile.exists())
                pictureFile.mkdir();
            Bitmap bitmap=drawableToBitmap(drawable);
            String fileName= musicName + "-" + artist + ".jpg";
            File picCacheFile=new File(pictureFile, fileName);
        try {
            FileOutputStream fos=new FileOutputStream(picCacheFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //获取图片
    public Drawable getFicFile(String musicName, String artist) {
        Drawable drawable;
        String path = root + "/PlayerCache/" + "/picture/" + musicName + "-" + artist + ".jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        drawable =new BitmapDrawable(bitmap);
        return drawable;
    }

    //检查图片是否缓存
    public boolean picCache(String musicName, String artist) {
        String path = root + "/PlayerCache/" + "/picture/" + musicName + "-" + artist + ".jpg";
        //检查是否有SD卡
        //如果有SD卡，则搜索文件夹中是否有歌词文件
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File f = new File(path);
            if (f.exists())
                cache = true;
            else
                cache = false;
        } else {
            //如果没有SD卡，逻辑待完善。。。。
        }
        return cache;
    }

    //将drawable转化为bitmap
   private Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }



}


	
	

	
	
	
	
	
	

