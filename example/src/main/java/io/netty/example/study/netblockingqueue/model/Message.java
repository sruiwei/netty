package io.netty.example.study.netblockingqueue.model;

import java.io.Serializable;

/**
 * @author shangruiwei
 * @date 2023/9/12 21:52
 */
public class Message implements Serializable {

    private String messageId;

    /**
     * 操作类型（1：offer，2：poll）
     */
    private Integer optType;
    private Shop shop;

    public Integer getOptType() {
        return optType;
    }

    public void setOptType(Integer optType) {
        this.optType = optType;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
