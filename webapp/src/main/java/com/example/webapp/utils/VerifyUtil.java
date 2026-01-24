package com.example.webapp.utils;

import com.example.webapp.utils.dto.KeyValueDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;



@Slf4j
public class VerifyUtil {

    public static final int LENGTH_6 = 6;
    public static final int LENGTH_4 = 4;
    public static final int LENGTH_3 = 3;
    public static final int LENGTH_10 = 10;

    private VerifyUtil() {
    }

    private static final Pattern emailer = Pattern.compile("^[\\w-._]+@[\\w-]+(\\.[\\w-]+)*(\\.([a-zA-Z]){2,3})$");

    private static final Pattern mobiler = Pattern.compile("^(1[0-9][0-9])\\d{8}$");

    private static final Pattern md5Pass32 = Pattern.compile("^[0-9a-f]{32}$");

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static final String LOCALHOST = "127.0.0.1";

    public static final String OPEN_BRACE = "{";

    public static final String CLOSE_BRACE = "}";

    /**
     * 判断str是否是合法email
     *
     * @param str
     * @return
     * @throws
     * @title: isEmail
     * @description:
     */
    public static boolean isEmail(String str) {
        if (str == null) {
            return false;
        }
        String email = str.toLowerCase();
        return emailer.matcher(email).matches();
    }

    public static boolean isMobile(String str) {
        if (str == null) {
            return false;
        }
        return mobiler.matcher(str).matches();
    }

    /**
     * 从一个给定的string中随机拿出length个char拼成一个随机串
     *
     * @param genModel
     * @param length
     * @return
     */
    public static String randomString(String genModel, int length) {
        int genModelLength = genModel.length();
        if (length < 1 || genModelLength < 1) {
            return null;
        }

        Random randGen = new Random();
        char[] numbersAndLetters = (genModel).toCharArray();

        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(genModelLength)];
        }
        return new String(randBuffer);
    }

    /**
     * 生成随机idcard
     * @return
     */
    public static String randomIdcard() {
        return randomString("ABCDEFGHIJKLMNOPQRSTUVWXYZ",  LENGTH_3)+
                DateTimeUtil.dateInt(new Date())+
                randomString("0123456789", LENGTH_10);
    }

    /**
     * 生成随机用户名
     * @param length
     * @return
     */
    public static String randomUserName(int length) {
        return randomString("abcdefghijkmnpqrstuvxyz", 1)+
                randomString("abcdefghijkmnpqrstuvxyz_-23456789", length-1);
    }

    /**
     * 生成随机密码
     * @return
     */
    public static String randomPassword() {
        return randomString("23456789abcdefghjkmnpqrstuvxyzABCDEFGHJKLMNPQRSTUVXYZ", LENGTH_10);
    }

    /**
     * @return
     * @throws
     * @title: randomMobiCode
     * @description:生成随机的手机验证码
     */
    public static String randomMobiCode() {
        return randomString("123456789", LENGTH_6);
    }

    /**
     * @return
     * @throws
     * @title: randomEmailCode
     * @description: 生成随机的密码/邮箱验证码
     */
    public static String randomEmailCode() {
        return randomString("1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", LENGTH_6);
    }

    /**
     * @return
     * @throws
     * @title: randomPicCode
     * @description:图片验证码
     */
    public static String randomPicCode() {
        return randomString("234567890abcdefghjkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ",  LENGTH_4);
    }

    public static Integer parseInt(Integer id ,Map<Integer, KeyValueDTO> map){
        if(map == null || map.get(id) == null ){
            return 0;
        }

        String value = map.get(id).getName();
        if(StringUtils.isBlank(value)){
            value="0";
        }
        return Integer.parseInt(value);
    }

    public static String parseString(Integer id ,Map<Integer, KeyValueDTO> map){
        if(map == null ||  map.get(id)==null){
            return "";
        }

        String value =  map.get(id).getName();
        if(StringUtils.isBlank(value)){
            value="0";
        }
        return value;
    }
}

