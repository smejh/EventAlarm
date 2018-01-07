package com.jh.eventalarm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class SelectEventInfo {

	private final String url = "http://www.lottecinema.co.kr/LCWS/Event/EventData.aspx";
	private final String param = "paramList={\"MethodName\":\"GetEventLists\",\"channelType\":\"MW\",\"osType\":\"Chrome\",\"osVersion\":\"Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36\",\"EventClassificationCode\":\"20\",\"SearchText\":\"\",\"CinemaID\":\"\",\"PageNo\":1,\"PageSize\":4}";
	private final int SECOND = 1000;
	private final int MINUTE = 60 * SECOND;
	
	private final String fileName = "/conf/push_key.txt";

	public void getEventInfo() {
		int min = 20;
		Map<String, String> map = new HashMap<String, String>();
		SendPush sp = new SendPush();
		sp.setPushKeyList(getPushKeyList());

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SECOND).setConnectTimeout(SECOND).build();

		StringEntity entity = new StringEntity(param, ContentType.create("application/json", "UTF-8"));
		HttpPost post = new HttpPost(url);

		post.setConfig(requestConfig);
		post.setEntity(entity);
		post.setHeader("Content-Encoding", "gzip");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

		String eventId = "";
		String eventName = "";
		JSONObject jo;
		JSONArray ja;
		
		try {
			for (;;) {
				response = httpclient.execute(post);

				System.out.println(new Date());
				
				jo = (JSONObject) JSONValue.parse(getReturnMsg(response));
				ja = (JSONArray) jo.get("Items");

				for (int inx = 0, cnt = ja.size(); inx < cnt; inx++) {
					jo = (JSONObject) ja.get(inx);
					eventName = jo.get("EventName").toString();
					
					if (isHaveEvent(eventName)) {
						eventId = jo.get("EventID").toString();
					
						if (!map.containsKey(eventId)) {
							map.put(eventId, eventName);
							sp.sendFCM(eventName);
						}
					}
				}
				
				Thread.sleep(min * MINUTE);
			}
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isHaveEvent(String eventName) {
		if ((eventName.contains("얼리버드")) || (eventName.contains("얼리 버드")) || (eventName.contains("1+1")) || (eventName.contains("1 + 1"))) {
			return true;
		}
		
		return false;
	}

	private String getReturnMsg(CloseableHttpResponse response) throws ParseException, IOException {
		String m = EntityUtils.toString(response.getEntity());
		return m;
	}
	
	public ArrayList<String> getPushKeyList() {
		ArrayList<String> result = new ArrayList<String>();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(System.getProperties().getProperty("user.dir") + fileName));
			
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				result.add(temp);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
}