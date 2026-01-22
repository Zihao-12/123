package com.example.webapp.utils;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;
import com.zhihuiedu.common.redis.RedisKeyGenerator;
import com.zhihuiedu.common.redis.RedisUtils;
import com.zhihuiedu.framework.utils.SpringContextUtil;
import com.zhihuiedu.framework.utils.http.HttpUtil;
import com.zhihuiedu.thrid.AccessKeyIdSecretEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.codec.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;

/**
 * 封装小程序服务端API
 */
@Slf4j
@Component
public class AppletsUtil {
    public static final String GET_ACCESS_TOKEN = "GET_ACCESS_TOKEN";
    public static final Integer SUCESS_CODE =0;
    /**
     * 登陆 auth.code2Session  -- 服务端登陆接口调用
     * @param code   	登录时获取的 code
     *                  前端通过 wx.login 接口获得临时登录凭证 code 后传到 后端登陆接口， 开发者服务器调用此接口完成登录流程
     *                  grant_type: 授权类型，此处只需填写 authorization_code
     *                  token = jsonObject.getString("access_token");
     *                 Integer expires_in=jsonObject.getInteger("expires_in");
     * 1.开发者可以直接通过 wx.login + code2Session 获取到该用户 UnionID，无须用户授权。
     * 2.前端通过 wx.login 接口获得临时登录凭证 code 换取 用户唯一标识 OpenID 、
     *                    用户在微信开放平台帐号下的唯一标识UnionID（若当前小程序已绑定到微信开放平台帐号） 和
     *                    会话密钥 session_key
     * 3.会话密钥 session_key 是对用户数据进行 加密签名 的密钥。为了应用自身的数据安全，开发者服务器不应该把会话密钥下发到小程序，也不应该对外提供这个密钥。
     * 4.OpenID UnionID session_key 入库
     * 5。wx.login 调用时，用户的 session_key 可能会被更新而致使旧 session_key 失效（刷新机制存在最短周期，如果同一个用户短时间内多次调用 wx.login，并非每次调用都导致 session_key 刷新）。
     *                  开发者应该在明确需要重新登录时才调用 wx.login，及时通过 auth.code2Session 接口更新服务器存储的 session_key。
     * 6。微信不会把 session_key 的有效期告知开发者。我们会根据用户使用小程序的行为对 session_key 进行续期。用户越频繁使用小程序，session_key 有效期越长
     * @return
     */
    public static WXCode2SessionDTO code2Session(String code){
        String url="https://api.weixin.qq.com/sns/jscode2session?" +
                "appid="+ AccessKeyIdSecretEnum.WX_APP.getAk()+"&secret="+AccessKeyIdSecretEnum.WX_APP.getAks()+"&js_code="+code+"&grant_type=authorization_code";
        String at = HttpUtil.sendGet(url);
        WXCode2SessionDTO code2SessionDTO = JSON.parseObject(at,WXCode2SessionDTO.class);
        return code2SessionDTO;
    }

    public  String  getAccessToken(){
        RedisUtils redisUtils= SpringContextUtil.getContext().getBean(RedisUtils.class);
        String key = RedisKeyGenerator.getKey(AppletsUtil.class, GET_ACCESS_TOKEN);
        String token = (String) redisUtils.get(key);
        if(StringUtils.isBlank(token)){
            String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+AccessKeyIdSecretEnum.WX_APP.getAk()+"&secret="+AccessKeyIdSecretEnum.WX_APP.getAks();
            String at = HttpUtil.sendGet(url);
            token =  JSON.parseObject(at).getString("access_token");
            log.info("---------- 重新获取-access_token--------------");
            redisUtils.set(key,token,7000);
        }
        log.info("access_token：{}",token);
        return token;
    }

    /**
     * 获取小程序二维码
     * 45009	调用分钟频率受限(目前5000次/分钟，会调整)，如需大量小程序码，建议预生成
     * 41030	page 不合法（页面不存在或者小程序没有发布、根路径前加 /或者携带参数）
     * 40097	env_version 不合法
     * 生成二维码后 accessToken会失效，不建议C端用户调用
     * @param scene  参数 a=x
     * @param page
     * @return
     */
    public void getUnlimited(String scene, String page, HttpServletResponse response){
        InputStream inputStream =null;
        try {
            inputStream = AppletsUtilEnum.INSTANCE.getInstance().getUnlimited(scene,page);
            //获取响应输出流对象。
            OutputStream outputStream = response.getOutputStream();
            IOUtils.copy(inputStream,outputStream );
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("{}",e);
                }
                ;
            }
        }
    }

    public InputStream getUnlimited(String scene, String page) throws IOException {
        JsonObject jsonObject = new JsonObject();
        String token = getAccessToken();
        String url="https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+token;
        if(StringUtils.isNotBlank(page)){
            jsonObject.addProperty("page",page);
        }
        if(StringUtils.isNotBlank(scene)){
            jsonObject.addProperty("scene",scene);
        }
        InputStream inputStream = HttpUtil.getStreamByUrl(url,jsonObject.toString());
        return  inputStream;
    }

    /**
     * 获取用户电话信息
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     * @throws Exception
     */
    public static WXPhoneNumberDTO getPhoneNumber(String encryptedData, String sessionKey, String iv) throws Exception {
        WXPhoneNumberDTO phoneNumberDTO = null;
        String phone=  decryptForWeChatApplet(encryptedData, sessionKey, iv);
        if(StringUtils.isNotBlank(phone)){
            phoneNumberDTO = JSON.parseObject(phone,WXPhoneNumberDTO.class);
        }
        return phoneNumberDTO;
    }

    /**
     * 获取用户信息
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     * @throws Exception
     */
    public static WXUserInfoDTO getUserInfo(String encryptedData, String sessionKey, String iv) throws Exception {
        WXUserInfoDTO userInfoDTO = null;
        String userInfo=  decryptForWeChatApplet(encryptedData, sessionKey, iv);
        if(StringUtils.isNotBlank(userInfo)){
            userInfoDTO = JSON.parseObject(userInfo,WXUserInfoDTO.class);
        }
        return userInfoDTO;
    }

    public static boolean isSuccess(Integer code){
        return !(code!=null && !SUCESS_CODE.equals(code));
    }


    /**
     * 微信小程序 开放数据解密
     * AES解密（Base64）
     * @param encryptedData 已加密的数据 包括敏感数据在内的完整用户信息的加密数据
     * @param sessionKey    解密密钥
     * @param iv            IV偏移量
     * @return
     * @throws Exception
     */
    public static String decryptForWeChatApplet(String encryptedData, String sessionKey, String iv) {
        byte[] decryptBytes = Base64.decode(encryptedData);
        byte[] keyBytes = Base64.decode(sessionKey);
        byte[] ivBytes = Base64.decode(iv);
        String rs = null;
        try {
            rs = decryptByAesBytes(decryptBytes, keyBytes, ivBytes);
            log.error("数据解密 success rs:{}",rs);
        }catch (Exception e){
            log.error("数据解密 fail {} ",ExceptionUtils.getStackTrace(e));
        }
        return rs;
    }

    /**
     * AES解密
     * Add by 成长的小猪（Jason.Song） on 2018/10/26
     * @param decryptedBytes    待解密的字节数组
     * @param keyBytes          解密密钥字节数组
     * @param ivBytes           IV初始化向量字节数组
     * @return
     * @throws Exception
     */
    public static String decryptByAesBytes(byte[] decryptedBytes, byte[] keyBytes, byte[] ivBytes) throws Exception {
        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
        SecretKeySpec spec = new SecretKeySpec(keyBytes, "AES");
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
        parameters.init(new IvParameterSpec(ivBytes));
        cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
        byte[] resultByte = cipher.doFinal(decryptedBytes);
        if (null != resultByte && resultByte.length > 0) {
            return new String(resultByte, "UTF-8");
        }
        return null;
    }

}
