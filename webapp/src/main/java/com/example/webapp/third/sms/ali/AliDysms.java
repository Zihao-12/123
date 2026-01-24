package com.example.webapp.third.sms.ali;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.example.webapp.result.Result;
import com.example.webapp.third.AccessKeyIdSecretEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class AliDysms {

    public static final String OK = "OK";

    /**
     * 使用AK&SK初始化账号Client
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(AccessKeyIdSecretEnum.ALI_SMS.getAk())
                .setAccessKeySecret(AccessKeyIdSecretEnum.ALI_SMS.getAks());
        // 访问的域名
        config.endpoint = AccessKeyIdSecretEnum.ALI_SMS.getEp();
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * error code :
     *    isv.BUSINESS_LIMIT_CONTROL   业务限流  触发小时级流控Permits:5
     *         默认流控：使用同一个签名，对同一个手机号码发送短信验证码，支持1条/分钟，5条/小时 ，累计10条/天
     *    isv.DOMESTIC_NUMBER_NOT_SUPPORTED   国际/港澳台消息模板不支持发送境内号码
     *    isv.DENY_IP_RANGE  源IP地址所在的地区被禁用
     *    isv.DAY_LIMIT_CONTROL  触发日发送限额
     *    isv.OUT_OF_SERVICE   业务停机 余额不足
     *    isv.AMOUNT_NOT_ENOUGH 当前账户余额不足
     *    isv.MOBILE_NUMBER_ILLEGAL 非法手机号
     * @param phone
     * @param code
     * @return
     */
    public static Result send(String phone, String code, String templetCode) {
        try {
            if(StringUtils.isBlank(templetCode)){
                return Result.fail("验证码发送失败");
            }
            AlismsTempletEnum templetEnum = AlismsTempletEnum.getSmsTempletByTempletCode(templetCode);
            com.aliyun.dysmsapi20170525.Client client = createClient();
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(templetEnum.getSignName())
                    .setTemplateCode(templetEnum.getTempletCode())
                    .setTemplateParam("{\"code\":"+code+"}");
            log.info("ali-send-code,mobile:{},random:{}",phone,code);
            SendSmsResponse response = client.sendSms(sendSmsRequest);
            if(OK.equals(response.getBody().getCode())){
                return Result.ok(response.getBody().getCode());
            }else {
                log.error("code:{},message:{}",response.getBody().getCode(),response.getBody().getMessage());
                return Result.fail("今日验证码已超过最大限制！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(0);
    }


}
