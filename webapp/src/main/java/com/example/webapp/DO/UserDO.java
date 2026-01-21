package com.example.webapp.DO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UserDO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String userName;

    private String nickName;

    private String jobNumber;

    private String phone;

    private Integer gender;

    private Integer age;

    private String password;

    private String headImg;

    private String openId;

    private String unionId;

    private String dockingType;

    private String readerBadge;

    private String pinyinAcronym;

    private Integer mechanismId;

    private String mechanismName;

    private Integer position;

    private Integer type;

    private Integer status;

    private String statusView;

    private Integer isDelete;

    private Date updateTime;

    private Date createTime;

    private String showName;

    private String appShowName;

    private String loginLogo;

    private String appLoginLogo;
}