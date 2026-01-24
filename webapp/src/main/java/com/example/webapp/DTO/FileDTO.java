package com.example.webapp.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel( "文件上传")
@Data
public class FileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(notes = "文件路径")
    private String filePath;
    @ApiModelProperty(notes = "文件名")
    private String fileName;
    @ApiModelProperty(notes = "文件大小(Byte)")
    private Long fileSize;
    @ApiModelProperty(notes = "访问URL")
    private String url;

}
