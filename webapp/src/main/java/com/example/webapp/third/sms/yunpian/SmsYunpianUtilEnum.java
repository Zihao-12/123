package com.example.webapp.third.sms.yunpian;

import com.yunpian.sdk.YunpianClient;

/**
 * @author gehaisong
 */
public enum SmsYunpianUtilEnum {
    INSTANCE;
    private YunpianClient instance;
    public YunpianClient getInstance(SmsTempletEnum smsTempletEnum) {
        if(instance == null){
            instance = new YunpianClient(smsTempletEnum.getApiKey()).init();
        }
        return instance;
    }
}
