package com.amaker.wlo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amaker.util.HttpUtil;

public class PayActivity extends Activity{
	// 显示点餐信息WebView
	private WebView wv;
	// 查询点餐信息按钮和结算按钮
	private Button queryBtn,payBtn;
	// 订单编号
	private EditText orderIdEt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置标题
		setTitle("安卓点餐系统结台界面");
		// 设置当前Activity的界面布局
		setContentView(R.layout.pay);
		// 获得WebView实例
		wv = (WebView)findViewById(R.id.pay_webview);
		// 实例化查询按钮
		queryBtn = (Button)findViewById(R.id.pay_query_Button01);
		// 实例化结算按钮
		payBtn = (Button)findViewById(R.id.pay_Button01);
		// 实例化订单编号编辑框
		orderIdEt = (EditText)findViewById(R.id.pay_order_number_EditText01);

		// 添加查询点餐信息监听器
		queryBtn.setOnClickListener(queryListener);
		// 添加结算息监听器
		payBtn.setOnClickListener(payListener);
		SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
		String  isGuest=pref.getString("isGuest", "");
		if (isGuest.equals("yes")){
			payBtn.setEnabled(false);
		}

	}
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {

			String result=(String)msg.obj;
			Log.d("payActivity",result);
			Toast.makeText(PayActivity.this, result, Toast.LENGTH_LONG).show();
			payBtn.setEnabled(false);
		}
	};
	private void sendMsg(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg=handler.obtainMessage();
				String orderId = orderIdEt.getText().toString();
				// 请求服务器url
				String url = getUrlFromSp()+"servlet/PayMoneyServlet?id="+orderId;
				// 获得查询结果
				String result = HttpUtil.queryStringForPost(url);
				msg.obj=result;
				handler.sendMessage(msg);
			}
		}).start();
	}
	// 查询点餐信息监听器
	OnClickListener queryListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 获得订单编号
			String orderId = orderIdEt.getText().toString();
			// 请求服务器url
			String url = getUrlFromSp()+"servlet/PayServlet?id="+orderId;
			// 将返回信息在WebView中显示
			wv.loadUrl(url);
		}
	};
	public String getUrlFromSp(){
		SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
		String  url=pref.getString("url", "");
		return url;
	}
	// 结算监听器
	OnClickListener payListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			sendMsg();

		}
	};

}
