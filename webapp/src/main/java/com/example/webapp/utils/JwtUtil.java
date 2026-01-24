package com.example.webapp.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.enums.PlatformMarkEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class JwtUtil {
    public static final String JWT_TOKEN = "_token_";
    public static final String JWT_SID = "_sid_";


    public static final String TOKEN_KEY_USERID = "userId";
    public static final String TOKEN_KEY_MECHANISMID = "mechanismId";
    public static final String TOKEN_KEY_GENDER = "gender";
    public static final String TOKEN_KEY_AGE = "age";
    public static final String TOKEN_KEY_TYPE = "type";
    public static final String TOKEN_KEY_POSITION = "position";
    public static final String TOKEN_KEY_USERNAME = "userName";
    public static final String TOKEN_KEY_MECHANISMNAME = "mechanismName";
    public static final String TOKEN_KEY_SHOW_NAME = "showName";
    public static final String TOKEN_KEY_APP_SHOW_NAME = "appShowName";
    public static final String TOKEN_KEY_LOGIN_LOGO = "loginLogo";
    public static final String TOKEN_KEY_APP_LOGIN_LOGO = "appLoginLogo";
    public static final String TOKEN_KEY_PHONE = "phone";
    public static final String TOKEN_KEY_NICKNAME = "nickName";
    public static final String TOKEN_KEY_HEADIMG = "headImg";
    public static final String TOKEN_KEY_SID = "sid";
    public static final String TOKEN_KEY_EXP_MINUTES = "expMinutes";
    public static final String TOKEN_KEY_EXPIRED_DATE = "expiredDate";
    public static final String TOKEN_KEY_OPEN_ID = "openId";
    public static final String TOKEN_KEY_UNION_ID = "unionId";
    public static final String TOKEN_KEY_OPEN_LOGIN_TYPE = "openLoginType";

    public static final String SECRET = "JIAOSHOUJIA_JKKLJOfj";
    public static final String DELIMITER_ONE_COMMA = ",";

    public static String createToken(UserDto user) {
        //生成时间
        Date date = new Date();
        //过期时间
        Date expiresDate =DateTimeUtil.plusMinutes(date,user.getExpMinutes());
        Map<String, Object> headerMap = Maps.newHashMap();
        headerMap.put("alg", "HS256");
        headerMap.put("typ", "JWT");
        // param backups {iss:Service, aud:APP}
        String token = JWT.create().withHeader(headerMap)
                .withIssuer("Service")
                .withClaim("aud", "APP")
                .withClaim(TOKEN_KEY_USERID, String.valueOf(user.getId()))
                .withClaim(TOKEN_KEY_MECHANISMID, String.valueOf(user.getMechanismId()))
                .withClaim(TOKEN_KEY_OPEN_LOGIN_TYPE, String.valueOf(user.getOpenLoginType()))
                .withClaim(TOKEN_KEY_GENDER, String.valueOf(user.getGender()))
                .withClaim(TOKEN_KEY_AGE, String.valueOf(user.getAge()))
                .withClaim(TOKEN_KEY_TYPE, String.valueOf(user.getType()))
                .withClaim(TOKEN_KEY_POSITION, String.valueOf(user.getPosition()))
                .withClaim(TOKEN_KEY_USERNAME, user.getUserName())
                .withClaim(TOKEN_KEY_MECHANISMNAME, user.getMechanismName())
                .withClaim(TOKEN_KEY_SHOW_NAME, user.getShowName())
                .withClaim(TOKEN_KEY_APP_SHOW_NAME, user.getAppShowName())
                .withClaim(TOKEN_KEY_LOGIN_LOGO, user.getLoginLogo())
                .withClaim(TOKEN_KEY_APP_LOGIN_LOGO, user.getAppLoginLogo())
                .withClaim(TOKEN_KEY_PHONE, user.getPhone())
                .withClaim(TOKEN_KEY_NICKNAME, user.getNickName())
                .withClaim(TOKEN_KEY_HEADIMG, user.getHeadImg())
                .withClaim(TOKEN_KEY_OPEN_ID, user.getOpenId())
                .withClaim(TOKEN_KEY_UNION_ID, user.getUnionId())
                .withClaim(TOKEN_KEY_SID, user.getSid())
                .withClaim(TOKEN_KEY_EXP_MINUTES, String.valueOf(user.getExpMinutes()))
                .withClaim(TOKEN_KEY_EXPIRED_DATE,DateTimeUtil.format(expiresDate,DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))
                .withIssuedAt(date)
                .withExpiresAt(expiresDate)
                .withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC256(SECRET));
        return token;
    }


    /**
     * 解密Token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Map<String, Claim> verifyToken(String token) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            jwt = verifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            if(e instanceof TokenExpiredException){
                //token过期
                return null;
            }else {
                throw new RuntimeException(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    /**
     * 根据Token获取 sid
     *
     * @param token
     * @return sid
     */
    public static UserDto getLoginUser(String token, String source) {
        try {
            Map<String, Claim> claimMap = verifyToken(token);
            if(claimMap!=null){
                UserDto userDto =new UserDto();
                userDto.setId(getIntegerValue(TOKEN_KEY_USERID,claimMap));
                userDto.setMechanismId(getIntegerValue(TOKEN_KEY_MECHANISMID,claimMap));
                userDto.setOpenLoginType(getIntegerValue(TOKEN_KEY_OPEN_LOGIN_TYPE,claimMap));
                userDto.setGender(getIntegerValue(TOKEN_KEY_GENDER,claimMap));
                userDto.setAge(getIntegerValue(TOKEN_KEY_AGE,claimMap));
                userDto.setType(getIntegerValue(TOKEN_KEY_TYPE,claimMap));
                userDto.setPosition(getIntegerValue(TOKEN_KEY_POSITION,claimMap));
                userDto.setUserName(getStringValue(TOKEN_KEY_USERNAME,claimMap));
                userDto.setMechanismName(getStringValue(TOKEN_KEY_MECHANISMNAME,claimMap));
                userDto.setShowName(getStringValue(TOKEN_KEY_SHOW_NAME,claimMap));
                userDto.setAppShowName(getStringValue(TOKEN_KEY_APP_SHOW_NAME,claimMap));
                userDto.setLoginLogo(getStringValue(TOKEN_KEY_LOGIN_LOGO,claimMap));
                userDto.setAppLoginLogo(getStringValue(TOKEN_KEY_APP_LOGIN_LOGO,claimMap));
                userDto.setPhone(getStringValue(TOKEN_KEY_PHONE,claimMap));
                userDto.setNickName(getStringValue(TOKEN_KEY_NICKNAME,claimMap));
                userDto.setHeadImg(getStringValue(TOKEN_KEY_HEADIMG,claimMap));
                userDto.setOpenId(getStringValue(TOKEN_KEY_OPEN_ID,claimMap));
                userDto.setUnionId(getStringValue(TOKEN_KEY_UNION_ID,claimMap));
                userDto.setSid(getStringValue(TOKEN_KEY_SID,claimMap));
                userDto.setExpMinutes(getIntegerValue(TOKEN_KEY_EXP_MINUTES,claimMap));
                userDto.setExpiredDate(getStringValue(TOKEN_KEY_EXPIRED_DATE,claimMap));
                return userDto;
            }
        }catch (Exception e){
            log.error("getLoginUser  来源 is {} ,token is {},exception is {}",source,token,ExceptionUtils.getStackTrace(e));
        }
        return null;
    }



    private static String  getStringValue(String key,Map<String, Claim> claimMap){
        Claim claim = claimMap.get(key);
        if(claim!=null){
            return claim.asString();
        }
        return null;
    }

    private static Integer  getIntegerValue(String key,Map<String, Claim> claimMap){
        Claim claim = claimMap.get(key);
        if(claim!=null){
            String  value =claim.asString();
            if(!"null".equals(value) && StringUtils.isNotBlank(value)){
                return Integer.parseInt(claim.asString());
            }
        }
        return null;
    }
    public static String getCookieTokenKey(PlatformMarkEnum markEnum){
        return JWT_TOKEN+markEnum.getMark();
    }

    public static String getCookieSidKey(PlatformMarkEnum markEnum){
        return JWT_SID+markEnum.getMark();
    }
}
