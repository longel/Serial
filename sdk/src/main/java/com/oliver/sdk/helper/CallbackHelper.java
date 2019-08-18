package com.oliver.sdk.helper;

import com.oliver.sdk.listener.IFailListener;
import com.oliver.sdk.listener.IListener;
import com.oliver.sdk.listener.ISuccessListener;

import io.reactivex.annotations.Nullable;

/**
 * author : Oliver
 * date   : 2019/8/6
 * desc   :
 */

public class CallbackHelper {

    public static void onSuccess(IListener listener, @Nullable Object obj) {
        if (listener instanceof ISuccessListener) {
            ((ISuccessListener) listener).onSuccess(obj);
        }
    }

    public static void onFail(IListener listener, @Nullable Throwable throwable) {
        if (listener instanceof IFailListener) {
            ((IFailListener) listener).onFail(throwable);
        }
    }
}
