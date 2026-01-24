package com.example.webapp.Service.mechanismOpen;

import com.example.webapp.DO.MechanismOpenDO;
import com.example.webapp.DTO.MechanismOpenDTO;
import com.example.webapp.DTO.MechanismOpenDelayDTO;
import com.example.webapp.query.MechanismOpenQuery;
import com.example.webapp.result.Result;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface MechanismOpenService {

    /**
     * 查询机构列表
     * @param query
     * @return
     */
    PageInfo findMechanismOpenList(MechanismOpenQuery query);

    /**
     * 获取所有 开通进行中的 机构 不分页
     * @return
     */
    List<MechanismOpenDTO> findMechanismOpeningList();

    /**
     * 创建机构
     * @param mechanismOpenDO
     * @return
     */
    public Result saveMechanismOpenDO(MechanismOpenDO mechanismOpenDO);


    /**
     * 根据ID查询机构开通
     * @param mechanismOpenId
     * @return
     */
    Result findMechanismOpenById(Integer mechanismOpenId);

    Result findCoursePackageById(Integer Id);

    Result delOpen(Integer id);

    /**
     * 机构微软/实训开通 判断机构开通是否可以删除
     * @param id 开通ID
     *
     * @return
     */
    Result isDelOpen(Integer id);

    Result deactivateOpen(Integer id);

    Result openDelay(MechanismOpenDelayDTO openDelay);

    Integer getPracticeOpenStatus(Integer mechanismId);
}