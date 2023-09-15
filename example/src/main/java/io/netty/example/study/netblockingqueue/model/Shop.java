package io.netty.example.study.netblockingqueue.model;

import java.io.Serializable;

/**
 * @author shangruiwei
 * @date 2023/9/11 21:46
 */
public class Shop implements Serializable {

    private Long id;
    private String shopName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
