package com.example.webapp.Service.mechanism;

import com.github.pagehelper.PageInfo;

import java.util.List;

public interface MechanismService {

    /**
     * 查询机构列表
     * @param query
     * @return
     */
    PageInfo findMechanismList(MechanismQuery query);

    /**
     * 根据ID查询机构
     * @param mechanismId
     * @return
     */
    Result findMechanismById(Integer mechanismId);

    MechanismDTO getMechanismById(Integer mechanismId);

    MechanismDTO findMechanismByccount(String account);

    /**
     * 创建机构
     * @param mechanismDO
     * @return
     */
    public Result saveMechanismDO(MechanismDO mechanismDO);


    /**
     * 保存机构联系人
     * @param personList
     * @return
     */
    Result saveContactPerson(List<MechanismContactPersonDO> personList);

    /**
     * 删除机构
     * @param id
     * @return
     */
    Result deleteMechanism(Integer id);

    /**
     * 判断机构是否有开通记录
     * @param mechanismId
     * @return
     */
    Result isOpened(Integer mechanismId);

    FrontPageDTO getMechanismAccessUrl(String mechanismId);

    /**
     * 是否限制IP
     * @param mid
     * @param restrict
     * @return
     */
    Result restrictIp(Integer mid, Integer restrict);

    /**
     * 添加IP
     * @param mechanismId
     * @param ip
     * @return
     */
    Result addIp(Integer mechanismId, String ip);

    /**
     * 删除机构限制IP
     * @param ipId
     * @return
     */
    Result deleteIp(Integer mechanismId, Integer ipId);

    /**
     * IP列表
     * @param query
     * @return
     */
    List ipList(IpQuery query);

    /**
     * 验证机构账号是否可用
     * @param account
     * @return
     */
    Result verifyAccount(String account);

    /**
     * 添加机构统计数据生成模版
     * @param fakeDateSetDO
     * @return
     */
    Result saveFakeset(FakeDateSetDO fakeDateSetDO);

    Result getFakeset(Integer mechanismId);
}
