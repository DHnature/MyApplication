package com.example.administrator.rainmusic.ui.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.rainmusic.R;
import com.example.administrator.rainmusic.constant.Constants;
import com.example.administrator.rainmusic.httpservice.LoginServiceUtils;
import com.example.administrator.rainmusic.httpservice.RegisterServiceUtils;


public class LoginActivity extends Activity {
	private Button login;
	private Button register;
	private EditText username;
	private EditText password;
	private ProgressDialog dialog;
	private ProgressDialog dialog2;
	private String info="";
	private String info2="";
	private static Handler handler=new Handler();
	private static Handler handler2=new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		login=(Button) findViewById(R.id.login_activity);
		register=(Button) findViewById(R.id.register);
		username=(EditText) findViewById(R.id.username);
		password=(EditText) findViewById(R.id.password);



//注册事件
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new ProgressDialog(LoginActivity.this);
				dialog.setTitle("提示");
				dialog.setMessage("正在登陆，请稍后...");
				dialog.setCancelable(true);
				dialog.show();
				new Thread(new myThread()).start();

			}
		});




		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog2 = new ProgressDialog(LoginActivity.this);
				dialog2.setTitle("提示");
				dialog2.setMessage("正在注册，请稍后...");
				dialog2.setCancelable(true);
				dialog2.show();
				new Thread(new myThread2()).start();

			}
		});

	}

	// 子线程接收数据，主线程修改数据
	public class myThread implements Runnable {
		@Override
		public void run() {

			info= LoginServiceUtils.executeHttpGet(username.getText().toString(),
                    password.getText().toString(),Constants.LOGIN);

			handler.post(new Runnable() {
				@Override
				public void run() {
					// 最好返回一个固定键值，根据键值判断是否登陆成功，有键值就保存该info跳转，没键值就是错误信息直接toast
//					infotv.setText(info);
					dialog.dismiss();
					Toast toast = Toast.makeText(LoginActivity.this, info, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();
				}
			});

		}

	}


public class  myThread2 implements Runnable {
@Override
	public void run() {
	info2= RegisterServiceUtils.executeHttpGet(username.getText().toString(),password.getText().toString(), Constants.REGISITER);
	handler2.post(new Runnable() {
		@Override
		public void run() {
			dialog2.dismiss();
			Toast toast = Toast.makeText(LoginActivity.this, info2, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
}
	});

}
}

	// 检测网络
	private boolean checkNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}


}
