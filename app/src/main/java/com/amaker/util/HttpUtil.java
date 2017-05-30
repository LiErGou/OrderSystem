package com.amaker.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.SharedPreferences;
import android.util.Log;

public class HttpUtil {
	// ��URL
	//public static final String BASE_URL="http://10.128.228.187:8080/WirelessOrder_Server/";
	// ���Get�������request

	public static HttpGet getHttpGet(String url){
		HttpGet request = new HttpGet(url);
		 return request;
	}
	// ���Post�������request
	public static HttpPost getHttpPost(String url){
		 HttpPost request = new HttpPost(url);
		 return request;
	}
	// �����������Ӧ����response
	public static HttpResponse getHttpResponse(HttpGet request) throws ClientProtocolException, IOException{
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}
	// �����������Ӧ����response
	public static HttpResponse getHttpResponse(HttpPost request) throws ClientProtocolException, IOException{
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}
	
	// ����Post���󣬻����Ӧ��ѯ���
	public static String queryStringForPost(String url){
		// ���url���HttpPost����
		HttpPost request = HttpUtil.getHttpPost(url);
		String result = null;
		try {
			// �����Ӧ����
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// �ж��Ƿ�����ɹ�
			Log.w("HttpUtil", response+"��response��cook");
			Log.w("HttpUtil", response.getStatusLine().getStatusCode()+"cook");
			if(response.getStatusLine().getStatusCode()==200){
				// �����Ӧ
				result = EntityUtils.toString(response.getEntity());
				return result;
			}else {
				Log.w("HttpUtil", "����cook");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "�����쳣��";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "�����쳣��";
			return result;
		}
        return null;
    }

	// �����Ӧ��ѯ���
	public static String queryStringForPost(HttpPost request){
		String result = null;
		try {
			// �����Ӧ����
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// �ж��Ƿ�����ɹ�
			if(response.getStatusLine().getStatusCode()==200){
				// �����Ӧ
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "�����쳣��";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "�����쳣��";
			return result;
		}
        return null;
    }
	// ����Get���󣬻����Ӧ��ѯ���
	public static  String queryStringForGet(String url){
		// ���HttpGet����
		HttpGet request = HttpUtil.getHttpGet(url);
		String result = null;
		try {
			// �����Ӧ����
			HttpResponse response = HttpUtil.getHttpResponse(request);
			// �ж��Ƿ�����ɹ�
			if(response.getStatusLine().getStatusCode()==200){
				// �����Ӧ
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = "�����쳣��";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = "�����쳣��";
			return result;
		}
        return null;
    }
}
