package com.oliver.sdk.listener.cmd;

/**
 * author : Oliver
 * date   : 2019/8/18
 * desc   :
 */

public  interface IOnSendCommandListener extends IOnCommandListener {
    void onSendCommand(String dev,String command);
}
