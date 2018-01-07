package com.jh.eventalarm;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class StartEventAlarmService {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		SelectEventInfo sei = new SelectEventInfo();
		sei.getEventInfo();
		
		/* 테스트 코드*/
//		SendPush sp = new SendPush();
//		sp.setPushKeyList(sei.getPushKeyList());
//		sp.sendFCM("Test");
	}
}