package com.oliver.sdk.util;

/**
 * author : Oliver
 * date   : 2018/9/26
 * desc   :
 */

public class ASCIIUtil {


    public static String ascii2String(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return sb.toString();
    }
}
