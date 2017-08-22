package com.example.administrator.rainmusic.httpservice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;


import com.example.administrator.rainmusic.interfaces.PicHttpCallBackListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PictureUtil {
	//从网络中获得图片方法1		
			public static Bitmap getBitmap(String path) throws IOException {
				try {
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() == 200) {
						InputStream inputStream = conn.getInputStream();
						Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						return bitmap;
					}
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				return null;
				
			}

	
		    
		  //从网络中获得图片方法2			
		    public static void loadImageFromNetwork(final String imageUrl, final PicHttpCallBackListener listener)  {
		       new Thread(new Runnable() {
		    	Drawable drawable = null;
				@Override
				public void run() {
					  try {  
						  drawable = Drawable.createFromStream(
				       new URL(imageUrl).openStream(), "image.jpg");
				       if(listener!=null)
						listener.onFinish(drawable);
				   } 
					 catch (IOException e) {
						   if(listener!=null)
							  listener.onError(e);
				            
				        }  
					 
				}
			}).start();
		       
}  

	//将drawable转化为bitmap		
		    public static Bitmap drawableToBitmap(Drawable drawable) {
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
	
	
	public class ImgDownload extends AsyncTask<String, Integer, Drawable> {

		@Override
		protected Drawable doInBackground(String... params) {
			 Drawable drawable = null;
		        try {  
		           drawable = Drawable.createFromStream(
		           new URL(params[0]).openStream(), "image.jpg");
		        } catch (IOException e) {
		            
		        }  
		        if (drawable == null) {  
		            Log.d("图片", "null drawable");
		        } else {  
		            Log.d("图片", "not null drawable");
		        }  
		          
		        return drawable ;  
			
		}
		
	}
	
	
	
	
	
	
}
