package com.example.webapp.DTO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MechanismContactPersonDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer mechanismId;
    private String name;
    private String phone;
    private String email;
    private String remark;
    private Integer isDelete;
    private Date updateTime;
    private Date createTime;
}
