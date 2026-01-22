package com.example.webapp.Mapper;

import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.Query.CourseQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public class CourseMapper {
    public List<CourseDTO> selectAll(CourseQuery query) {
        return null;
    }
}
