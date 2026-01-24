package com.example.webapp.Service.area;

import com.example.webapp.DTO.AreaDTO;

import java.util.List;

public interface AreaService {
    public AreaDTO getAreaById(Integer areaId);

    List<AreaDTO> getAreaList(Integer parentId);
}
