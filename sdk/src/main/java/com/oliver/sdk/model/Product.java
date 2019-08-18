package com.oliver.sdk.model;

import java.util.List;

/**
 * author : Oliver
 * date   : 2019/8/6
 * desc   : 设备上的不同产品
 */

public class Product {

    /**
     * productId : 1
     * name : 默认项目
     * current:true,
     * policy:common
     * extra:每个字段的值表示的意思具体见实体类
     * description : 为公司串口测试缩写Demo
     * uart : [{"path":"/dev/ttyS1","baudrate":"115200"},{"path":"/dev/ttyS2","baudrate":"115200"},{"path":"/dev/ttyS3","baudrate":"115200"},{"path":"/dev/ttyS4","baudrate":"115200"}]
     */

    private int productId;
    private String name;
    private String policy; // 这个字段一定要有，SerialManager内部会根据这个字段进行反射，获取policy
    private boolean current;// 当前板子上运行的是否是该产品
    private String extra;
    private String description;
    private List<Uart> uart;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra == null ? "" : extra;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String common) {
        this.policy = common == null ? "" : common;
    }

    public List<Uart> getUart() {
        return uart;
    }

    public void setUart(List<Uart> serial) {
        this.uart = serial;
    }

    public boolean getCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", current='" + current + '\'' +
                ", description='" + description + '\'' +
                ", serial=" + uart +
                '}';
    }
}
