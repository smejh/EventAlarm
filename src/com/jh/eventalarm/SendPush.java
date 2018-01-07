package com.jh.eventalarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SendPush {

	private final String CALL_URL = "https://fcm.googleapis.com/fcm/send";
	private final String SERVER_KEY = "key=AAAANDsVCfE:APA91bH2OUy4079_OGOEzsU_Jlj5AZf8srJsK_x0I-2MHVen0PECLOD_qqWZ4YBAIMymEhTbjTJmGQ_yBQDmGVe8i9l-2UFGR9dAh98AG08LrhkGaB80ffX9ZTn4VSAb1Xchphfiwvq-";
	//private final String [] PUSH_KEY = {"fH7nWhedtl4:APA91bETMSg0UaXBJYn4Vp4nScHwpAktXUHoFLFt6ekYoKCbHF0yFRYB8DHzDjmuiduKqemWdN3qiudNkGa5hfMTETaxIouTUaL-_W9dDYZXT5AXPuhnn7Jk5gArDqMBYyVzqR8ZmLy5", "eYoRxWLYRMA:APA91bEsRsyabZib8rfcNiaROl_oJU8rupSbuSf7OldXFwHay8T5lSrZDanc_MVhB5fdmH_ciABza5O6MAnrmjzLg4Bz4QOeRuvV5K-KxF801nfETufu7Wccy4QgtRmfHBH76Z9f2Stn"};
	
	private ArrayList<String> pushKeyList;
	
	public ArrayList<String> getPushKeyList() {
		return pushKeyList;
	}

	public void setPushKeyList(ArrayList<String> pushKeyList) {
		this.pushKeyList = pushKeyList;
	}

	private final String param = 
	"{\"to\" : \"PUSH_KEY\"," +
	    "\"notification\" : {" +
	      "\"body\" : \"sendMsg\"," +
	      "\"title\" : \"Event Alarm\" }}";
	
	public void sendFCM(String sendMsg) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		
		try {
			HttpPost httpPost = new HttpPost(CALL_URL);
			httpPost.setHeader("Authorization", SERVER_KEY);;
			httpPost.setHeader("Content-Type", "application/json");
			
			String requestParam = "";
			StringEntity entity = null;
			
			for (int inx = 0, len = pushKeyList.size(); inx < len; inx++) {
				requestParam = param.replaceAll("sendMsg", sendMsg).replaceAll("PUSH_KEY", pushKeyList.get(inx));
				
				entity = new StringEntity(requestParam, ContentType.create("application/json", "UTF-8"));
				httpPost.setEntity(entity);
				
				response = httpclient.execute(httpPost);

				printReturnMsg(response);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (response != null) {
				try {
					response.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				httpclient.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void printReturnMsg(CloseableHttpResponse httpResponse) throws UnsupportedOperationException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = reader.readLine()) != null) {
			response.append(inputLine);
		}
		reader.close();
		
		System.out.println(response.toString());
	}
}
