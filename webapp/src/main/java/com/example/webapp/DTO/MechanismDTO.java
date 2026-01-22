package com.example.webapp.DTO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MechanismDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String account;
    private String password;
    private Integer attribute;
    private String attributeCn;
    private Integer province;
    private String provinceCn;
    private Integer city;
    private String cityCn;
    private String address;
    private String domain;
    private String navbarLogo;
    private String loginLogo;
    private String showName;
    private String appLoginLogo;
    private String appNavbarLogo;
    private String appDomain;
    private String appShowName;
    private Integer ipRestrict;
    private Integer isDelete;
    private Date updateTime;
    private Date createTime;

    /**
     * 机构联系人
     */
    private List<MechanismContactPersonDTO> contactPersonList;
}
