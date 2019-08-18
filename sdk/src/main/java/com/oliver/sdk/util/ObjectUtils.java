package com.oliver.sdk.util;

/**
 * author : Oliver
 * date   : 2019/8/18
 * desc   :
 */

public class ObjectUtils {

    public static void assertNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException("obj is null!!!" );
        }
    }
}
