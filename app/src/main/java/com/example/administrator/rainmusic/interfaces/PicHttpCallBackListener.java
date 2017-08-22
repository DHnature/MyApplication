package com.example.administrator.rainmusic.interfaces;

import android.graphics.drawable.Drawable;

public interface PicHttpCallBackListener {
	void onFinish(Drawable drawable);
	void onError(Exception e);
}
