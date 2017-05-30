package com.amaker.wlo;

import android.app.Activity;
import android.content.Intent;
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
	private EditText dateTime;
	private EditText tel;
	private EditText name;
	private Button reserveButton;
	private String initStartDateTime = ""; // 初始化开始时间
	private String initEndDateTime = "2017年5月23日 17:44"; // 初始化结束时间
	private Spinner spinner;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(msg.what==1)
			{
				Toast.makeText(getApplicationContext(), "预约成功", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getApplicationContext(), "预约失败", Toast.LENGTH_SHORT).show();
			}

		}

	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		reserveButton=(Button)findViewById(R.id.button);
		// 两个输入框
		dateTime = (EditText) findViewById(R.id.inputDate);
		tel=(EditText) findViewById(R.id.tel);
		name=(EditText) findViewById(R.id.name);
		Log.d("TestActivity", "liolioliostartDateTime is:" + dateTime);
		dateTime.setText(initStartDateTime);
		spinner = (Spinner)findViewById(R.id.personNoSpinner);
		dateTime.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						TestActivity.this, initEndDateTime);
				dateTimePicKDialog.dateTimePicKDialog(dateTime);

			}
		});
		reserveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				sendResquest();
			}
		});


		ArrayList<String> list = new ArrayList<String>();
		for(int i=1;i<13;i++){
			list.add(i+"");
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, R.id.text,list);
		spinner.setAdapter(adapter);
		spinner.setPrompt("就餐人数");

	}
	private void sendResquest(){
		new Thread(new Runnable() {
			Message msg = handler.obtainMessage();
			@Override
			public void run() {
				if(reserve()){
					msg.what=1;
				}else{
					msg.what=0;
				}
			handler.sendMessage(msg);
			}
		}).start();
	}
	private boolean reserve(){
		String personNo =(String) spinner.getSelectedItem();// //获得桌号
		String telString=tel.getText().toString();
		String reserveDate=dateTime.getText().toString();
		String reservename =name.getText().toString();
		String result=query(personNo, telString, reserveDate,reservename);
		Log.d("TestActivity", result);
		if(personNo==null){
			Toast.makeText(getApplicationContext(), "请选择人数", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(telString.equals("")){
			Toast.makeText(getApplicationContext(), "请输入预留手机号", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(result==null){
			return false;
		}else{
			return true;
		}
	}
	private String query(String personNo,String telString,String reserveDate,String name){
		// 查询参数
		String queryString = "personNo="+personNo+"&telString="+telString+"&reserveDate="+reserveDate+"&name="+name;
		// url
		String url = HttpUtil.BASE_URL+"servlet/ReserveServlet?"+queryString;
		Log.d("TestActivity", url);
		// 查询返回结果
		return HttpUtil.queryStringForPost(url);
	}
}
