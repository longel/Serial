package com.oliver.sdk.listener.cmd;


/**
 * author : Oliver
 * date   : 2019/8/18
 * desc   :
 */

public interface IOnReceiveCommandListener extends IOnCommandListener {

    void onReceiveCommand(String dev,String command);
}
