package com.example.zeonit.nclc.response;

public class Notice {
    private int noticeId;
    private String type;
    private String description;
    private String upCommingDate;
    private boolean isValid;
    private boolean isExpire;

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpCommingDate() {
        return upCommingDate;
    }

    public void setUpCommingDate(String upCommingDate) {
        this.upCommingDate = upCommingDate;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isExpire() {
        return isExpire;
    }

    public void setExpire(boolean expire) {
        isExpire = expire;
    }
}
