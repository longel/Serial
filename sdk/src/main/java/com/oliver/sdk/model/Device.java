package com.oliver.sdk.model;

import java.util.List;

/**
 * author : Oliver
 * date   : 2019/8/6
 * desc   : 设备抽象（板子型号）
 */

public class Device {

    /**
     * model : all
     * description : 在这里对设备进行描述
     * product : [{"productId":1,"name":"默认项目","description":"为公司串口测试缩写Demo","serial":[{"path":"/dev/ttyS1","baudrate":"115200"},{"path":"/dev/ttyS2","baudrate":"115200"},{"path":"/dev/ttyS3","baudrate":"115200"},{"path":"/dev/ttyS4","baudrate":"115200"}]}]
     */

    private String model;
    private String description;
    private List<Product> product;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }


    @Override
    public String toString() {
        return "Device{" +
                "model='" + model + '\'' +
                ", description='" + description + '\'' +
                ", product=" + product +
                '}';
    }
}
