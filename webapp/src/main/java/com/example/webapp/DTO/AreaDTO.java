package com.example.webapp.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaDTO implements Serializable {
    private static final long serialVersionUID = -8630407503903114716L;
    private Integer id;
    private Integer directly;
    private String name;
    private Integer pid;
}
