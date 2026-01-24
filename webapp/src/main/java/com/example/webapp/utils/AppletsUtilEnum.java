package com.example.webapp.utils;


public enum AppletsUtilEnum {
    INSTANCE;
    private AppletsUtil instance;
    public AppletsUtil getInstance() {
        if(instance == null){
            instance = new AppletsUtil();
        }
        return instance;
    }
}
