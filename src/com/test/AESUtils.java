package com.test;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    private final static String secretKey = "12345678901234567890123456789012"; //32bit

    //암호화
    public static byte[] AES_Encode(String userId, byte[] targetData) throws  NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        String newKeyDate = userId + secretKey.substring(userId.length(), secretKey.length());
    	//String newKeyDate = secretKey;
        String IV = newKeyDate.substring(8, 24);
        
        //byte [] IV = new byte [] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        
        byte [] aa = IV.getBytes();
        System.out.println(aa);
        
        byte[] keyData = newKeyDate.getBytes();
/*        byte[] keyData = secretKey.getBytes();
        IV = secretKey.substring(0,16);*/
        
        System.out.println("newKeyDate : " + newKeyDate);
        System.out.println("IV : " + IV);

        SecretKey secureKey = new SecretKeySpec(keyData, "AES");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));
        //  org.apache.commons.codec.binary.Base64 호환을 버전 사용
        
        byte[] encrypted = c.doFinal(targetData);
        
        return Base64.getEncoder().encode(encrypted);

        // NO_WRAP 옵션을 지원한느 BASE64 버전으로 사용
        /*byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);*/
    }
    //복호화
    public static byte[] AES_Decode(String userId, byte[] targetData) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        String newKeyDate = userId + secretKey.substring(userId.length(), secretKey.length());
        //String newKeyDate = secretKey;
        String IV = newKeyDate.substring(8, 24);
        byte[] keyData = newKeyDate.getBytes();
        
/*        byte[] keyData = secretKey.getBytes();
        IV = secretKey.substring(0,16);*/
        
        SecretKey secureKey = new SecretKeySpec(keyData, "AES");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes("UTF-8")));

        //  org.apache.commons.codec.binary.Base64 호환을 버전 사용
        byte[] byteStr = Base64.getDecoder().decode(targetData);
        return c.doFinal(byteStr);
        //return new String(c.doFinal(byteStr),"UTF-8");
        // NO_WRAP 옵션을 지원한느 BASE64 버전으로 사용
        /*byte[] byteStr = Base64.decode(str.getBytes("utf-8"), Base64.NO_WRAP);
        return new String(c.doFinal(byteStr),"UTF-8");*/
    }

}
