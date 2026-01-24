package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel( "SSO用户对象")
@Data
public class UserDto implements Serializable {
    @ApiModelProperty(value = "userId")
    private Integer id;
    @ApiModelProperty( value= "0普通用户 1机构游客")
    private Integer type;
    @ApiModelProperty( value= "班级职务： -1无 0学生 1老师 2助教")
    private Integer position;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "手机")
    private String phone;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "用户头像")
    private String headImg;
    private String sid;
    private String mechanismName;
    @ApiModelProperty(value = "PC-机构显示名称" )
    private String showName;
    @ApiModelProperty(value = "H5/小程序/APP-机构显示名称" )
    private String appShowName;
    @ApiModelProperty(value = "PC-登陆页logo" )
    private String loginLogo;
    @ApiModelProperty(value = "H5/小程序/APP-登陆页logo" )
    private String appLoginLogo;
    @ApiModelProperty(value = "登录成功-1仅开通实训 2仅开通微软 3全开通,实训未过期-微软过期 4全开通,实训过期-微软未过期 5全开通,无过期")
    private Integer openLoginType;
    /**  过期分钟数 */
    @ApiModelProperty(value = "登录过期分钟数")
    private Integer expMinutes;
    @ApiModelProperty(value = "登录过期时间")
    private String expiredDate;
    @ApiModelProperty(value = "机构ID")
    private Integer mechanismId;
    @ApiModelProperty(value = "性别")
    private Integer gender;
    @ApiModelProperty(value = "0未设置")
    private Integer age;
    private String openId;
    private String unionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Integer getExpMinutes() {
        return expMinutes;
    }

    public void setExpMinutes(Integer expMinutes) {
        this.expMinutes = expMinutes;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}
