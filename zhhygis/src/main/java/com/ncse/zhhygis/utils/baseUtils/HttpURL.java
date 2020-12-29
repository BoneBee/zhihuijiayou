package com.ncse.zhhygis.utils.baseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zh.bean.login.MyStaff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class HttpURL {

    //携带token使用
	public static String getContent(String path, String param,String token) throws IOException {
		// 打开连接
		URL url = new URL(path);
		URLConnection urlConnection = url.openConnection();
		HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
		httpUrlConnection.setRequestProperty("Charset", "UTF-8");
		httpUrlConnection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
		httpUrlConnection.setRequestProperty("token",token);
		if (param != null) {
			// 以POST方式处理
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestMethod("POST");
			// 发送post请求
			OutputStream outputStream = httpUrlConnection.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream);
			if (param != null) {
				printWriter.write(param);
			}
			printWriter.flush();
			printWriter.close();
			outputStream.close();
		} else {
			// 默认以GET方式处理
		}

		// 接收处理结果反馈
		String resultString = "";
		// long startTime = System.currentTimeMillis();
		InputStream inputStream = httpUrlConnection.getInputStream();
		// long endTime = System.currentTimeMillis();
		// System.out.println(endTime - startTime);

		int responseCode = httpUrlConnection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			String line = bufferedReader.readLine();
			while (line != null) {
				resultString += line;
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		} else {
			// TODO: 改为日志
			resultString="服务返回异常：" + responseCode;
		}
		inputStream.close();
		httpUrlConnection.disconnect();
		return resultString;
	}
    //不携带token使用
	public static String getRenyuanContent(String path, String param) throws IOException {
		// 打开连接
		URL url = new URL(path);
		URLConnection urlConnection = url.openConnection();
		HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
		httpUrlConnection.setRequestProperty("Charset", "UTF-8");
		httpUrlConnection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
		if (param != null) {
			// 以POST方式处理
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestMethod("POST");
			// 发送post请求
			OutputStream outputStream = httpUrlConnection.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream);
			if (param != null) {
				printWriter.write(param);
			}
			printWriter.flush();
			printWriter.close();
			outputStream.close();
		} else {
			// 默认以GET方式处理
		}

		// 接收处理结果反馈
		String resultString = "";
		// long startTime = System.currentTimeMillis();
		InputStream inputStream = httpUrlConnection.getInputStream();
		// long endTime = System.currentTimeMillis();
		// System.out.println(endTime - startTime);

		int responseCode = httpUrlConnection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			String line = bufferedReader.readLine();
			while (line != null) {
				resultString += line;
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		} else {
			// TODO: 改为日志
			resultString="服务返回异常：" + responseCode;
		}
		inputStream.close();
		httpUrlConnection.disconnect();
		return resultString;
	}


	public static String getContent(String path, Map param,String token) throws IOException {
		JSONObject obj = new JSONObject(param);
		return getContent(path,obj.toString(),token);
	}
   //获得人员详情
    public static String getRenyuan(String path,Map param) throws Exception{
		JSONObject obj = new JSONObject(param);
		return getRenyuanContent(path,obj.toString());
	}

	/*public static String getgaodeGps(String url){
		String returnjson="";
		try {
			
			returnjson = getContent(url,null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("报错："+e.getMessage());
		}
		return returnjson;
	}*/

	public static String taibaoGetgaodeGps(Map mapAddress,String ak,String wcity){

		String recAddr=(String)mapAddress.get("recAddr");
		String apAddr=(String)mapAddress.get("apAddr");
		String pAddr=(String)mapAddress.get("pAddr");
		String returns="";
		JSONObject resultJo;
		String status="0";
		boolean flag=false;
		String rereturn="";
		//RedisUtil redisUtil=(RedisUtil)mapAddress.get("redisUtil");
		if(!"".equals(recAddr)){
			
			/*returns=(String)redisUtil.get(recAddr);
			if(returns==null || "".equals(returns)) {
				returns=getgaodeGps(recAddr,ak);
				redisUtil.set(recAddr, returns);
			}
			*/
			resultJo = JSON.parseObject(returns);
			status = resultJo.getString("status");
			if("1".equals(status)) {

				JSONArray resultJa = resultJo.getJSONArray("geocodes");
				if(resultJa!=null && resultJa.size()>0) {
					rereturn=returns;
					mapAddress.put("sydz", recAddr);
				}
				for (int i = 0; i < resultJa.size(); i++) {
					JSONObject itemJo = resultJa.getJSONObject(i);
					String city = null == itemJo.getString("city")?"":itemJo.getString("city");
					/*if(city.equals("") ||city.equals("[]")) {
						break;
					}*/
					String area = null == itemJo.getString("district")?"":itemJo.getString("district");
					if(!area.equals("") && !area.equals("[]") && wcity.equals(city) ) {
						//mapAddress.put("sydz", recAddr);
						return returns;
					}
				}

			}
		}
		if(!"".equals(apAddr)){
			/*returns=(String)redisUtil.get(apAddr);
			if(returns==null || "".equals(returns)) {
				returns=getgaodeGps(apAddr,ak);
				redisUtil.set(apAddr, returns);
			}*/

			resultJo = JSON.parseObject(returns);
			status = resultJo.getString("status");

			if("1".equals(status)) {

				JSONArray resultJa = resultJo.getJSONArray("geocodes");
				if(resultJa!=null && resultJa.size()>0) {
					rereturn=returns;
					mapAddress.put("sydz", apAddr);
				}
				for (int i = 0; i < resultJa.size(); i++) {
					JSONObject itemJo = resultJa.getJSONObject(i);
					String city = null == itemJo.getString("city")?"":itemJo.getString("city");
					/*if(city.equals("") ||city.equals("[]")) {
						break;
					}*/
					String area = null == itemJo.getString("district")?"":itemJo.getString("district");
					if(!area.equals("") && !area.equals("[]")  && wcity.equals(city)  ) {
						//mapAddress.put("sydz", apAddr);
						return returns;
					}
				}

			}
		}
		if(!"".equals(pAddr)) {
			/*returns=(String)redisUtil.get(pAddr);
			if(returns==null || "".equals(returns)) {
				returns=getgaodeGps(pAddr,ak);
				redisUtil.set(pAddr, returns);
			}*/
			//returns=getgaodeGps(pAddr,ak);	
			resultJo = JSON.parseObject(returns);
			status = resultJo.getString("status");
			if("1".equals(status)) {
				JSONArray resultJa = resultJo.getJSONArray("geocodes");
				if(resultJa!=null && resultJa.size()>0) {
					rereturn=returns;
					mapAddress.put("sydz", pAddr);
				}
			}
		}
		if(rereturn.equals("")) {
			rereturn=returns;
		}
		return rereturn;
	}



}
