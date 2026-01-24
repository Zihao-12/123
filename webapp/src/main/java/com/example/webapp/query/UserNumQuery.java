package com.example.webapp.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserNumQuery implements Serializable {
    private static final long serialVersionUID = -3226636602328151880L;
    private Integer nodeId;
    private String parentIdFullPath;
    /**
     * 等于0学生 大于0 班主任+助教
     */
    private int position;


}
