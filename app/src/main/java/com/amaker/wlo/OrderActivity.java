package com.amaker.wlo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amaker.wlo.shopBean;

import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.SimpleAdapter;
import android.net.Uri;
import android.widget.SimpleCursorAdapter;
import android.database.Cursor;

import java.util.Date;
import java.text.SimpleDateFormat;

import android.content.SharedPreferences;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import com.amaker.util.HttpUtil;
import com.gc.materialdesign.views.Button;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;

public class OrderActivity extends Activity {

	private Button startBtn,hotMenuBtn;
	private String orderId=null;
	private String ShopId;
	private String userId;
	private String isGuest;// 判断身份
	private CheckBox checkBox;
	private ListView listView;
	// 点菜列表中具体的数据项

	private TextView popTotalPrice; // 结算的价格
	private TextView popDelete; // 删除
	private TextView popRecycle; // 收藏
	private TextView popCheckOut; // 结算
	private LinearLayout layout; // 结算布局
	private ShopAdapter adapter; // 自定义适配器
	private ListView main_listView;
	private Spinner tableNoSpinner;
	private EditText personNumEt;
	private List<shopBean> list;
	private List data = new ArrayList();// 购物车数据集合
	private List<AddressBean> addressList;
	private boolean flag = false;
	private SimpleAdapter sa;
	private Map map;
	private List caidan = new ArrayList();
	private Cursor c = null;
	String result;

	// 点菜列表中具体的数据项

	private int[] to = new int[5];
	private String[] from = { "id", "name", "num", "price", "remark" };

	@SuppressLint("UseSparseArrays")
	// 全选或全取消
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// setContentView(R.layout.order);

		// 实例化Spinner
		tableNoSpinner = (Spinner) findViewById(R.id.tableNoSpinner);
		// 为桌号下拉列表Spinner绑定数据
		setTableAdapter();
		hotMenuBtn=(Button) findViewById(R.id.hotmenu);
		
		// 实例化开桌按钮
		startBtn = (Button) findViewById(R.id.startButton02);
		// 为开桌按钮添加监听器
		startBtn.setOnClickListener(startListener);
		// 实例化下单按钮
		// 为下单按钮添加监听器
		hotMenuBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent intent = new Intent(OrderActivity.this,HotMenuActivity.class);
				intent.putExtra("extra_date",orderId);
				startActivityForResult(intent,1);
				
			}
			
		});
		userId=getUserId();
		// 实例化人数编辑框
		personNumEt = (EditText) findViewById(R.id.personNumEditText02);

		// 实例化ListView
		main_listView = (ListView) findViewById(R.id.main_listView);
		// 为点菜列表ListView绑定数据
		// setMenusAdapter();
		initViews();
		init();
	}
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		switch (requestCode){
		case 1:


		}
	
	}
	private void setMenusAdapter() {
		// 显示点菜项的TextView引用

		to[1] = R.id.shop_name;// R.layout.main_item不居中存在
		to[2] = R.id.shop_number;
		to[3] = R.id.shop_price;

		// 实例化点菜列表ListView Adapter
		sa = new SimpleAdapter(this, data, R.layout.main_item, from, to);
		// 为ListView绑定数据
		main_listView.setAdapter(sa);
	}

	// 为桌号下拉列表Spinner绑定数据
	@SuppressLint("NewApi")
	private void setTableAdapter() {
		// 访问本地SQLite数据库中桌号表的Uri
		Uri uri = Uri.parse("content://com.amaker.provider.TABLES/table");
		// 要选择桌号表中的列
		String[] projection = { "_id", "num", "description" };
		// 查询放回游标

		// = managedQuery(uri, projection, null, null, null);
		if (Build.VERSION.SDK_INT < 11) {
			c = managedQuery(uri, projection, null, null, null);
		} else {
			CursorLoader cursorLoader = new CursorLoader(this, uri, null, null,
					null, null);
			c = cursorLoader.loadInBackground();
		}
		// if(c.moveToFirst()){
		// do{
		// String id=c.getString(c.getColumnIndex("_id"));
		// Log.d("OrderActivity",id+"cook");
		// }while(c.moveToNext());
		// }
		// 实例化桌号下拉列表Spinner的Adapter
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, c,
				new String[] { "_id" }, new int[] { android.R.id.text1 });
		// 为桌号Spinner绑定数据
		tableNoSpinner.setAdapter(adapter2);
	}

	// 开桌监听器
	private OnClickListener startListener = new OnClickListener() {

		public void onClick(View v) {

			String personNum = personNumEt.getText().toString();
			if(personNum.equals("")){
				Toast.makeText(getApplicationContext(), "请输入就餐人数", Toast.LENGTH_SHORT).show(); 
				return;
			}
			TextView tv = (TextView) tableNoSpinner.getSelectedView();// 获得spinner数据
			String tableId = tv.getText().toString();// //获得桌号
			String id;
			String seatNum = null;
			if (c != null) {
				// Log.d("OrderActivity","cook1");
				if (c.moveToFirst()) {
					do {
						// Log.d("OrderActivity","cook2");
						id = c.getString(c.getColumnIndex("_id"));
						seatNum = c.getString(c.getColumnIndex("seatNum"));
						// Log.d("OrderActivity","id是"+id+"seatNum是:"+seatNum+"cook");
						// Log.d("OrderActivity","cook3");
						if (tableId.equals(id)) {
							// Log.d("OrderActivity","cook4");
							break;
						}
					} while (c.moveToNext());// 找到对应id的桌子
					seatNum = c.getString(c.getColumnIndex("seatNum"));// 找到对应id的桌子的座位数
					// Log.d("OrderActivity","cook5");
				}
			} else {
				Toast.makeText(OrderActivity.this, "开桌失败", Toast.LENGTH_LONG)
						.show();
				return;
			}
			int intSeatN = Integer.valueOf(seatNum).intValue();
			int intPersonNum = Integer.valueOf(personNum).intValue();
			// 判断座位数小于人数时的情况
			if (intSeatN < intPersonNum && intSeatN == 4) {
				Toast.makeText(OrderActivity.this,
						"座位数" + intSeatN + "远小于人数" + intPersonNum + "请选择别的桌子",
						Toast.LENGTH_LONG).show();
				return;
			} else if (intSeatN < intPersonNum  && intSeatN > 4) {
				Toast.makeText(OrderActivity.this,
						"座位数" + intSeatN + "远小于人数" + intPersonNum + "请选择别的桌子",
						Toast.LENGTH_LONG).show();
				return;
			}
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yy:mm:dd hh:MM");
			// 开桌时间
			String orderTime = sdf.format(date);
			// 开桌用户，从登陆配置文件中获得
			SharedPreferences pres = getSharedPreferences("user_msg", 0);
			userId = pres.getString("id", "");
			isGuest = pres.getString("isGuest", "");
			// 桌号

			Toast.makeText(OrderActivity.this, "桌号：" + tableId,
					Toast.LENGTH_LONG).show();
			// String tableId = "1";
			// 人数

			// 请求参数列表
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// 添加请求参数
			params.add(new BasicNameValuePair("orderTime", orderTime));
			params.add(new BasicNameValuePair("userId", userId));
			params.add(new BasicNameValuePair("tableId", tableId));
			params.add(new BasicNameValuePair("personNum", personNum));
			params.add(new BasicNameValuePair("isGuest", isGuest));
			UrlEncodedFormEntity entity1 = null;
			try {
				entity1 = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// 请求服务器url
			String url = getUrlFromSp() + "servlet/StartTableServlet";
			// 获得请求对象HttpPost
			HttpPost request = HttpUtil.getHttpPost(url);
			// 设置查询参数
			request.setEntity(entity1);
			// 获得响应结果
			getResult(request);
			// 将开桌生成的订单Id，赋值给全局orderId
			while(true){
				if(flag)
					break;
			}
			flag=false;
			Toast.makeText(OrderActivity.this, "订单号：" + result,
					Toast.LENGTH_LONG).show();
		}
	};
	public String getUrlFromSp(){
		SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
		String  url=pref.getString("url", "");
		return url;
	}
	private void getResult(final HttpPost request){
		new Thread(new Runnable() {
			@Override
			public void run() {
				result = HttpUtil.queryStringForPost(request);
				orderId = result;
				SharedPreferences.Editor editor =getSharedPreferences("data",MODE_PRIVATE).edit();
				editor.putString("orderId", orderId);
				editor.apply();
			flag=true;
			}
		}).start();
	}
	// 初始化UI界面
	private void initViews() {
		checkBox = (CheckBox) findViewById(R.id.all_check);
		listView = (ListView) findViewById(R.id.main_listView);
		popTotalPrice = (TextView) findViewById(R.id.shopTotalPrice);
		popDelete = (TextView) findViewById(R.id.delete);
		popRecycle = (TextView) findViewById(R.id.collection);
		popCheckOut = (TextView) findViewById(R.id.checkOut);
		layout = (LinearLayout) findViewById(R.id.price_relative);

		ClickListener cl = new ClickListener();
		checkBox.setOnClickListener(cl);
		popDelete.setOnClickListener(cl);
		popCheckOut.setOnClickListener(cl);
		popRecycle.setOnClickListener(cl);
	}

	// 初始化数据

	private void init() {
		getListData();
		list = ShoppingCanst.list;
		adapter = new ShopAdapter(this, list, handler, R.layout.main_item);
		listView.setAdapter(adapter);
	}

	// 获取集合数据
	private void getListData() {
		ShoppingCanst.list = new ArrayList<shopBean>();
		shopBean bean = new shopBean();
		bean.setShopId(1);
		bean.setShopPicture(R.drawable.shoes1);
		bean.setStoreName("");
		bean.setShopName("萝卜酥饼");
		bean.setShopDescription("酥脆可口，香气逼人");
		bean.setShopPrice(2);
		bean.setShopNumber(1);
		bean.setChoosed(false);
		ShoppingCanst.list.add(bean);
		shopBean bean2 = new shopBean();
		bean2.setShopId(4);
		bean2.setShopPicture(R.drawable.shoes2);
		bean2.setStoreName("");
		bean2.setShopName("绿豆糕");
		bean2.setShopDescription("香嫩酥滑，绿色食品");
		bean2.setShopPrice(1);
		bean2.setShopNumber(1);
		bean2.setChoosed(false);
		ShoppingCanst.list.add(bean2);
		shopBean bean3 = new shopBean();
		bean3.setShopId(5);
		bean3.setShopPicture(R.drawable.shoes3);
		bean3.setStoreName("");
		bean3.setShopName("米酒汤圆");
		bean3.setShopDescription("甜而不腻");
		bean3.setShopPrice(3);
		bean3.setShopNumber(1);
		bean3.setChoosed(false);
		ShoppingCanst.list.add(bean3);
		shopBean bean4 = new shopBean();
		bean4.setShopId(6);
		bean4.setShopPicture(R.drawable.shoes4);
		bean4.setStoreName("");
		bean4.setShopName("苹果派");
		bean4.setShopDescription("饭后甜点，健康美味");
		bean4.setShopPrice(2);
		bean4.setShopNumber(1);
		bean4.setChoosed(false);
		ShoppingCanst.list.add(bean4);
		shopBean bean5 = new shopBean();
		bean5.setShopId(14);
		bean5.setShopPicture(R.drawable.rc2);
		bean5.setStoreName("");
		bean5.setShopName("单个荷包蛋 ");
		bean5.setShopDescription("价格实惠，好吃不贵");
		bean5.setShopPrice(3);
		bean5.setShopNumber(1);
		bean5.setChoosed(false);
		ShoppingCanst.list.add(bean5);
		shopBean bean6 = new shopBean();
		bean6.setShopId(15);
		bean6.setShopPicture(R.drawable.rc3);
		bean6.setStoreName("");
		bean6.setShopName("东坡肉");
		bean6.setShopDescription("");
		bean6.setShopPrice(14);
		bean6.setShopNumber(1);
		bean6.setChoosed(false);
		ShoppingCanst.list.add(bean6);
		shopBean bean7 = new shopBean();
		bean7.setShopId(16);
		bean7.setShopPicture(R.drawable.rc4);
		bean7.setStoreName("");
		bean7.setShopName("剁椒鱼头");
		bean7.setShopDescription("");
		bean7.setShopPrice(18);
		bean7.setShopNumber(1);
		bean7.setChoosed(false);
		ShoppingCanst.list.add(bean7);
		shopBean bean9 = new shopBean();
		bean9.setShopId(17);
		bean9.setShopPicture(R.drawable.tp1);
		bean9.setStoreName("");
		bean9.setShopName("马蹄糕");
		bean9.setShopDescription("");
		bean9.setShopPrice(7);
		bean9.setShopNumber(1);
		bean9.setChoosed(false);
		ShoppingCanst.list.add(bean9);
		shopBean bean10 = new shopBean();
		bean10.setShopId(13);
		bean10.setShopPicture(R.drawable.rc1);
		bean10.setStoreName("");
		bean10.setShopName("拔丝山药");
		bean10.setShopDescription("");
		bean10.setShopPrice(12);
		bean10.setShopNumber(1);
		bean10.setChoosed(false);
		ShoppingCanst.list.add(bean10);
		shopBean bean11 = new shopBean();
		bean11.setShopId(8);
		bean11.setShopPicture(R.drawable.lc1);
		bean11.setStoreName("");
		bean11.setShopName("凉拌豆腐");
		bean11.setShopDescription("");
		bean11.setShopPrice(7);
		bean11.setShopNumber(1);
		bean11.setChoosed(false);
		ShoppingCanst.list.add(bean11);
		shopBean bean12 = new shopBean();
		bean12.setShopId(9);
		bean12.setShopPicture(R.drawable.lc2);
		bean12.setStoreName("");
		bean12.setShopName("凉拌海带");
		bean12.setShopDescription("");
		bean12.setShopPrice(8);
		bean12.setShopNumber(1);
		bean12.setChoosed(false);
		ShoppingCanst.list.add(bean12);
		shopBean bean13 = new shopBean();
		bean13.setShopId(10);
		bean13.setShopPicture(R.drawable.lc3);
		bean13.setStoreName("");
		bean13.setShopName("凉拌金针菇");
		bean13.setShopDescription("");
		bean13.setShopPrice(7);
		bean13.setShopNumber(1);
		bean13.setChoosed(false);
		ShoppingCanst.list.add(bean13);
		shopBean bean14 = new shopBean();
		bean14.setShopId(11);
		bean14.setShopPicture(R.drawable.lc4);
		bean14.setStoreName("");
		bean14.setShopName("凉拌木耳");
		bean14.setShopDescription("");
		bean14.setShopPrice(9);
		bean14.setShopNumber(1);
		bean14.setChoosed(false);
		ShoppingCanst.list.add(bean14);
		shopBean bean15 = new shopBean();
		bean15.setShopId(12);
		bean15.setShopPicture(R.drawable.lc5);
		bean15.setStoreName("");
		bean15.setShopName("凉拌西兰花");
		bean15.setShopDescription("");
		bean15.setShopPrice(8);
		bean15.setShopNumber(1);
		bean15.setChoosed(false);
		ShoppingCanst.list.add(bean15);

	}

	// 事件点击监听器
	private final class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.all_check: // 全选
				selectedAll();
				break;
			case R.id.delete: // 删除
				String shopIndex = deleteOrCheckOutShop();
				showDialogDelete(shopIndex);
				break;
			case R.id.checkOut: // 下单
				// 添菜监听器
				// 下单监听器
				// 调用点菜方法
				// addMeal();// 遍历点菜列表

				/*
				 * for (int i = 0; i < caidan.size(); i++) { // 获得其中点菜map Map
				 * map = (Map)caidan.get(i); // 获得点菜项 String name = (String)
				 * map.get("name"); String num = String.valueOf(map.get("num"));
				 * String price = String.valueOf(map.get("price")); String
				 * shopid = String.valueOf(map.get("shopid"));
				 * 
				 * System.out.println("-------1-------现在输出获取到的节点数据-----------1-----"
				 * ); System.out.println("菜名:"+name);
				 * System.out.println("数量:"+num); System.out.println("价格:"+
				 * price); System.out.println("ID:"+ shopid);
				 * System.out.println(
				 * "------1--------输出完毕获取到的节点数据---------1-------");
				 * 
				 * }
				 */

				if(orderId==null){
					Toast.makeText(getApplicationContext(), "请在开桌后点餐", Toast.LENGTH_SHORT).show(); 
				}else{
					for (int i = 0; i < caidan.size(); i++) {
						// 获得其中点菜map
						// 获得其中点菜map
						Map map = (Map) caidan.get(i);
						// 获得点菜项
						String name = (String) map.get("name");
						String num = String.valueOf(map.get("num"));
						String price = String.valueOf(map.get("price"));
						String shopid = String.valueOf(map.get("shopid"));

						System.out
								.println("-------1-------现在输出获取到的节点数据-----------1-----");
						System.out.println("菜名:" + name);
						System.out.println("数量:" + num);
						System.out.println("价格:" + price);
						System.out.println("ID:" + shopid);
						System.out
								.println("------1--------输出完毕获取到的节点数据---------1-------");

						// 封装到请求参数中
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						// 添加到请求参数中
						params.add(new BasicNameValuePair("menuId", shopid));
						params.add(new BasicNameValuePair("orderId", orderId));
						params.add(new BasicNameValuePair("num", num));
						params.add(new BasicNameValuePair("remark", ""));
						params.add(new BasicNameValuePair("isGuest", isGuest));
						params.add(new BasicNameValuePair("userId", userId));
						UrlEncodedFormEntity entity1 = null;
						try {
							entity1 = new UrlEncodedFormEntity(params, HTTP.UTF_8);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						// 请求服务器Servlet的url
						String url = getUrlFromSp()
								+ "servlet/OrderDetailServlet";
						// 获得HttpPost对象
						HttpPost request = HttpUtil.getHttpPost(url);
						// 为请求设置参数
						request.setEntity(entity1);
						// 获得返回结果
						String result = HttpUtil.queryStringForPost(request);

						goCheckOut();
					}

					// 添菜方法

					if (v.getId() == R.id.checkOut) { // 纭鐐瑰嚮鎸夐挳
						Toast.makeText(getApplicationContext(), "下单成功！",
								Toast.LENGTH_LONG).show();
					} else if (v.getId() == R.id.title_left) { // 宸︽爣棰樿繑鍥�
//						finish();

						break;

					}
				}
			}
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		holder = new ViewHolder();
		holder.name_tv = (TextView) convertView.findViewById(R.id.shop_name);
		holder.price_tv = (TextView) convertView.findViewById(R.id.shop_price);
		holder.num_et = (TextView) convertView.findViewById(R.id.shop_number);
		convertView.setTag(holder);

		return convertView;
	}

	@SuppressWarnings("unused")
	private final class ViewHolder {
		public TextView name_tv; // 搴楀鍚嶇О
		public TextView price_tv; // 璇ュ晢鍝佹墍闇�鎬婚噾棰�
		// 鍟嗗搧鎻忚堪
		public TextView num_et; // 鍟嗗搧鏁伴噺
		// 缁欏晢瀹剁暀瑷�
	}

	// 结算
	private void addMeal() {
		// 获得LayoutInflater实例
		LayoutInflater inflater = LayoutInflater.from(this);
		// 实例化在弹出对话框中添加的视图
		final View v = inflater.inflate(R.layout.main_item, null);
		// 获得视图中的Spinner对象，菜单下拉列表
		final ListView main_listView = (ListView) v
				.findViewById(R.id.main_listView);
		// 获得视图中的EditText对象,数量
		TextView numEt = (TextView) v.findViewById(R.id.shop_number);

		// 访问本地SQLite数据库中MenuTbl表的Uri
		Uri uri = Uri.parse("content://com.amaker.provider.MENUS/menu1");
		// 查询列
		String[] projection = { "_id", "name", "price" };
		// 获得ContentResolver实例
		ContentResolver cr = getContentResolver();
		// 查询放回游标
		Cursor c = cr.query(uri, projection, null, null, null);
		// 实例化SimpleCursorAdapter
		SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this,
				R.layout.spinner_lo, c,
				new String[] { "_id", "price", "name" }, new int[] {
						R.id.id_TextView01, R.id.price_TextView02,
						R.id.name_TextView03, });

		// 获得AlertDialog.Builder实例
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
		// 设置标题
		// 设置自定义视图
		.setView(v)
		// 设置确定按钮
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					// 确定按钮事件
					public void onClick(DialogInterface dialog, int id) {

						// 获得ListView中的自定义视图LinearLayout

						// 获得ListView中的自定义视图LinearLayout
						LinearLayout v1 = (LinearLayout) main_listView
								.getSelectedView();
						TextView id_tv = (TextView) v1
								.findViewById(R.id.id_TextView01);
						// 获得TextView，菜价格
						TextView price_tv = (TextView) v1
								.findViewById(R.id.price_TextView02);
						// 获得TextView，菜名称
						TextView name_tv = (TextView) v1
								.findViewById(R.id.name_TextView03);
						// 获得EditText，菜数量
						EditText num_et = (EditText) v
								.findViewById(R.id.numEditText);
						// 获得EditText，菜备注
						EditText remark_et = (EditText) v
								.findViewById(R.id.add_remarkEditText);

						// 菜价格值
						String priceStr = price_tv.getText().toString();
						// 菜名称值
						String nameStr = name_tv.getText().toString();
						// 菜数量值
						String numStr = num_et.getText().toString();

						// 封装到Map中
						map = new HashMap();

						map.put("name", nameStr);
						map.put("num", numStr);
						map.put("price", priceStr);

						// 添加到ListView
						data.add(map);

						// 关联的TextView
						to[0] = R.id.id_ListView;
						to[1] = R.id.shop_name;
						to[2] = R.id.shop_price;
						to[3] = R.id.remark_ListView;

						// 实例化SimpleAdapter
						sa = new SimpleAdapter(OrderActivity.this, data,
								R.layout.main_item, from, to);
						// 为ListView绑定数据
						main_listView.setAdapter(sa);

					}
				}).setNegativeButton("取消", null);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void goCheckOut() {
//		String shopIndex = deleteOrCheckOutShop();
//		Intent checkOutIntent = new Intent(OrderActivity.this,
//				MainMenuActivity.class);
//		checkOutIntent.putExtra("shopIndex", shopIndex);
//		startActivity(checkOutIntent);
	}

	// 全选或全取消
	private void selectedAll() {
		for (int i = 0; i < list.size(); i++) {
			ShopAdapter.getIsSelected().put(i, flag);
		}
		adapter.notifyDataSetChanged();
	}

	// 删除或结算商品
	private String deleteOrCheckOutShop() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if (ShopAdapter.getIsSelected().get(i)) {
				sb.append(i);
				sb.append(",");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	// 弹出对话框询问用户是否删除被选中的商品
	private void showDialogDelete(String str) {
		final String[] delShopIndex = str.split(",");
		new AlertDialog.Builder(OrderActivity.this)
				.setMessage("您确定删除这" + delShopIndex.length + "菜品吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						for (String s : delShopIndex) {
							int index = Integer.valueOf(s);
							list.remove(index);
							ShoppingCanst.list.remove(index);
							// 连接服务器之后，获取数据库中商品对应的ID，删除商品
							// list.get(index).getShopId();
						}
						flag = false;
						selectedAll(); // 删除商品后，取消所有的选择
						flag = true; // 刷新页面后，设置Flag为true，恢复全选功能
					}
				}).setNegativeButton("取消", null).create().show();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 10) { // 更改选中商品的总价格
				float price = (Float) msg.obj;
				if (price > 0) {
					popTotalPrice.setText("￥" + price);
					layout.setVisibility(View.VISIBLE);
				} else {
					layout.setVisibility(View.GONE);
				}
			} else if (msg.what == 11) {
				// 所有列表中的商品全部被选中，让全选按钮也被选中
				// flag记录是否全被选中
				flag = !(Boolean) msg.obj;
				checkBox.setChecked((Boolean) msg.obj);
			} else if (msg.what == 12) {
				// 所有列表中的商品全部被选中，让全选按钮也被选中
				// flag记录是否全被选中
				// List caidan=List(msg.obj);
				// System.out.println(caidan);
				// 这里是获取了数据了么？
				caidan = new ArrayList();
				caidan = (List) msg.obj;
				for (int i = 0; i < caidan.size(); i++) {
					// 获得其中点菜map
					Map map = (Map) caidan.get(i);
					// 获得点菜项
					String name = (String) map.get("name");
					String num = String.valueOf(map.get("num"));
					String price = String.valueOf(map.get("price"));

					System.out
							.println("-------1-------现在输出获取到的节点数据-----------1-----");
					System.out.println("菜名:" + name);
					System.out.println("数量:" + num);
					System.out.println("价格:" + price);
					System.out
							.println("------1--------输出完毕获取到的节点数据---------1-------");

				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	 private String getUserId(){
	    	SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
	    	String  userId=pref.getString("userId", "");
	    	return userId;
	    }
}

@SuppressLint("HandlerLeak")
class ShopAdapter extends BaseAdapter {

	private Handler mHandler;
	private int resourceId; // 适配器视图资源ID
	private Context context; // 上下文对象
	private List<shopBean> list; // 数据集合List
	private LayoutInflater inflater; // 布局填充器
	private static HashMap<Integer, Boolean> isSelected;

	@SuppressLint("UseSparseArrays")
	public ShopAdapter(Context context, List<shopBean> list, Handler mHandler,
			int resourceId) {
		this.list = list;
		this.context = context;
		this.mHandler = mHandler;
		this.resourceId = resourceId;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		initDate();
	}

	// 初始化isSelected的数据
	private void initDate() {
		for (int i = 0; i < list.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		ShopAdapter.isSelected = isSelected;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		shopBean bean = list.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(resourceId, null);
			holder = new ViewHolder();
			holder.shop_photo = (ImageView) convertView
					.findViewById(R.id.shop_photo);
			holder.shop_name = (TextView) convertView
					.findViewById(R.id.shop_name);
			holder.shop_description = (TextView) convertView
					.findViewById(R.id.shop_description);
			holder.shop_price = (TextView) convertView
					.findViewById(R.id.shop_price);
			holder.shop_number = (TextView) convertView
					.findViewById(R.id.shop_number);
			holder.shop_check = (CheckBox) convertView
					.findViewById(R.id.shop_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.shop_photo.setImageResource(bean.getShopPicture());
		holder.shop_name.setText(bean.getShopName());
		holder.shop_description.setText(bean.getShopDescription());
		holder.shop_price.setText("￥" + bean.getShopPrice());
		holder.shop_number.setTag(position);
		holder.shop_number.setText(String.valueOf(bean.getShopNumber()));
		holder.shop_number.setOnClickListener(new ShopNumberClickListener());
		holder.shop_check.setTag(position);
		holder.shop_check.setChecked(getIsSelected().get(position));
		holder.shop_check
				.setOnCheckedChangeListener(new CheckBoxChangedListener());
		return convertView;
	}

	private final class ViewHolder {
		public ImageView shop_photo; // 商品图片
		public TextView shop_name; // 商品名称
		public TextView shop_description; // 商品描述
		public TextView shop_price; // 商品价格
		public TextView shop_number; // 商品数量
		public CheckBox shop_check; // 商品选择按钮
	}

	// 数量TextView点击监听器
	private final class ShopNumberClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// 获取商品的数量
			String str = ((TextView) v).getText().toString();
			int shopNum = Integer.valueOf(str);
			showDialog(shopNum, (TextView) v);
		}
	}

	private int number = 0; // 记录对话框中的数量
	private EditText editText; // 对话框中数量编辑器

	/**
	 * 弹出对话框更改商品的数量
	 * 
	 * @param shopNum
	 *            商品原来的数量
	 * @param textNum
	 *            Item中显示商品数量的控件
	 */
	private void showDialog(int shopNum, final TextView textNum) {
		View view = inflater.inflate(R.layout.number_update, null);
		Button btnSub = (Button) view.findViewById(R.id.numSub);
		Button btnAdd = (Button) view.findViewById(R.id.numAdd);
		editText = (EditText) view.findViewById(R.id.edt);
		editText.setText(String.valueOf(shopNum));
		btnSub.setOnClickListener(new ButtonClickListener());
		btnAdd.setOnClickListener(new ButtonClickListener());
		number = shopNum;
		new AlertDialog.Builder(context).setView(view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// 将用户更改的商品数量更新到服务器
						int position = (Integer) textNum.getTag();
						list.get(position).setShopNumber(number);
						handler.sendMessage(handler.obtainMessage(1, textNum));
					}
				}).setNegativeButton("取消", null).create().show();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) { // 更改商品数量
				((TextView) msg.obj).setText(String.valueOf(number));
				// 更改商品数量后，通知Activity更新需要付费的总金额
				mHandler.sendMessage(mHandler
						.obtainMessage(10, getTotalPrice()));
			} else if (msg.what == 2) {// 更改对话框中的数量
				editText.setText(String.valueOf(number));
			}
		}
	};

	// Button点击监听器
	private final class ButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.numSub) {
				if (number > 1) {
					number--;
					handler.sendEmptyMessage(2);
				}
			} else if (v.getId() == R.id.numAdd) {
				number++;
				handler.sendEmptyMessage(2);
			}
		}
	}

	// CheckBox选择改变监听器
	private final class CheckBoxChangedListener implements
			OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton cb, boolean flag) {
			int position = (Integer) cb.getTag();
			getIsSelected().put(position, flag);
			shopBean bean = list.get(position);
			bean.setChoosed(flag);
			mHandler.sendMessage(mHandler.obtainMessage(10, getTotalPrice()));
			// 如果所有的物品全部被选中，则全选按钮也默认被选中
			mHandler.sendMessage(mHandler.obtainMessage(11, isAllSelected()));
			// 菜单
			mHandler.sendMessage(mHandler.obtainMessage(12, getTotalCaidan()));

		}
	}

	/**
	 * 计算选中商品的金额
	 * 
	 * @return 返回需要付费的总金额
	 */
	private float getTotalPrice() {
		shopBean bean = null;
		float totalPrice = 0;
		for (int i = 0; i < list.size(); i++) {
			bean = list.get(i);
			if (bean.isChoosed()) {
				totalPrice += bean.getShopNumber() * bean.getShopPrice();
			}
		}
		return totalPrice;
	}

	private List getTotalCaidan() {
		shopBean bean = null;
		String priceStr = "";
		String nameStr = "";
		String numStr = "";
		String idStr = "";
		List data1 = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			bean = list.get(i);
			if (bean.isChoosed()) {
				idStr += bean.getShopId() + "-";
				nameStr += bean.getShopName() + "-";
				priceStr += bean.getShopPrice() + "-";
				numStr += bean.getShopNumber() + "-";

				HashMap map = new HashMap();
				map.put("name", bean.getShopName());
				map.put("num", bean.getShopNumber());
				map.put("price", bean.getShopPrice());
				map.put("shopid", bean.getShopId());
				data1.add(map);

				// 封装到Map中
				// data1 = new ArrayList();// 购物车数据集合
				/*
				 * HashMap map = new HashMap(); //进行字符串分割 String[] nameght
				 * =nameStr.split("-");//转换 数组 String[] priceght
				 * =priceStr.split("-");//转换 数组 String[] numght
				 * =numStr.split("-");//转换 数组 for(int j=0;j<nameght.length;j++)
				 * { map.put("name", nameght[j]); map.put("num", numght[j]);
				 * map.put("price", priceght[j]);
				 * System.out.println("--------------现在输出获取到的节点数据----------------"
				 * ); System.out.println("菜名"+nameght[j]);
				 * System.out.println("数量"+numght[j]);
				 * System.out.println("价格"+priceght[j]);
				 * System.out.println("--------------输出完毕获取到的节点数据----------------"
				 * ); } // 添加到ListView data1.add(map);
				 */
			}
		}
		/*
		 * for (int i = 0; i < data1.size(); i++) { // 获得其中点菜map Map map =
		 * (Map)data1.get(i); // 获得点菜项 String name = (String) map.get("name");
		 * String num = (String)map.get("num"); String price =
		 * (String)map.get("price");
		 * System.out.println("--------------现在输出获取到的节点数据----------------");
		 * System.out.println("菜名"+name); System.out.println("数量"+num);
		 * System.out.println("价格"+price);
		 * System.out.println("--------------输出完毕获取到的节点数据----------------"); }
		 */
		return data1;
	}

	/**
	 * 判断是否购物车中所有的商品全部被选中
	 * 
	 * @return true所有条目全部被选中 false还有条目没有被选中
	 */
	private boolean isAllSelected() {
		boolean flag = true;
		for (int i = 0; i < list.size(); i++) {
			if (!getIsSelected().get(i)) {
				flag = false;
				break;
			}
		}
		return flag;
	}
}
