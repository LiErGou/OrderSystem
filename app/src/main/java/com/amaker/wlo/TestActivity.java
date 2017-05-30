package com.amaker.wlo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amaker.util.HttpUtil;
import com.gc.materialdesign.views.Button;

import java.util.ArrayList;

public class TestActivity extends Activity {
	/** Called when the activity is first created. */

	private EditText ip;
	private Button settingButton;
	private String ipString;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		settingButton=(Button)findViewById(R.id.setButton);
		// 两个输入框
		ip=(EditText)findViewById(R.id.setting);
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ipString=ip.getText().toString().trim();
				SharedPreferences.Editor editor =getSharedPreferences("data",MODE_PRIVATE).edit();
				editor.putString("url", "http://"+ipString+":8080/WirelessOrder_Server/");
				editor.apply();
				Toast.makeText(getApplicationContext(), "ip地址设置成功！",
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
