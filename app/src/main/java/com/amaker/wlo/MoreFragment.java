package com.amaker.wlo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amaker.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MoreFragment extends Fragment {

    //    private SimpleAdapter adapter;
    private MoreAdapter adapter;
    private Button startBtn;
    private String orderId=null;
    private String ShopId;
    private String userId;
    private String isGuest;// 判断身份
    private CheckBox checkBox;
    private ListView list;
    // 点菜列表中具体的数据项

    private TextView popTotalPrice; // 结算的价格
    private TextView popDelete; // 删除
    private TextView popRecycle; // 收藏
    private TextView popCheckOut; // 结算
    private LinearLayout layout; // 结算布局
    private ListView main_listView;
    private Spinner tableNoSpinner;
    private EditText personNumEt;
    private List<shopBean> shopBeanList;
    private List data = new ArrayList();// 购物车数据集合
    private List<AddressBean> addressList;
    private boolean flag = true;
    private SimpleAdapter sa;
    private Map map;
    private List caidan = new ArrayList();
    private Cursor c = null;
    private View view=null;
    private int tabIndex=0;
    public static final String INTENT_INT_INDEX = "intent_int_index";
    // 点菜列表中具体的数据项

    private int[] to = new int[5];
    private String[] from = { "id", "name", "num", "price", "remark" };
    private List<DishMenu> dish=null;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentmenu, container, false);
        list = (ListView) view.findViewById(R.id.list);
        tabIndex = getArguments().getInt(INTENT_INT_INDEX);
        Log.d("MoreF", "tabIndex is："+ tabIndex);
        Log.d("MoreF", "view is：" + view);
        sendRequestWithOkHttp(tabIndex);
        initViews();
        while(true){
            if(dish!=null){
                init();
                break;
            }
        }
        Log.d("MoreF","liolioadapter 执行setListAdapter");
//        setListAdapter(adapter);
        Log.d("MoreF", "liolioadapter setListAdapter执行完毕");
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        adapter = new SimpleAdapter(getActivity(), getData(listItem, iconItem),
//                R.layout.me_function_item, new String[] { "name", "icon" },
//                new int[] { R.id.functionName, R.id.functionIcon });


    }


//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//    }

    private List<? extends Map<String, ?>> getData(String[] strs, int[] icon) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < 5; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", strs[i]);
            map.put("icon", icon[i]);
            list.add(map);
        }

        return list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }
    private void initViews() {
        Log.d("","view is:"+view);
        checkBox = (CheckBox)view. findViewById(R.id.all_check);
        list = (ListView)view. findViewById(R.id.list);
        popTotalPrice = (TextView)view. findViewById(R.id.shopTotalPrice);
        popDelete = (TextView)view. findViewById(R.id.delete);
        popRecycle = (TextView) view.findViewById(R.id.collection);
        popCheckOut = (TextView) view.findViewById(R.id.checkOut);
        layout = (LinearLayout) view.findViewById(R.id.price_relative);
        Log.d("MoreF","initView执行 liolio");
        ClickListener cl = new ClickListener();
        checkBox.setOnClickListener(cl);
        popDelete.setOnClickListener(cl);
        popCheckOut.setOnClickListener(cl);
        popRecycle.setOnClickListener(cl);
    }

    // 初始化数据

    private void init() {
        getListData(dish);
        shopBeanList = ShoppingCanst.list;
        adapter = new MoreAdapter(getContext(), shopBeanList, handler, R.layout.main_item);
        Log.d("MoreF","init执行 liolio");
        Log.d("MoreF","liolio2 listview is："+ list);
        Log.d("MoreF","liolioadapter is："+adapter);
        list.setAdapter(adapter);
        Log.d("MoreF", "liolioadapter 执行完毕");
    }

    private void sendRequestWithOkHttp(final int tabIndex){

        new Thread(new Runnable(){

            @Override
            public void run() {
                try{
                    OkHttpClient client =new OkHttpClient();
                    Request request=new Request.Builder()
                            .url(HttpUtil.BASE_URL+"servlet/GetMenusServlet?"+"tabIndex="+tabIndex).build();
                    Response reponse=client.newCall(request).execute();
                    String reponseData=reponse.body().string();
                    Log.d("TestActivity","user"+reponseData);
                    dish=parseJSONWithGSON(reponseData);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private List<DishMenu> parseJSONWithGSON(String jsonData) {
        Gson gson=new Gson();
        List<DishMenu> menul=gson.fromJson(jsonData, new TypeToken<List<DishMenu>>()
        {}.getType());
        for(DishMenu menu:menul){
            Log.d("TestActivity","Name="+menu.getName());
            Log.d("TestActivity","id="+menu.getId());
            Log.d("TestActivity","pic="+menu.getPic());
        }
        return menul;
    }

    private void setShop(shopBean bean,int id,String pic,String MenuName,String description,int Price,int ShopNumber,boolean Choosed){
        bean.setShopId(id);
        bean.setPicUrl(pic);
        bean.setShopName(MenuName);
        bean.setShopDescription(description);
        bean.setShopPrice(Price);
        bean.setShopNumber(ShopNumber);
        bean.setChoosed(Choosed);

    }
    // 获取集合数据
    private void getListData(List<DishMenu> dishMenul) {
        ShoppingCanst.list = new ArrayList<shopBean>();
        shopBean bean=null;
        int i=0;
        for(DishMenu DishMenu : dishMenul) {
            i++;
            bean=new shopBean();
            setShop(bean,DishMenu.getId(),DishMenu.getPic(),
                    DishMenu.getName(),DishMenu.getDiscribe(),DishMenu.getPrice(),1,false);
            ShoppingCanst.list.add(bean);
        }
    }

    // 事件点击监听器
    private final class ClickListener implements View.OnClickListener {
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
                    if(orderId==null){
                        Toast.makeText(getContext(), "请在开桌后点餐", Toast.LENGTH_SHORT).show();
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
                            String url = HttpUtil.BASE_URL
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
                            Toast.makeText(getContext(), "下单成功！",
                                    Toast.LENGTH_LONG).show();
                        } else if (v.getId() == R.id.title_left) { // 宸︽爣棰樿繑鍥�
//						Intent intent =new Intent();
//						intent.putExtra("data_retuen", orderId);
//						setResult(RESULT_OK,intent);
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

    //	Intent intent =new Intent();
//	intent.putExtra("data_retuen", orderId);
//	setResult(RESULT_OK,intent);
//	finish();
    private void goCheckOut() {
        String shopIndex = deleteOrCheckOutShop();
//		Intent checkOutIntent = new Intent(HotMenuActivity.this,
//				MainMenuActivity.class);
//		Intent intent =new Intent();
//		intent.putExtra("data_retuen", orderId);
//		intent.putExtra("shopIndex", shopIndex);
//		setResult(RESULT_OK,intent);
//		finish();
    }

    // 全选或全取消
    private void selectedAll() {
        for (int i = 0; i < shopBeanList.size(); i++) {
            HotAdapter.getIsSelected().put(i, flag);
        }
        adapter.notifyDataSetChanged();
    }

    // 删除或结算商品
    private String deleteOrCheckOutShop() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < shopBeanList.size(); i++) {
            if (HotAdapter.getIsSelected().get(i)) {
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
        new AlertDialog.Builder(getContext())
                .setMessage("您确定删除这" + delShopIndex.length + "菜品吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        for (String s : delShopIndex) {
                            int index = Integer.valueOf(s);
                            shopBeanList.remove(index);
                            ShoppingCanst.list.remove(index);
                            // 连接服务器之后，获取数据库中商品对应的ID，删除商品
                            // shopBeanList.get(index).getShopId();
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


    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}

class MoreAdapter extends BaseAdapter {

    private Handler mHandler;
    private int resourceId; // 适配器视图资源ID
    private Context context; // 上下文对象
    private List<shopBean> list; // 数据集合List
    private LayoutInflater inflater; // 布局填充器
    private static HashMap<Integer, Boolean> isSelected;

    @SuppressLint("UseSparseArrays")
    public MoreAdapter(Context context, List<shopBean> list, Handler mHandler,
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
        MoreAdapter.isSelected = isSelected;
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
        String internetUrl =bean.getPicUrl();
        Picasso
                .with(context)
                .load(internetUrl)
                .into(holder.shop_photo);

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
    private final class ShopNumberClickListener implements View.OnClickListener {
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
    private final class ButtonClickListener implements View.OnClickListener {
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
            CompoundButton.OnCheckedChangeListener {
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



