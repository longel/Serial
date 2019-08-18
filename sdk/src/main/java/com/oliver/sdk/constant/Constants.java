package com.oliver.sdk.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author : Oliver
 * date   : 2019/8/6
 * desc   :
 */

public class Constants {

    public static final Map<String, String> SERIAL_PORT_MAP = new HashMap<>();
    public static final List<String> SERIAL_PORT_PATH = new ArrayList<>();


    public static final String TTYS1 = "/dev/ttyS1";
    public static final String TTYS2 = "/dev/ttyS2";
    public static final String TTYS3 = "/dev/ttyS3";
    public static final String TTYS4 = "/dev/ttyS4";

    static {
        SERIAL_PORT_MAP.put(TTYS1, "串口1");
        SERIAL_PORT_MAP.put(TTYS2, "串口2");
        SERIAL_PORT_MAP.put(TTYS3, "串口3");
        SERIAL_PORT_MAP.put(TTYS4, "串口4");

        SERIAL_PORT_PATH.add(TTYS1);
        SERIAL_PORT_PATH.add(TTYS2);
        SERIAL_PORT_PATH.add(TTYS3);
        SERIAL_PORT_PATH.add(TTYS4);
    }


}
