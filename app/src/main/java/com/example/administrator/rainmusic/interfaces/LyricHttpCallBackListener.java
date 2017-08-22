package com.example.administrator.rainmusic.interfaces;

public interface LyricHttpCallBackListener {
	void onFinish(String response);
	void onError(Exception e);
}
