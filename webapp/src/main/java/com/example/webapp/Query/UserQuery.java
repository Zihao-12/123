package com.example.webapp.Query;

import com.example.webapp.result.ResultPage;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserQuery extends ResultPage implements Serializable {
    private String nickName;

    private String phone;

    private Long registerTime;

    private Date beginTime;

    private Date endTime;

    private Integer status;

    private Integer position;

    private Integer mechanismId;

    private Integer collegeId;
}
