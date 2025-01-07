package org.example.utils;

public class HexStringUtils {

    public static byte[] hexTobytes(String hex) {
        if (hex.isEmpty()) {
            return null;
        } else {
            byte[] result = new byte[hex.length() / 2];
            int j = 0;
            for(int i = 0; i < hex.length(); i+=2) {
                result[j++] = (byte)Integer.parseInt(hex.substring(i,i+2), 16);
            }
            return result;
        }
    }
}
