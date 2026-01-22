package com.example.webapp.third;


/**
 *   AccessKey ID 和 AccessKey Secret 是您访问阿里云 API 的密钥，具有该账户完全的权限，请您妥善保管
 *   AccessKey 在线时间越长，泄露风险越高。建议创建新 AccessKey 替代有风险的项
 */
public enum AccessKeyIdSecretEnum {
    /**
     * ak:  AccessKey ID      或 微信 appId
     * aks: AccessKey Secret  或 微信 appSecret
     * ep:  END_POINT:点播域名
     */
    ALI_VIDEO("1","1","cn-beijing","vod.cn-beijing.aliyuncs.com",""),
    ALI_OSS  ("1","1","cn-beijing","oss-cn-beijing.aliyuncs.com","kidsplusclub"),
    ALI_SMS  ("1","1","","dysmsapi.aliyuncs.com",""),

    WX_APP  ("1","1","","","");

    private String ak;
    private String aks;
    private String ep;
    private String bucketName;
    private String regionId;

    AccessKeyIdSecretEnum(String ak, String aks, String regionId, String ep, String bucketName) {
        this.ak = ak;
        this.aks = aks;
        this.regionId = regionId;
        this.ep = ep;
        this.bucketName = bucketName;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getAks() {
        return aks;
    }

    public void setAks(String aks) {
        this.aks = aks;
    }

    public String getEp() {
        return ep;
    }

    public void setEp(String ep) {
        this.ep = ep;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }
}
