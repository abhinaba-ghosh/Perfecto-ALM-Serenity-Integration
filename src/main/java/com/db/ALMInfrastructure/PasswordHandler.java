package com.db.ALMInfrastructure;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class PasswordHandler {

	private static final String ALGO="AES";
	private static final byte[] keyValue=new byte[] {
			'T','h','e','B','e','S','t','S','e','c','r','e','t','K','e','y'
	};
	
	
	public static String encrypt(String data) throws Exception{
		Key key=generateKey();
		Cipher c =Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal=c.doFinal(data.getBytes());
		String encryptedValue=new BASE64Encoder().encode(encVal);
		return encryptedValue;
	}
	
	public static String decrypt(String encrypteddata) throws Exception{
		Key key=generateKey();
		Cipher c =Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue=new BASE64Decoder().decodeBuffer(encrypteddata);
		byte[] decValue=c.doFinal(decordedValue);
		String decryptedValue=new String(decValue);
		return decryptedValue;
	}

	private static Key generateKey() {
		// TODO Auto-generated method stub
		Key key=new SecretKeySpec(keyValue, ALGO);
		return key;
	}
}
