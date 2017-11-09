package com.zhzj.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by l.shao on 2017/7/6.
 */
public class TokenProcessor {
    private static TokenProcessor instance = new TokenProcessor();

    private  static long previous;

    protected TokenProcessor() {
    }

    public static TokenProcessor getInstance() {
        return instance;
    }

    public static   synchronized String generateToken(String msg, boolean timeChange) {
        try {

            long current = System.currentTimeMillis();
            if (current == previous)                current++;
            previous = current;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(msg.getBytes());
            if (timeChange) {
                // byte now[] = (current+"").toString().getBytes();
                byte now[] = (new Long(current)).toString().getBytes();
                md.update(now);
            }
            return toHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static String toHex(byte buffer[]) {
        StringBuffer sb = new StringBuffer(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 15, 16));
        }

        return sb.toString();
    }

    public static void main(String[] args){

        String token =  TokenProcessor.generateToken("Vicky",true);

        System.err.println(token);

        String token2 =  TokenProcessor.generateToken("Vicky",false);

        System.err.println(token2);

    }
}
