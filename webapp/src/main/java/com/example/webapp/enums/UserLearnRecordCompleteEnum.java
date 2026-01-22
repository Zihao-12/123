package com.example.webapp.enums;


public enum UserLearnRecordCompleteEnum {
    COMPLETE_NOT_START (-1,"未开始(没听课)"),
    COMPLETE_NO (0,"未完成"),
    COMPLETE_YES (1,"已完成");

    private Integer status;
    private String name;

    UserLearnRecordCompleteEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}