package com.example.webapp.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class CollegeUserRefDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer userId;
    private Integer collegeId;
    private Integer position;
}
