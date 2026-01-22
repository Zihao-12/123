package com.example.webapp.utils.http;

/**
 * 平台标记枚举
 */
public enum ChunZhenIPTypeEnum {
    /**
     * 纯真企业版：https://market.aliyun.com/products/57126001/cmapi00053387.html#sku=yuncode4738700002
     * 纯真社区版（个人调试）：https://market.aliyun.com/products/57002002/cmapi00046276.html?spm=5176.product-detail.sidebar.1.74411f8dz3eQ45&scm=20140722.C_cmapi00046276.P_146.MO_732-ST_4769-V_1-ID_cmapi00046276-OR_rec#sku=yuncode4027600003
     * 不同产品域名不一样
     *
     */
    QI_YE ("https://cz88geoaliyun.cz88.net","/search/ip/geo",
            "204069476","2ku5qttDSaDqac9nA7vkGk3sgKxQxbL0","6f24bcae308d4ac993899b8d9bc9d96a");

    private String host;
    private String path;
    private String appKey;
    private String appSecret;
    private String appCode;

    ChunZhenIPTypeEnum(String host,String path,String appKey,String appSecret,String appCode){
        this.host=host;
        this.path=path;
        this.appKey=appKey;
        this.appSecret=appSecret;
        this.appCode=appCode;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getAppCode() {
        return appCode;
    }
}