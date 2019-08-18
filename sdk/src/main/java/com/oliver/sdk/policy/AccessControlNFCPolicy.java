package com.oliver.sdk.policy;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.oliver.sdk.model.Uart;
import com.oliver.sdk.service.SerialService;
import com.oliver.sdk.util.ByteUtils;


/**
 * author : Oliver
 * date   : 2019/8/18
 * desc   :
 */

public class AccessControlNFCPolicy extends AbsPolicy {


    public static final int WHAT_FIND_CARD = 0x01;

    private Handler mHandler;
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_FIND_CARD:
                    if (msg.obj instanceof SerialService.SerialBinder) {
                        SerialService.SerialBinder binder = (SerialService.SerialBinder) msg.obj;
                        binder.sendCommand(TTYS3);
                    }
                    break;
            }
            mHandler.sendMessageDelayed(msg, 200);
            return false;
        }
    };

    public AccessControlNFCPolicy() {
        mHandler = new Handler(mCallback);
        if (Looper.myLooper() == null) {
            Looper.prepare();
            Looper.loop();
        }
        byte[] bytes = ByteUtils.string2Hex("JSC00041212");
        mUart1Bytes = bytes;
        mUart2Bytes = bytes;
        mUart3Bytes = bytes;
        mUart4Bytes = bytes;
    }

    @Override
    public void onStart(Object obj) {
        super.onStart(obj);
        Message message = mHandler.obtainMessage();
        message.what = WHAT_FIND_CARD;
        message.obj = obj;
        mHandler.sendMessageDelayed(message, 100);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
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
}
