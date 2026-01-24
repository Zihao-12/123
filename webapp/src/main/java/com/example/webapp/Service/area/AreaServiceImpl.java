package com.example.webapp.Service.area;

import com.example.webapp.DTO.AreaDTO;
import com.example.webapp.Mapper.Area.AreaMapper;
import com.example.webapp.annotation.Cacheable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Service
public class AreaServiceImpl implements AreaService, Serializable {
    @Autowired
    private AreaMapper areaMapper;


    @Cacheable(prefix = "getAreaList",fieldKey = "#parentId")
    @Override
    public List<AreaDTO> getAreaList(Integer parentId) {
        return  areaMapper.getAreaList(parentId);
    }

    @Cacheable(prefix = "getAreaById",fieldKey = "#areaId")
    @Override
    public AreaDTO getAreaById(Integer areaId){
        return areaMapper.getAreaById(areaId);
    }
}
