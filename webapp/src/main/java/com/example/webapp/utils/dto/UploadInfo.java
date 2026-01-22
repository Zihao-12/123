package com.example.webapp.utils.dto;

import java.io.Serializable;

public class UploadInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 文件相对路径路径(包含第三方资源路径)
     */
    private String filePath;
    /**
     * 原文件名
     */
    private String fileOldName;
    /**
     * 新文件名
     */
    private String fileNewName;
    /**
     * 扩展名
     */
    private String extendName;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileOldName() {
        return fileOldName;
    }

    public void setFileOldName(String fileOldName) {
        this.fileOldName = fileOldName;
    }

    public String getFileNewName() {
        return fileNewName;
    }

    public void setFileNewName(String fileNewName) {
        this.fileNewName = fileNewName;
    }

    public String getExtendName() {
        return extendName;
    }

    public void setExtendName(String extendName) {
        this.extendName = extendName;
    }
}
