package com.example.webapp.Service.Banner;


import com.example.webapp.DO.BannerDO;
import com.example.webapp.DTO.BannerDTO;
import com.example.webapp.DTO.BindMechanismDTO;
import com.example.webapp.Query.BannerQuery;
import com.example.webapp.Query.BindMechanismQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageInfo;

public interface BannerService {

    /**
     * 机构端轮播图列表
     * @param query
     * @return
     */
    ResultPage mechList(BannerQuery query);

    /**
     * 运营端轮播图列表
     * @param query
     * @return
     */
    ResultPage list(BannerQuery query);

    /**
     * 插入
     * @param banner
     * @return
     */
    Result insert(BannerDO banner);

    /**
     * 详情
     * @param id
     * @return
     */
    BannerDTO view(int id);

    /**
     * 更新
     * @param bannerDO
     * @return
     */
    Result update(BannerDO bannerDO);

    /**
     * 删除
     * @param id
     * @return
     */
    Result delete(int id);

    /**
     * 上下架:0下架 1上架
     * @param id
     * @param status
     * @return
     */
    Result updateStatus(int id, int status);


    /**
     * 轮播图绑定到指定部机构
     * @param bindMechanismDTO
     * @return
     */
    Result bindSpecifyMechanism(BindMechanismDTO bindMechanismDTO);

    /**
     * 轮播图绑定到全部机构
     * @param bannerId
     * @return
     */
    Result bindAllMechanism(Integer bannerId);

    /**
     * 取消轮播图关联机构
     * @param bannerId
     * @param mechanismId
     * @return
     */
    Result disassociate(Integer bannerId, Integer mechanismId);

    /**
     * 获取轮播图关联的机构列表
     * @param query
     * @return
     */
    PageInfo getRefMechanismList(BindMechanismQuery query);
}
