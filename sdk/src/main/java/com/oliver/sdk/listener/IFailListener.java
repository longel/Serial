package com.oliver.sdk.listener;

/**
 * author : Oliver
 * date   : 2019/8/6
 * desc   :
 */

public interface IFailListener extends IListener{

    void onFail(Throwable throwable);
}
