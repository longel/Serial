package com.oliver.sdk.policy;

import android.os.IBinder;

import com.oliver.sdk.model.Uart;
import com.oliver.sdk.util.ByteUtils;

import java.util.Arrays;

/**
 * author : Oliver
 * date   : 2019/8/18
 * desc   :
 */

public abstract class AbsPolicy implements IPolicy {

    protected byte[] mUart1Bytes;
    protected byte[] mUart2Bytes;
    protected byte[] mUart3Bytes;
    protected byte[] mUart4Bytes;

    public AbsPolicy() {
    }

    @Override
    public void sendCommand(IBinder binder) {

    }

    @Override
    public void sendCommand(IBinder binder, String dev, byte[] bytes) {

    }

    @Override
    public void sendCommand(IBinder binder, String dev, String ascii) {

    }

    @Override
    public byte[] get(String dev) {
        byte[] targetBytes = null;
        switch (dev) {
            case TTYS1:
                targetBytes = getUart_1();
                break;
            case TTYS2:
                targetBytes = getUart_2();
                break;
            case TTYS3:
                targetBytes = getUart_3();
                break;
            case TTYS4:
            default:
                targetBytes = getUart_4();
        }
        return targetBytes;
    }

    @Override
    public void onStart(Object obj) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public byte[] getUart_1() {
        return mUart1Bytes;
    }

    @Override
    public byte[] getUart_2() {
        return mUart2Bytes;
    }

    @Override
    public byte[] getUart_3() {
        return mUart3Bytes;
    }

    @Override
    public byte[] getUart_4() {
        return mUart4Bytes;
    }

    @Override
    public String parseCommand(Uart uart, byte[] bytes, int length) {
        String command = null;
        switch (uart.getPath()) {
            case TTYS1:
                command = parseUart_1(uart, bytes, length);
                break;
            case TTYS2:
                command = parseUart_2(uart, bytes, length);
                break;
            case TTYS3:
                command = parseUart_3(uart, bytes, length);
                break;
            case TTYS4:
            default:
                command = parseUart_4(uart, bytes, length);
        }
        return command;
    }

    @Override
    public String parseUart_1(Uart uart, byte[] bytes, int length) {
        return commonParse(uart, bytes, length);
    }

    @Override
    public String parseUart_2(Uart uart, byte[] bytes, int length) {
        return commonParse(uart, bytes, length);
    }

    @Override
    public String parseUart_3(Uart uart, byte[] bytes, int length) {
        return commonParse(uart, bytes, length);
    }

    @Override
    public String parseUart_4(Uart uart, byte[] bytes, int length) {
        return commonParse(uart, bytes, length);
    }


    protected String commonParse(Uart uart, byte[] bytes, int length) {
        String result = null;
        byte[] data = new byte[length];
        System.arraycopy(bytes, 0, data, length, length);
        if (uart.getFormatRule() == Uart.RULE_ASCII) {
            result = ByteUtils.bytes2ASCII(data);
        } else if (uart.getFormatRule() == Uart.RULE_HEX) {
            result = ByteUtils.bytes2HEX(data);
        } else {
            result = Arrays.toString(data);
        }
        return result;
    }


}
