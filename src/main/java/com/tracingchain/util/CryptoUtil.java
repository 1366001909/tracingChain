package com.tracingchain.util;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


/**
 * 加密工具类
 * 
 * @author aaron.rao
 *
 */
public class CryptoUtil {
	private CryptoUtil() {
	}

	public static String SHA256(String str) {
		MessageDigest messageDigest;
		String encodeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes("UTF-8"));
			encodeStr = byte2Hex(messageDigest.digest());
		} catch (Exception e) {
			System.out.println("getSHA256 is error" + e.getMessage());
		}
		return encodeStr;
	}

	public static String MD5(String input) {
		try{
			MessageDigest md = MessageDigest.getInstance("md5");
			byte[] res = md.digest(input.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b:res
				 ) {
				int num = b & 0xff;
				String str = Integer.toHexString(num);
				if(str.length() == 1){
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		}catch (NoSuchAlgorithmException e){
			e.printStackTrace();
			return "";
		}
	}

	public static String UUID() {
		return UUID.randomUUID().toString().replaceAll("\\-", "");
	}

	private static String byte2Hex(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		String temp;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) {
				builder.append("0");
			}
			builder.append(temp);
		}
		return builder.toString();
	}

}
