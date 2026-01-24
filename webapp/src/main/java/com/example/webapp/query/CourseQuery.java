package com.example.webapp.query;


import com.example.webapp.result.ResultPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;



@Data
public class CourseQuery extends ResultPage implements Serializable {

    private static final long serialVersionUID = -8873473813442439780L;

    private Integer courseId;
	private String name;
	private List<Integer> categoryIdList;
    private List<Integer> categoryAgeIdList;
	private Integer status;
   	private Integer type;
    private Integer mechanismId;
    private Integer userId;
    private Integer coursePackageId;
    private Integer courseListType;

}
