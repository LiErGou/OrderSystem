package com.amaker.wlo;

import com.amaker.util.HttpUtil;
import com.gc.materialdesign.views.Button;

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

public class UserRegisterActivity extends Activity  {
	
	private EditText userEditText,pwdEditText;
	private CheckBox manCheckBox,womanCheckBox;
	private CheckBox likeHotCheckBox,likeSourCheckBox,likeSaltCheckBox,likeSweetCheckBox,dislikeSweetCheckBox,dislikeHotCheckBox,dislikeSourCheckBox,dislikeSaltCheckBox;
	private CheckBox likeVegCheckBox,likePorkCheckBox,likeChickenDucksChickBox,likeBeefMuttonCheckBox,likeFishCheckBox;
	private CheckBox dislikeVegCheckBox,dislikePorkCheckBox,dislikeCheckenDucksChickBox,dislikeBeefMuttonCheckBox,dislikeFishCheckBox,disXiangcaiCheckBox;
	private Button registerBut,backLogin,testBut;
//	private int lF=0,dF=0,lM=0,dM=0;
	int lf[]={0,0,0,0};
	int df[]={0,0,0,0};
	int lm[]={0,0,0,0,0};
	int dm[]={0,0,0,0,0,0};
	int gender=0;//�Ա�
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���ñ���
		setTitle("��׿���ע�����");
		setContentView(R.layout.user_register);
		userEditText = (EditText)findViewById(R.id.userEditText2);
		// ͨ��findViewById����ʵ�������
		manCheckBox=(CheckBox)findViewById(R.id.manCheck);
		womanCheckBox=(CheckBox)findViewById(R.id.womanCheck);
		pwdEditText = (EditText)findViewById(R.id.pwdEditText2);
		likeHotCheckBox=(CheckBox)findViewById(R.id.likeHotCheck);
		likeSourCheckBox=(CheckBox)findViewById(R.id.likeSourCheck);
		likeSaltCheckBox=(CheckBox)findViewById(R.id.likeSaltCheck);
		likeSweetCheckBox=(CheckBox)findViewById(R.id.likeSweetCheck);
		dislikeSweetCheckBox=(CheckBox)findViewById(R.id.dislikeSweetCheck);
		dislikeHotCheckBox=(CheckBox)findViewById(R.id.dislikeHotCheck);
		dislikeSourCheckBox=(CheckBox)findViewById(R.id.dislikeSourCheck);
		dislikeSaltCheckBox=(CheckBox)findViewById(R.id.dislikeSaltCheck);
		likeVegCheckBox=(CheckBox)findViewById(R.id.likeVegCheck);
		likePorkCheckBox=(CheckBox)findViewById(R.id.likePorkCheck);
		likeChickenDucksChickBox=(CheckBox)findViewById(R.id.likeChickenDucksChick);
		likeBeefMuttonCheckBox=(CheckBox)findViewById(R.id.likeBeefMuttonCheck);
		likeFishCheckBox=(CheckBox)findViewById(R.id.likeFishCheck);
		dislikeVegCheckBox=(CheckBox)findViewById(R.id.dislikeVegCheck);
		dislikePorkCheckBox=(CheckBox)findViewById(R.id.dislikePorkCheck);
		dislikeCheckenDucksChickBox=(CheckBox)findViewById(R.id.dislikeChickenDucksCheck);
		dislikeBeefMuttonCheckBox=(CheckBox)findViewById(R.id.dislikeBeefMuttonCheck);
		dislikeFishCheckBox=(CheckBox)findViewById(R.id.dislikeFishCheck);
		disXiangcaiCheckBox=(CheckBox)findViewById(R.id.disXiangcaiCheck);
		registerBut=(Button)findViewById(R.id.registerButton);
		backLogin=(Button)findViewById(R.id.backLoginButton);
		testBut=(Button)findViewById(R.id.testButton);
		final CheckBox []likeFlaver={likeSweetCheckBox,likeHotCheckBox,likeSourCheckBox,likeSaltCheckBox};
		final CheckBox []dislikeFlaver={dislikeSweetCheckBox,dislikeHotCheckBox,dislikeSourCheckBox,dislikeSaltCheckBox};
		final CheckBox []likeMaterial={likeVegCheckBox,likePorkCheckBox,likeChickenDucksChickBox,likeBeefMuttonCheckBox,likeFishCheckBox};
		final CheckBox []dislikeMaterial={dislikeVegCheckBox,dislikePorkCheckBox,dislikeCheckenDucksChickBox,dislikeBeefMuttonCheckBox,dislikeFishCheckBox,disXiangcaiCheckBox};
		manCheckBox.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				manCheckBox.setChecked(true);
				womanCheckBox.setChecked(false);
			}
		});
		womanCheckBox.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				womanCheckBox.setChecked(true);
				manCheckBox.setChecked(false);
			}
		});
		backLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
				
			}
			
		});
		testBut.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserRegisterActivity.this,TestActivity.class);
				startActivity(intent);
				
			}
			
		});
		registerBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for(int i=0;i<likeFlaver.length;i++){
//					Log.w("UserRegisterActivity",i+ "cook");
					if(likeFlaver[i].isChecked()){
						lf[i]=1;
//						lF=i+1;
						Log.w("UserRegisterActivity",i+ "cook");
					}else lf[i]=0;
				}
				for(int i=0;i<dislikeFlaver.length;i++){
					if(dislikeFlaver[i].isChecked()){
						df[i]=1;
//						dF=i+1;
					}else df[i]=0;
				}
				for(int i=0;i<likeMaterial.length;i++){
					if(likeMaterial[i].isChecked()){
						lm[i]=1;
//						lM=i+1;
					}else lm[i]=0;
				}
				for(int i=0;i<dislikeMaterial.length;i++){
					if(dislikeMaterial[i].isChecked()){
						dm[i]=1;
//						dM=i+1;
					}else dm[i]=0;
				}
				if(manCheckBox.isChecked()==true) {
					gender=1;
				}else gender=0;
				if(isSameChoose()){
					if(validate()){
						Toast toast=Toast.makeText(getApplicationContext(), "ע��ɹ�", Toast.LENGTH_SHORT); 
						Toast toast2=Toast.makeText(getApplicationContext(), "�û����ظ�������ԭ��ע��ʧ��", Toast.LENGTH_SHORT);
						if(register()!=false){
							toast.show(); 
						}else{
							toast2.show(); 
						}
					}
				}
			}
		});
		
	}
	//�ж��Ƿ���δѡ
	private boolean isSameChoose(){
		
		for(int i=0;i<lf.length;i++){
			if(lf[i]==1&&df[i]==1){
				Toast toast3=Toast.makeText(getApplicationContext(), "ƫ�ÿ�ζ��ɿڿ�ζ���ͻ��������ѡ��", Toast.LENGTH_SHORT);
				toast3.show();
				return false;
				
			}
		}
		for(int i=0;i<lm.length;i++){
			if(lm[i]==1&&dm[i]==1){
				Toast toast3=Toast.makeText(getApplicationContext(), "ƫ��ʳ����ɿ�ʳ�����ͻ��������ѡ��", Toast.LENGTH_SHORT);
				toast3.show();
				return false;
			}
		}
		if(manCheckBox.isChecked()==false&&womanCheckBox.isChecked()==false) {
			Toast toast3=Toast.makeText(getApplicationContext(), "�빴ѡ�Ա�", Toast.LENGTH_SHORT);
			toast3.show();
			return false;
		}
		return true;

	}
	//ע�ắ��
	private boolean register(){
		// ����û�����
		String username = userEditText.getText().toString();
		Log.d("LoginActivity", username);
		// �������
		String pwd = pwdEditText.getText().toString();
		// ��õ�¼���
		String result=query2(username,pwd);
		Log.d("LoginActivity", result+"cook");
		if(result!=null&&result.equals("0")){
			return false;
		}else{
			saveUserMsg(result);
			return true;
		}
	}
	private boolean validate(){
		String username = userEditText.getText().toString();
		if(username.equals("")){
			showDialog("�û������Ǳ����");
			return false;
		}
		String pwd = pwdEditText.getText().toString();
		if(pwd.equals("")){
			showDialog("�û������Ǳ����");
			return false;
		}
		return true;
	}
	private void showDialog(String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg)
		       .setCancelable(false)
		       .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	private void saveUserMsg(String msg){
		// �û����
		String id = "";
		// �û�����
		String name = "";
		// �����Ϣ����
		String[] msgs = msg.split(";");
		int idx = msgs[0].indexOf("=");
		id = msgs[0].substring(idx+1);
		idx = msgs[1].indexOf("=");
		name = msgs[1].substring(idx+1);
		// ������Ϣ
		SharedPreferences pre = getSharedPreferences("user_msg", MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = pre.edit();
		editor.putString("id", id);
		editor.putString("name", name);
		editor.commit();
	}
	private String query2(String account,String password){
		// ��ѯ����
		String lfStr="";
		for(int i=0;i<lf.length;i++){
			lfStr=lfStr+"&lf["+i+"]="+lf[i];
		}
		String dfStr="";
		for(int i=0;i<df.length;i++){
			dfStr=dfStr+"&df["+i+"]="+df[i];
		}
		String lmStr="";
		for(int i=0;i<lm.length;i++){
			lmStr=lmStr+"&lm["+i+"]="+lm[i];
		}
		String dmStr="";
		for(int i=0;i<dm.length;i++){
			dmStr=dmStr+"&dm["+i+"]="+dm[i];
		}
		//String queryString = "account="+account+"&password="+password+"&likeFlavor="+lF+"&dislikeFlavor="+dF+"&likeMaterial="+lM+"&dislikeMaterial="+dM+"&a[0]=10&a[1]=11&a[2]=12";
		// url
		String queryString = "account="+account+"&password="+password+"&gender="+gender+lfStr+dfStr+lmStr+dmStr;
		
		String url = HttpUtil.BASE_URL+"servlet/UserRegisterServlet?"+queryString;
		Log.d("LoginActivity", "url="+url+"cook");
		// ��ѯ���ؽ��
		return HttpUtil.queryStringForPost(url);
    }
	
}
