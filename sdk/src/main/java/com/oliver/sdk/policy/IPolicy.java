package com.oliver.sdk.policy;

import android.os.IBinder;

import com.oliver.sdk.model.Product;
import com.oliver.sdk.model.Uart;

/**
 * author : Oliver
 * date   : 2019/8/18
 * desc   : {@link com.oliver.sdk.service.SerialService#initProductByReflection(Product)} 获取处理策略
 * <p>
 * <pre>
 *     注意：Policy的子类的命名是有格式要求的，在配置文件assets/xxx/configuration中的product字段中，有个
 *     字段为policy，这个字段的值就是你对应的Policy的名字。例如：
 *
 *     "productId": 1,
 *      "name": "默认项目",
 *      "policy":"test",
 *      "current": true,
 *      "description": "为公司串口测试缩写Demo"
 *      ......
 *
 *      那么你的Policy对应的实现类的类名就是：TestPolicy
 * </pre>
 */

public interface IPolicy {

    String TTYS1 = "/dev/ttyS1";
    String TTYS2 = "/dev/ttyS2";
    String TTYS3 = "/dev/ttyS3";
    String TTYS4 = "/dev/ttyS4";

    void sendCommand(IBinder binder);

    void sendCommand(IBinder binder, String dev, byte[] bytes);

    void sendCommand(IBinder binder, String dev, String ascii);

    void onStart(Object obj);

    void onDestroy();

    byte[] get(String dev);

    byte[] getUart_1();

    byte[] getUart_2();

    byte[] getUart_3();

    byte[] getUart_4();

    String parseCommand(Uart uart, byte[] bytes, int length);

    String parseUart_1(Uart uart, byte[] bytes, int length);

    String parseUart_2(Uart uart, byte[] bytes, int length);

    String parseUart_3(Uart uart, byte[] bytes, int length);

    String parseUart_4(Uart uart, byte[] bytes, int length);

}
