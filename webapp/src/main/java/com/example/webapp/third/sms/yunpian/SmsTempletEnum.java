package com.example.webapp.third.sms.yunpian;

public enum SmsTempletEnum {
    /**
     *  签名指：【神经猿学院】
     */
    TEMPLET_YLW ("寓乐湾",3605130,"【神经猿学院】验证码：#code#（此验证码5分钟内有效，请勿泄露给他人）。如非本人操作，请忽略此信息。",""),
    TEMPLET_KELORPARK("科乐园",4298482,"【科乐园】验证码：#code#（此验证码5分钟内有效，请勿泄露给他人）。如非本人操作，请忽略此信息。","");

    private String name;
    private Integer templetId;
    private String templet;
    private String apiKey;

    SmsTempletEnum(String name,Integer templetId,String templet,String apiKey){
        this.name=name;
        this.templetId=templetId;
        this.templet =templet;
        this.apiKey=apiKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTempletId() {
        return templetId;
    }

    public void setTempletId(Integer templetId) {
        this.templetId = templetId;
    }

    public String getTemplet() {
        return templet;
    }

    public void setTemplet(String templet) {
        this.templet = templet;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public static SmsTempletEnum getSmsTempletEnumByTempletId(Integer templetId) {
        SmsTempletEnum[] values = SmsTempletEnum.values();
        for (SmsTempletEnum smsTempletEnum : values) {
            if(smsTempletEnum.templetId.equals(templetId)){
                return smsTempletEnum;
            }
        }
        return TEMPLET_KELORPARK;
    }
}