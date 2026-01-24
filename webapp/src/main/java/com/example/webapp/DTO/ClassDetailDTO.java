package com.example.webapp.DTO;

import com.example.webapp.DO.UserDO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClassDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 班主任
     */
    private List<UserDO> teacherList;
    /**
     * 班级助教列表
     */
    private List<UserDO> assistantList;
    /**
     * 班级学生列表
     */
    private List<UserDO> studentList;

}
