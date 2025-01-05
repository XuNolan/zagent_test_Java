package org.example.utils;

import java.util.Random;

public class RandomStringUtils {
    public static String CAPITAL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static String NUMBER = "0123456789";

    public static String getCapitalAndNumber(int length) {
        return getRamdonString(length, true, false, false);
    }
    public static String getNumber(int length) {
        return getRamdonString(length, false, false, true);
    }

    public static String getRamdonString(int length, boolean needCapital, boolean needLowerCase, boolean needNumber) {
        String base = "";
        if(needCapital){
            base += CAPITAL;
        }
        if(needLowerCase){
            base += LOWERCASE;
        }
        if(needNumber){
            base += NUMBER;
        }
        if(base.isEmpty()) return null;

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }
}
