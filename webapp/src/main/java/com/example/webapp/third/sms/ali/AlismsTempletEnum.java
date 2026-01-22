package com.example.webapp.third.sms.ali;

public enum AlismsTempletEnum {
    /**
     *  签名指：【神经猿学院】
     */
    TPL_VERIFY_CODE ("布局未来","SMS_242695665","验证码"),
    TPL_NOTIFY("新东方启蒙教育","SMS_215337940","通知");

    private String signName;
    private String templetCode;
    private String desc;

    AlismsTempletEnum(String signName, String templetCode, String desc){
        this.signName=signName;
        this.templetCode=templetCode;
        this.desc =desc;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getTempletCode() {
        return templetCode;
    }

    public void setTempletCode(String templetCode) {
        this.templetCode = templetCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static AlismsTempletEnum getSmsTempletByTempletCode(String templetCode) {
        AlismsTempletEnum[] values = AlismsTempletEnum.values();
        for (AlismsTempletEnum smsTempletEnum : values) {
            if(smsTempletEnum.templetCode.equals(templetCode)){
                return smsTempletEnum;
            }
        }
        return TPL_VERIFY_CODE;
    }
}