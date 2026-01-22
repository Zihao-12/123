package com.example.webapp.DTO;

import com.example.webapp.DO.StUserLearnRecordDO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CourseDTO {
    private static final long serialVersionUID = -5617551860034624357L;
    private Integer id;
    private String name;
    private String introduction;
    private String cover;
    private String detail;
    private Integer contentCategoryId;
    private String contentCategoryName;
    private Integer courseSectionNumber;
    private Integer type;
    private Integer mechanismId;
    private String typeCn;
    private Integer status;
    private String statusCn;
    private Integer isDelete;
    private Date updateTime;
    private Date createTime;
    private List<CourseSectionDTO> sectionList;
    private Integer courseCode;
    private Integer packageNumbers;
    private List<CoursePackageDTO> coursePackageList;
    private Integer sort;
    private Integer complete;
    private Integer favorite;
    private Integer lastCourseSectionId;
    private Integer learnedPersonNum;
    private StUserLearnRecordDO userLearnRecord;
    private Integer praiseNum;
    private Integer viewNum;
    private String categoryIds;
    private String categoryNames;
    private String categoryAgeIds;
    private String categoryAgeNames;
    private Integer mode;
    private List<String> idFullPathList;
    private List<String> nameFullPathList;
}
