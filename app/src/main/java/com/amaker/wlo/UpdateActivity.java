package com.amaker.wlo;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amaker.provider.DBHelper;
import com.amaker.provider.Menus;
import com.amaker.util.HttpUtil;
import com.amaker.provider.*;
import android.content.ContentProvider;
/**
 * 
 * @author ����־
 * ʵ������ͬ������
 */

//�ڡ�com.amaker.wlo"�д���һ������Ϊ��UpdateActivity����Activity���ڸ����м̳�ListActivity������ͨ��ListView��չʾ��
	//��onCreate���������л��ListViewʵ������ΪListView�����ݡ�
public class UpdateActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���ñ���
		setTitle("��׿���ϵͳ���½���");
		// ���ListViewʵ��
		ListView listView = getListView();
		// ����ListViewҪ�󶨵�����
		String[] items = {"���²��ױ�����[MenuTbl]", "���²���������[TableTbl]" };
		// ʵ����adapter
		ListAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		// ΪListView����adapter
		listView.setAdapter(adapter);
	}

	
	
	//����ListActivity��onListItemClick������������ӦListView�б��еĵ����¼�
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		switch (position) {
		// ���²��ױ�����
		case 0:
			confirm(1);
			break;
		// ������λ������
		case 1:
			confirm(2);
			break;
		default:
			break;
		}
	}
	
	
	
	// ȷ�϶Ի���
	//����һ��confirm������������ʾ�û�ȷ���Ƿ������Ҫͬ�����ݣ���ֹ�û������
	private void confirm(final int item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("�����Ҫ������?").setCancelable(false).setPositiveButton(
				"ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (item == 1) {
							// ���²��ױ�����
							updateMenu();
						} else {
							// ������λ������
							updateTable();
						}
					}
				}).setNegativeButton("ȡ��",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	
	
	
	//�ڸ����ж���һ�����²��ױ�ķ���updateMenu�������÷������÷�����Servlet���XML��ʽ����������
	//ͨ��Java DOM������XML��ò��ױ�����,�����ݱ��浽����SQLite���ݿ���
	// ���²��ױ�
	private void updateMenu() {
		// ���ʷ�����url
			String urlStr = HttpUtil.BASE_URL + "servlet/UpdateServlet";
		try {
			// ʵ����URL����
			URL url = new URL(urlStr);
			// ������
			//HttpURLConnection conn = (HttpURLConnection)url.openConnection();//�˴��иĶ�
			URLConnection conn=url.openConnection();
			// ���������
		//	conn.setRequestMethod("GET");
		//	conn.setReadTimeout(5*1000);  
		//	if(conn.getResponseCode()== 200){
			InputStream in = conn.getInputStream();
			// ʵ����DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			// ʵ����DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();
			// ���Document
			Document doc = builder.parse(in);
			// ��ýڵ��б�
			NodeList nl = doc.getElementsByTagName("menu");
			// ��÷������ݽӿ�ContentResolver
			ContentResolver cr = getContentResolver();
			// �������ݵ�Uri
			Uri uri1 = Menus.CONTENT_URI;
			// ɾ������SQLite���ݿ��в��ױ��е�����
			cr.delete(uri1, null, null);

			// ѭ�������ݱ��浽���ױ�
			for (int i = 0; i < nl.getLength(); i++) {
				// ʵ����ContentValues
				ContentValues values = new ContentValues();
				// ����XML�ļ���ò˵�id
				int id = Integer.parseInt(doc.getElementsByTagName("id")
						.item(i).getFirstChild().getNodeValue());
				// ����
				String name = doc.getElementsByTagName("name").item(i)
						.getFirstChild().getNodeValue();
				// ͼƬ·��
				String pic = doc.getElementsByTagName("pic").item(i)
						.getFirstChild().getNodeValue();
				// �۸�
				int price = Integer.parseInt(doc.getElementsByTagName("price")
						.item(i).getFirstChild().getNodeValue());
				// ������
				int typeId = Integer.parseInt(doc
						.getElementsByTagName("typeId").item(i).getFirstChild()
						.getNodeValue());
				// ��ע
				String remark = doc.getElementsByTagName("remark").item(i)
						.getFirstChild().getNodeValue();
				
				// ��ӵ�ContenValues����
				values.put("_id", id);
				values.put("name", name);
				values.put("price", price);
				values.put("pic", pic);
				values.put("typeId", typeId);
				values.put("remark", remark);
		
				// ���뵽���ݿ�
				cr.insert(uri1, values);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*ContentValues values = new ContentValues();
		ContentResolver cr = getContentResolver();
		Uri uri1 = Menus.CONTENT_URI;
		values.put("_id", 2);
		values.put("name", "fwd");
		values.put("price", 12);
		values.put("pic", "A/B/C/D");
		values.put("typeId", 4);
		values.put("remark", "nice");
		cr.insert(uri1, values);*/
	}

	// �������ű�
	private void updateTable() {
		// ���ʷ�����url
		String urlStr = HttpUtil.BASE_URL + "servlet/UpdateTableServlet";
		//SQLiteDatabase db = new DBHelper(getBaseContext()).getWritableDatabase();
		try {
			// ʵ����URL����
			URL url = new URL(urlStr);
			// ������
			URLConnection conn = url.openConnection();
			// ���������
			InputStream in = conn.getInputStream();
			// ʵ����DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			// ʵ����DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();
			// ���Document
			Document doc = builder.parse(in);
			// ��ýڵ��б�
			NodeList nl = doc.getElementsByTagName("Table");
			// ��÷������ݽӿ�ContentResolver
			ContentResolver cr = getContentResolver();
			// �������ݵ�Uri
			Uri uri1= Tables.CONTENT_URI;
			// ɾ������SQLite���ݿ��в��ױ��е�����
			cr.delete(uri1, null, null);

			// ѭ�������ݱ��浽���ױ�
			for (int i = 0; i < nl.getLength(); i++) {
				// ʵ����ContentValues
				ContentValues values = new ContentValues();
				// ����XML�ļ���ò˵�id
				int id = Integer.parseInt(doc.getElementsByTagName("id")
						.item(i).getFirstChild().getNodeValue());
				// ����
				int num = Integer.parseInt(doc.getElementsByTagName("num")
						.item(i).getFirstChild().getNodeValue());
				// ����״̬
			//	int flag = Integer.parseInt(doc
			//			.getElementsByTagName("flag").item(i).getFirstChild()
			//			.getNodeValue());
				// ����
				String description = doc.getElementsByTagName("description").item(i)
						.getFirstChild().getNodeValue();
				//ÿ������
				int seatNum = Integer.parseInt(doc.getElementsByTagName("seatNum")
						.item(i).getFirstChild().getNodeValue());
				
				//Log.d("UpdateActivity",id+"����cookseatNum="+seatNum);
				// ��ӵ�ContenValues����
				values.put("_id", id);
				values.put("num", num);
			//	values.put("flag", flag);
				values.put("description", description);
				values.put("seatNum", seatNum);
				// ���뵽���ݿ�
				cr.insert(uri1, values);
			}
			/*ContentValues values = new ContentValues();
			values.put("_id", 2);
			values.put("num",4);
		//	values.put("flag", 1);
			values.put("description", "����");
			cr.insert(uri1, values);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}}

	
