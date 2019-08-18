package com.oliver.sdk.policy;

import com.oliver.sdk.model.Uart;

/**
 * author : Oliver
 * date   : 2019/8/18
 * desc   :
 */

public class CommonPolicy extends AbsPolicy {


    public CommonPolicy() {
        mUart1Bytes = new byte[]{0x01, 0x01, 0x01, 0x01};
        mUart2Bytes = new byte[]{0x01, 0x01, 0x01, 0x01};
        mUart3Bytes = new byte[]{0x01, 0x01, 0x01, 0x01};
        mUart4Bytes = new byte[]{0x01, 0x01, 0x01, 0x01};
    }

    @Override
    public String parseCommand(Uart uart, byte[] bytes, int length) {
        return super.parseCommand(uart, bytes, length);

    }

    @Override
    public String parseUart_1(Uart uart, byte[] bytes, int length) {
        return super.parseUart_1(uart, bytes, length);
    }

    @Override
    public String parseUart_2(Uart uart, byte[] bytes, int length) {
        return super.parseUart_2(uart, bytes, length);
    }

    @Override
    public String parseUart_3(Uart uart, byte[] bytes, int length) {
        return super.parseUart_3(uart, bytes, length);
    }

    @Override
    public String parseUart_4(Uart uart, byte[] bytes, int length) {
        return super.parseUart_4(uart, bytes, length);
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
}
