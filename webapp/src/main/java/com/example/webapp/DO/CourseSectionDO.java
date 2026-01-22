package com.example.webapp.DO;


import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;




@Data 
public class CourseSectionDO implements Serializable {
    private static final long serialVersionUID = -7892270619888914696L;

	private Integer id;

    private String uniqueCode;

	private Integer courseId;

	private String name;

	private String video;

    private Integer videoDuration;

	private Integer parentId;

	private Integer type;

    private String courseware;
    private String coursewareName;

    private Integer sort;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
	private List<CourseSectionDO> sectionList;
}
