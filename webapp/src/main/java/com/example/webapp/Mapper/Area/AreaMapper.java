package com.example.webapp.Mapper.Area;

import com.example.webapp.DTO.AreaDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface AreaMapper {
    @Select("SELECT * FROM area WHERE pid=#{parentId} and id <>0")
    List<AreaDTO> getAreaList(Integer parentId);

    @Select("SELECT * FROM area WHERE id=#{areaId} and id >0")
    AreaDTO getAreaById(Integer areaId);
}