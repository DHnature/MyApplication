package com.example.administrator.rainmusic.httpservice;


import android.content.Context;
import android.os.Message;
import android.util.Log;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.interfaces.LyricHttpCallBackListener;
import com.example.administrator.rainmusic.ui.fragment.LyricFragment;
import com.example.administrator.rainmusic.ui.fragment.MusicPlayFragment;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtil {
	
//model为请求消息的模式
//searchMusic方法返回关于歌曲的详细信息，包括歌手名，专辑名，歌曲图片等
	public static void SearchMusic(final Context context, final String s, final int limit, final int type,
                                   final int offset, final int model, final LyricHttpCallBackListener listener){
//开启线程		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = UrlConstants.CLOUD_MUSIC_API_SEARCH +
			"type="+type+"&s='" + s + "'&limit="+limit+"&offset="+offset;
				RequestQueue requestQueue = InternetUtil.getmRequestqueue(context);
				StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
					@Override
					public void onResponse(String s){
						try {
							JSONObject json = new JSONObject(s);
							Log.d("onResponseA1: ",json.toString());
							Message message=new Message();
							message.obj=json.toString();
						switch(model){
		/*搜索歌词*/		case Constants.SEARCH_MUSIC_ID:
							message.what=Constants.SEARCH_MUSIC_ID;
							LyricFragment.handler.sendMessage(message);
							break;
		/*搜索歌曲图片*/	case Constants.SEARCH_PICURL:
							 message.what=Constants.SEARCH_PICURL;
							 MusicPlayFragment.handler.sendMessage(message);
							 break;
						}
						if(listener!=null)
							listener.onFinish(s.toString());
						} catch(JSONException e) {
							e.printStackTrace();
						}
					}
				},new Response.ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError volleyError){
					}
				});
				requestQueue.add(straingRequest);
				
			}
		}).start();
		
	}
	
	
//用于返回网易端的歌词，需要进一步处理
	public static void Cloud_Muisc_getLrcAPI(final Context context, final String os, final String id)
	{
		new Thread(new Runnable() {
		@Override
		public void run() {
			String url = UrlConstants.CLOUD_MUSIC_API_MUSICLRC + "os="+os+"&id="+id+"&lv=-1&kv=-1&tv=-1";
			RequestQueue requestQueue = InternetUtil.getmRequestqueue(context);
			StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
				@Override
				public void onResponse(String s){
					try {
						JSONObject json = new JSONObject(s);
						Log.d("onResponseC1: ",json.toString());   //test
						Message message=new Message();
						message.what=Constants.SEARCH_LYRIC;
						message.obj=json.toString();
						LyricFragment.handler.sendMessage(message);
				 } catch(JSONException e) {
						e.printStackTrace();
					}
				}
			} ,new Response.ErrorListener(){
				@Override
				public void onErrorResponse(VolleyError volleyError){
					Log.d("onResponseC2: ",volleyError.toString());
				
				}
			});
			requestQueue.add(straingRequest);
			
			
		}
	}).start();
		
		
	}
	
	//搜索歌曲API，不知道有什么用
		public static void Cloud_Music_MusicInfoAPI(Context context, String id, String ids)
		{
			String url = UrlConstants.CLOUD_MUSIC_API_MUSICINGO + "id="+id+"&ids=%5B"+ids+"%5D";
			
			RequestQueue requestQueue = InternetUtil.getmRequestqueue(context);
			StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
				@Override
				public void onResponse(String s){
					try {
						JSONObject json = new JSONObject(s);
						Log.d("onResponseB1: ",json.toString());
					} catch(JSONException e) {
						e.printStackTrace();
					}
				}
			},new Response.ErrorListener(){
				@Override
				public void onErrorResponse(VolleyError volleyError){
					Log.i("onResponseB2: ",volleyError.toString());
				}
			});
			requestQueue.add(straingRequest);
		}


	//暂未使用过

	public static JSONObject json = null;
	
	public static JSONObject getInfoFromUrl_Volley(String url, Context context)
	{
		json = null;
		RequestQueue requestQueue = InternetUtil.getmRequestqueue(context);
		StringRequest straingRequest = new StringRequest(url,new Response.Listener<String>(){
			@Override
			public void onResponse(String s){
				try {
					json = new JSONObject(s);
					
                 } catch(JSONException e) {
					e.printStackTrace();
				}
			}
		},new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError volleyError){
				
			}
		});
		requestQueue.add(straingRequest);
		return json;
	}

	
	public class UrlConstants {

		public static final String CLOUD_MUSIC_API_SEARCH = "http://s.music.163.com/search/get/?";

		public static final String CLOUD_MUSIC_API_MUSICINGO = "http://music.163.com/api/song/detail/?";

		public static final String CLOUD_MUSIC_API_MUSICLRC = "http://music.163.com/api/song/lyric?";
	}
}
