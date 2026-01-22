package com.example.webapp.third.sms.yunpian;

import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.SmsSingleSend;
import com.zhihuiedu.framework.result.CodeEnum;
import com.zhihuiedu.framework.result.Result;

import java.io.IOException;
import java.util.Map;

/**
 * 系统自带，验证码防轰炸规则：
 * 1，同一手机号同一个验证码模板，每30秒只能获取一条。
 * 如果触发本规则，接口将返回“错误代码33或53”，汇总信息在"失败请求统计页面"。
 *
 * 2，同一个手机号验证码类内容，每小时最多能获取3条。
 * 触发本规则，接口会返回错误代码22或53，如果出现这个返回码，您需要在前台给用户做相应提示。
 *
 * 3，同一个手机号验证码类内容，每天最多能获取到10条。
 * 触发本规则，接口会返回错误代码17或53，如果出现这个返回码，您需要在前台给用户做相应提示。
 *
 * 需自主开发：验证码有效时间设置为5分钟
 *
 * 2、3的验证规则：
 * 错误2提示：验证码获取过于频繁，请1小时后重试
 * 错误3提示：今日验证码次数已用完，请明日重试
 *
 * code = 0: 正确返回。可以从 api 返回的对应字段中取数据。
 * code > 0: 调用 API 时发生错误，需要开发者进行相应的处理。
 * -50 < code < 0: 权限验证失败，需要开发者进行相应的处理。
 * code = -50: 客户端或服务器内部异常
 */
public class SmsYunpianUtil {

    public static final String REPLACE_CODE = "#code#";

    /**
     * 只适用科乐园发送消息
     * @param phone
     * @param code
     * @return
     * @throws IOException
     */
    public static Result  send(String phone, String code) throws IOException {
        return send(phone,code,SmsTempletEnum.TEMPLET_KELORPARK.getTempletId());
    }

    /**
     * 发消息通用接口
     * @param phone
     * @param code
     * @param templetId SmsTempletEnum
     * @return
     * @throws IOException
     */
    public static Result send(String phone, String code, Integer templetId) throws IOException {
        SmsTempletEnum smsTempletEnum =SmsTempletEnum.getSmsTempletEnumByTempletId(templetId);
        YunpianClient client= SmsYunpianUtilEnum.INSTANCE.getInstance(smsTempletEnum);
        String text = smsTempletEnum.getTemplet().replace(REPLACE_CODE,code);
        //发送短信API
        Map<String, String> param = client.newParam(2);
        param.put(YunpianClient.MOBILE, phone);
        param.put(YunpianClient.TEXT, text);
        com.yunpian.sdk.model.Result <SmsSingleSend> result = client.sms().single_send(param);
        Result rs = Result.fail();
        rs.setMessage(result.getMsg()+","+result.getDetail());
        rs.setCode(CodeEnum.SUCCESS.getValue());
        if(!CodeEnum.SUCCESS.getValue().equals(result.getCode())){
            rs.setCode(CodeEnum.FAILED.getValue());
            if(result.getCode().equals(22) || result.getCode().equals(33)){
                rs.setMessage("验证码获取过于频繁，请1小时后重试");
            }else if(result.getCode().equals(17)){
                rs.setMessage("今日验证码次数已用完，请明日重试");
            }
        }
        return rs;
    }


}
