package com.example.administrator.rainmusic.httpservice;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class InternetUtil {

	private static RequestQueue mRequestqueue;
	public static RequestQueue getmRequestqueue(Context context)
	{
		if(mRequestqueue == null)
		{
			mRequestqueue = Volley.newRequestQueue(context);
			return mRequestqueue;
		}
		else{
			return mRequestqueue;
		}
	}
	public static <T> void addToRequestQueue(Request<T> request,Context context){
		getmRequestqueue(context).add(request);
	}

	public static void keywordSearch(String s, Response.Listener<JSONObject> listener,
                                     Response.ErrorListener errorListener, Context context) {
		String url = UrlConstants.CLOUD_MUSIC_API_PREFIX + "type=1&s='" + s + "'&limit=10&offset=0";
		request(Request.Method.GET, url, null, listener, errorListener,context);
	}


	public static void request(int method, String url, JSONObject jsonObject, Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener, Context context) {
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonObject, listener, errorListener);
		addToRequestQueue(jsonObjectRequest,context);
	}

	public class UrlConstants {
		public static final String CLOUD_MUSIC_API_PREFIX = "http://s.music.163.com/search/get/?";
	}
}

