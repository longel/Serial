package com.oliver.serial.util;

import android.text.TextUtils;

import com.oliver.sdk.constant.Constants;

/**
 * author : Oliver
 * date   : 2019/8/18
 * desc   :
 */

public class RuleUtils {

    public static String formatCommand(String dev, String command, boolean isSend) {
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(Constants.SERIAL_PORT_MAP.get(dev))) {
            builder.append("串口");
        } else {
            builder.append(Constants.SERIAL_PORT_MAP.get(dev));
        }
        if (isSend) {
            builder.append("发送指令：");
        } else {
            builder.append("接收指令：");
        }
        builder.append(command);
        return builder.toString();
    }
}
