package org.example.utils;

import java.util.Random;

public class RandomStringUtils {
    public static String CAPITAL = "ABCDEF";
    public static String LOWERCASE = "abcdef";
    public static String NUMBER = "0123456789";

    public static String getCapitalAndNumber(int length) {
        return getRamdonString(length, true, false, true);
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

        return getRamdonStringByBase(base, length);
    }

    public static String getRamdonStringByBase(String base,int length){
        if(base.isEmpty()) return null;

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }

     public static String getRamdonDatapathId(){
        //16位的最大值7fff ffff ffff ffff;
         String smallerThan7 = "01234567";
         Random random = new Random();
         StringBuilder sb = new StringBuilder(16);
         sb.append(smallerThan7.charAt(random.nextInt(smallerThan7.length())));

         String base = (CAPITAL + NUMBER);
         for (int i = 1; i < 16; i++) {
             sb.append(base.charAt(random.nextInt(base.length())));
         }
         return sb.toString();
     }


     public static String getRamdonMacAddress(){
         Random random = new Random();

         byte[] macAddress = new byte[6];
         macAddress[0] = (byte) (random.nextInt(256) & (byte) 0xFE); // & 0xFE 确保最低位是 0

         for (int i = 1; i < 6; i++) {
             macAddress[i] = (byte) random.nextInt(256);
         }

         StringBuilder macAddressStr = new StringBuilder();
         for (int i = 0; i < macAddress.length; i++) {
             macAddressStr.append(String.format("%02X", macAddress[i]));
             if (i < macAddress.length - 1) {
                 macAddressStr.append(":");
             }
         }
         return macAddressStr.toString();
     }
}

