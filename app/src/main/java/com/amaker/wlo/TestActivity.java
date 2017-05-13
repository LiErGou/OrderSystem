package com.amaker.wlo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * 时间拾取器界面
 *
 * @author wwj_748
 *
 */
public class TestActivity extends Activity {
	/** Called when the activity is first created. */
	private EditText startDateTime;
	private EditText endDateTime;

	private String initStartDateTime = "2013年9月3日 14:44"; // 初始化开始时间
	private String initEndDateTime = "2014年8月23日 17:44"; // 初始化结束时间

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);

		// 两个输入框
		startDateTime = (EditText) findViewById(R.id.inputDate);
		endDateTime = (EditText) findViewById(R.id.inputDate2);

		Log.d("TestActivity","liolioliostartDateTime is:"+startDateTime);
		startDateTime.setText(initStartDateTime);
		endDateTime.setText(initEndDateTime);

		startDateTime.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						TestActivity.this, initEndDateTime);
				dateTimePicKDialog.dateTimePicKDialog(startDateTime);

			}
		});

		endDateTime.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						TestActivity.this, initEndDateTime);
				dateTimePicKDialog.dateTimePicKDialog(endDateTime);
			}
		});
	}
}
