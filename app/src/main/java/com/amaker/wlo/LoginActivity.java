package com.amaker.wlo;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.amaker.util.HttpUtil;
import com.gc.materialdesign.views.Button;

public class LoginActivity extends Activity {
	// 声明登录、取消按钮
	private Button loginBtn,registerBtn;
	// 声明用户名、密码输入框
	private EditText userEditText,pwdEditText;
	private CheckBox workerCheckBox,guestCheckBox;
	private String userId;
	private boolean flag=false,getResult=false;
	int isGuest=2;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置标题
		setTitle("安卓点餐系统登陆界面");
		// 设置当前Activity界面布局
		setContentView(R.layout.login_system);
		// 通过findViewById方法实例化组件

		// 通过findViewById方法实例化组件
		loginBtn = (Button)findViewById(R.id.loginButton);
		// 通过findViewById方法实例化组件
		registerBtn = (Button)findViewById(R.id.registerButton);
		userEditText = (EditText)findViewById(R.id.userEditText);
		// 通过findViewById方法实例化组件
		pwdEditText = (EditText)findViewById(R.id.pwdEditText);
		workerCheckBox=(CheckBox)findViewById(R.id.workerCheck);
		guestCheckBox=(CheckBox)findViewById(R.id.guestCheck);

		workerCheckBox.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				workerCheckBox.setChecked(true);
				guestCheckBox.setChecked(false);
			}
		});
		guestCheckBox.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				guestCheckBox.setChecked(true);
				workerCheckBox.setChecked(false);
			}
		});


		registerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,UserRegisterActivity.class);
				startActivity(intent);
				
				/*if(validate()){
					Toast toast=Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT); 
					Toast toast2=Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT);
					if(register()!=false){
						toast.show(); 
					}else{
						toast2.show(); 
					}
				}*/
			}
		});

		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(workerCheckBox.isChecked()==true) {
					isGuest=1;  //标记登录身份
				}else isGuest=0;
				if(validate()){
					sendRequest();
					while(true){
						if(getResult==true){
							break;
						}
					}
					if(flag){
						Intent intent = new Intent(LoginActivity.this,MainMenuActivity.class);
						startActivity(intent);
						getResult=false;
					}else{
						showDialog("用户名称或者密码错误，请重新输入！");
					}
				}
			}
		});
	}
	// 登录方法
	@SuppressLint("NewApi")
	private boolean login(){
		// 获得用户名称
		String username = userEditText.getText().toString();
		Log.d("LoginActivity", username);
		// 获得密码
		String pwd = pwdEditText.getText().toString();
		// 获得登录结果
		String result=query(username,pwd);
		String[] sarray = result.split(" ");
		userId=sarray[1];
		SharedPreferences.Editor editor =getSharedPreferences("data",MODE_PRIVATE).edit();
		editor.putString("userId",userId);
		editor.apply();
		Log.d("LoginActivity", result+"cook");
		if(result!=null&&result.equals("0")){
			getResult=true;
			return false;
		}else{
			saveUserMsg(result);
			getResult=true;
			return true;
		}
	}
	private void sendRequest(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					if(login()){
						flag=true;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}

	// 将用户信息保存到配置文件
	private void saveUserMsg(String msg){
		// 用户编号
		String id = "";
		// 用户名称
		String name = "";
		// 获得信息数组
		String[] msgs = msg.split(";");
		int idx = msgs[0].indexOf("=");
		id = msgs[0].substring(idx+1);
		idx = msgs[1].indexOf("=");
		name = msgs[1].substring(idx+1);
		// 共享信息
		SharedPreferences pre = getSharedPreferences("user_msg", 0);
		SharedPreferences.Editor editor = pre.edit();
		editor.putString("id", id);
		editor.putString("name", name);
		if(isGuest==0){
			editor.putString("isGuest", "yes");
		}else editor.putString("isGuest", "no");
		editor.commit();
	}

	// 验证方法
	private boolean validate(){
		String username = userEditText.getText().toString();
		if(username.equals("")){
			showDialog("用户名称是必填项！");
			return false;
		}
		String pwd = pwdEditText.getText().toString();
		if(pwd.equals("")){
			showDialog("用户密码是必填项！");
			return false;
		}
		if(workerCheckBox.isChecked()==false&&guestCheckBox.isChecked()==false) {
			Toast toast3=Toast.makeText(getApplicationContext(), "请勾选登录身份", Toast.LENGTH_SHORT);
			toast3.show();
			return false;
		}
		return true;
	}
	private void showDialog(String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	// 根据用户名称密码查询
	private String query(String account,String password){
		// 查询参数
		String queryString = "account="+account+"&password="+password+"&station="+isGuest;
		// url
		String url = HttpUtil.BASE_URL+"servlet/LoginServlet?"+queryString;
		Log.d("LoginActivity", url);
		// 查询返回结果
		return HttpUtil.queryStringForPost(url);
	}
}