package com.oliver.sdk.model;

/**
 * author : Oliver
 * date   : 2019/8/6
 * desc   :
 */

public class SerialConfig {
    /**
     * channel.device : {
     *      "model":"all",
     *      "description":"在这里对设备进行描述",
     *      "product":[
     *          {
     *              "productId":1,
     *              "name":"默认项目",
     *              "description":"为公司串口测试缩写Demo",
     *              "serial":[
     *                  {
     *                      "path":"/dev/ttyS1",
     *                      "baudrate":"115200"
     *                  },
     *                  {
     *                      "path":"/dev/ttyS2",
     *                      "baudrate":"115200"
     *                  },
     *                  {
     *                      "path":"/dev/ttyS3",
     *                      "baudrate":"115200"
     *                  },
     *                  {
     *                      "path":"/dev/ttyS4",
     *                      "baudrate":"115200"
     *                  }
     *              ]
     *          }
     *       ]
     *    }
     * }
     *
     */

    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "SerialConfig{" +
                "device=" + device +
                '}';
    }
}
