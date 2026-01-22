package com.example.webapp.utils.dto;

import java.io.Serializable;

public class ChunkEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private UploadInfo uf;
    private String uuid;
    private Integer chunk;
    private Integer chunks;
    private String fileName;
    private String extendName;
    private String filePath;

    public UploadInfo getUf() {
        return uf;
    }

    public void setUf(UploadInfo uf) {
        this.uf = uf;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getChunk() {
        return chunk;
    }

    public void setChunk(Integer chunk) {
        this.chunk = chunk;
    }

    public Integer getChunks() {
        return chunks;
    }

    public void setChunks(Integer chunks) {
        this.chunks = chunks;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExtendName() {
        return extendName;
    }

    public void setExtendName(String extendName) {
        this.extendName = extendName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
