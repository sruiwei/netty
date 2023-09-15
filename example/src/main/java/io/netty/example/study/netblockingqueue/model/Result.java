package io.netty.example.study.netblockingqueue.model;

import java.io.Serializable;

/**
 * @author shangruiwei
 * @date 2023/9/12 21:57
 */
public class Result implements Serializable {

    private String messageId;

    private String code;
    private String desc;
    private Shop shop;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public static Result success(String messageId, String desc) {
        return success(messageId, desc, null);
    }

    public static Result success(String messageId, String desc, Shop shop) {
        Result result = new Result();
        result.setMessageId(messageId);
        result.setCode("200");
        result.setDesc(desc);
        result.setShop(shop);
        return result;
    }

    public static Result fail(String messageId, String code, String desc) {
        Result result = new Result();
        result.setMessageId(messageId);
        result.setCode(code);
        result.setDesc(desc);
        return result;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
