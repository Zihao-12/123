package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data

public class ObjectCategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;


    private Integer id;

    private String idFullPaths;

    private String nameFullPaths;

    private String levelIds;
    private String levelNames;

}