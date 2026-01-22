package com.example.webapp.DO;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;




@Data 
public class StUserLearnRecordDO implements Serializable {

    private static final long serialVersionUID = -6240099398294685197L;

	private Integer id;

    private Integer mechanismId;

	private Integer userId;
    /**
     * 课程id
     */

	private Integer courseId;
    /**
     * 视频学习时间
     */

    private Integer duration;
    /**
     * 学习进度
     */

	private Integer progress;
    /**
     * 0未完成 1已完成
     */

	private Integer complete;
    /**
     * 首次听课时间
     */

	private Date firstTime;
    /**
     * 完成时间
     */

	private Date completeTime;

	private Integer isDelete;

	private Date updateTime;

	private Date createTime;
}
