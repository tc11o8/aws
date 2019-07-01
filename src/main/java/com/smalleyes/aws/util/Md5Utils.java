package com.smalleyes.aws.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @desc
 * @Date 2018/4/20 15:13
 * @Author xupengcheng [xupengcheng@qq.com]
 */
public class Md5Utils {


    public Md5Utils() {
    }

    public static String getMd5(String key) {
        return toHexString(md5(key));
    }

    static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var1) {
            throw new RuntimeException(var1);
        }
    }

    public static String toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < b.length; ++i) {
            sb.append("0123456789abcdef".charAt(b[i] >>> 4 & 15));
            sb.append("0123456789abcdef".charAt(b[i] & 15));
        }

        return sb.toString();
    }

    public static byte[] toByteArray(String s) {
        byte[] buf = new byte[s.length() / 2];
        int j = 0;

        for (int i = 0; i < buf.length; ++i) {
            buf[i] = (byte) (Character.digit(s.charAt(j++), 16) << 4 | Character.digit(s.charAt(j++), 16));
        }

        return buf;
    }

    public static byte[] md5(byte[] data) {
        return getDigest().digest(data);
    }

    public static byte[] md5(String data) {
        return md5(data.getBytes());
    }

    public static String md5Hex(byte[] data) {
        return toHexString(md5(data));
    }

    public static String md5Hex(String data) {
        return toHexString(md5(data));
    }
    
    public static void main(String[] args) {
		
    	
    	  int SHARED_SHIFT   = 16;
          int SHARED_UNIT    = (1 << SHARED_SHIFT);
          int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
          int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;
          
          System.out.println("d");
         
         
	}
}
