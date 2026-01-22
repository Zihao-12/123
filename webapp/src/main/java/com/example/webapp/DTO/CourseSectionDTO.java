package com.example.webapp.DTO;

import com.example.webapp.DO.CourseSectionDO;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;




@Data 
public class CourseSectionDTO implements Serializable {
    private static final long serialVersionUID = 6229428839650351604L;

    private Integer id;

    private String uniqueCode;

    private Integer courseId;

    private String name;

    private String video;

    private String videoPlayName;

    private Integer videoDuration;

    private Integer parentId;

    private Integer type;

    private String courseware;

    private String coursewareName;

    private Integer sort;

    private Integer isDelete;

    private Date updateTime;

    private Date createTime;

    private List<CourseSectionDO> sectionList;

    private Integer complete;
}
