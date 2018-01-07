package com.test;

public class AESTest {

	public static void main(String[] args) {
		try {
			byte [] a = AESUtils.AES_Encode("75712", "정진호".getBytes());
			
			System.out.println(new String(a));
			
			
			byte [] b = AESUtils.AES_Decode("75712", a);
			
			System.out.println(new String(b));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
