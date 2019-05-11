package com.yihe.crawler;

import java.util.Date;

public class Item {
    private String md5;
    private String caption;
    private String status;
    private Date modifyTime;

    public Item(String md5, String caption, String status, Date modifyTime) {
        this.md5 = md5;
        this.caption = caption;
        this.status = status;
        this.modifyTime = modifyTime;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

}