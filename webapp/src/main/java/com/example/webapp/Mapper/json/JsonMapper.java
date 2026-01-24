package com.example.webapp.Mapper.json;

import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.query.CourseJsonQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface JsonMapper {


    @Results(id = "courseResultsMap",value = {
            @Result(id=true,property="id",column="id"),
            @Result(property="name",column="name")
    })
    @SelectProvider(type= JsonMapperDynaSqlProvider.class,method="selectAll")
    List<CourseDTO> selectAll(CourseJsonQuery query);


}