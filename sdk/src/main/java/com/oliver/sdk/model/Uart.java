package com.oliver.sdk.model;

/**
 * author : Oliver
 * date   : 2019/8/6
 * desc   : 设备下的产品所用的串口
 */

public class Uart {

    public static final int RULE_ORIGINAL = 0x01;  // 打印从串口读取到的byte[]
    public static final int RULE_HEX = 0x02;       // 打印从串口读取到的byte[]的16进制表示法
    public static final int RULE_ASCII = 0x03;     // 打印从串口读取到的byte[]的ASCII码字符串

    /**
     * "path": "/dev/ttyS1",
     * "baudrate": "115200",
     * "formatRule": 2,
     * "sendCommandCallback": false,
     * "receiveCommandCallback": false
     */

    private String path;
    private String baudrate;
    private boolean sendCommandCallback;
    private boolean receiveCommandCallback;
    /**
     * 假设 sendCommandCallback 或 receiveCommandCallback 为 true，表示要打印发送的指令或接收到的指令，
     * 该字段表示打印的格式,具体见{@link Uart#RULE_ORIGINAL}、{@link Uart#RULE_HEX}和{@link Uart#RULE_ASCII}
     */
    private int formatRule;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFormatRule() {
        return formatRule;
    }

    public void setFormatRule(int formatRule) {
        this.formatRule = formatRule;
    }

    public String getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(String baudrate) {
        this.baudrate = baudrate;
    }

    public boolean getSendCommandCallback() {
        return sendCommandCallback;
    }

    public void setSendCommandCallback(boolean sendCommandCallback) {
        this.sendCommandCallback = sendCommandCallback;
    }

    public boolean getReceiveCommandCallback() {
        return receiveCommandCallback;
    }

    public void setReceiveCommandCallback(boolean receiveCommandCallback) {
        this.receiveCommandCallback = receiveCommandCallback;
    }

    @Override
    public String toString() {
        return "Uart{" +
                "path='" + path + '\'' +
                ", baudrate='" + baudrate + '\'' +
                '}';
    }
}
