package com.oliver.sdk.util;

import android.text.TextUtils;

import java.util.Locale;

/**
 * author : Oliver
 * date   : 2018/9/30
 * desc   :
 */

public class ByteUtils {

    public static final String PRE_HEX = "0x";
    public static final String PRE_BINARY = "0b";

    public static String bytes2HEX(byte[] data) {
        if (data == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(PRE_HEX);
            String hex = Integer.toHexString(b & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                sb.append(0);
            }
            sb.append(hex);
            sb.append(" ");
        }
        return sb.toString();
    }

    public static byte[] string2Hex(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return null;
        }
        return msg.getBytes();
    }

    public static int hex2Integer(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public static String hex2IntegerForJSC260V24(String hex) {
        return String.format(Locale.getDefault(), "%010d", Integer.parseInt(hex, 16));
    }


    public static String bytes2ASCII(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) b);
        }
        return sb.toString();
    }
}
