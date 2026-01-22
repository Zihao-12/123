package com.example.webapp.Service.Banner;


import com.example.webapp.DO.BannerDO;
import com.example.webapp.DTO.BannerDTO;
import com.example.webapp.DTO.BindMechanismDTO;
import com.example.webapp.Query.BannerQuery;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.BannerTypeEnum;
import com.example.webapp.enums.SourceTypeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.third.AliOSS;
import com.example.webapp.utils.ListOperateDTO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@EnableTransactionManagement
@Service
@Slf4j
public class BannerServiceImpl implements BannerService, Serializable {
    private static final long serialVersionUID = 4800994516532057532L;
    @Autowired
    private BannerMapper bannerMapper;
    @Value("${portal.domain}")
    private String portalDomain;

    /**
     * 机构端轮播图列表
     * @param query
     * @return
     */
    @Override
    public ResultPage mechList(BannerQuery query) {
        PageInfo pageInfo = null;
        try {
            query.setPageNo(query.getPageNo() == null ? 0 : query.getPageNo());
            query.setPageSize(query.getPageSize() == null ? Constant.PAGE_SIZE : query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<BannerDTO> list = bannerMapper.mechList(query);
            pageInfo = new PageInfo(list);
            if (CollectionUtils.isEmpty(list)) {
                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            }else {
                list.stream().forEach(b->{
                    b.setTypeCn(BannerTypeEnum.getNameByType(b.getType()));
                    b.setSource(SourceTypeEnum.YUN_YING.getDescription());
                    if(b.getMechanismId()> Constant.YUNYING_MECHANISM_ID){
                        b.setSource(SourceTypeEnum.ZI_JIANG.getDescription());
                    }
                });
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    /**
     * 运营端轮播图列表
     * @param query
     * @return
     */
    @Override
    public ResultPage list(BannerQuery query) {
        PageInfo pageInfo = null;
        try {
            query.setPageNo(query.getPageNo() == null ? 0 : query.getPageNo());
            query.setPageSize(query.getPageSize() == null ? Constant.PAGE_SIZE : query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<BannerDTO> list = bannerMapper.list(query);
            pageInfo = new PageInfo(list);
            if (CollectionUtils.isEmpty(list)) {
                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            }else {
                list.stream().forEach(b->{
                    b.setTypeCn(BannerTypeEnum.getNameByType(b.getType()));
                    b.setSource(SourceTypeEnum.YUN_YING.getDescription());
                    if(b.getMechanismId()>Constant.YUNYING_MECHANISM_ID){
                        b.setSource(SourceTypeEnum.ZI_JIANG.getDescription());
                    }
                });
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    /**
     * 运营（机构）创建轮播图
     * @param bannerDO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @RepeatableCommit(timeout = 5)
    @Override
    public Result insert(BannerDO bannerDO) {
        int count = 0;
        try {
            if(bannerDO.getType() == null){
                bannerDO.setType(BannerTypeEnum.H5_HOME.getType());
            }
            if(Constant.JUMP_TYPE_H5.equals(bannerDO.getJumpType())){
                bannerDO.setJumpUrl(portalDomain);
            }
            count = bannerMapper.insert(bannerDO);
            if(bannerDO.getMechanismId()>Constant.YUNYING_MECHANISM_ID){
                List<MechanismBannerRefDO> refDOList = Lists.newArrayList();
                MechanismBannerRefDO refDO =new MechanismBannerRefDO();
                refDO.setMechanismId(bannerDO.getMechanismId());
                refDO.setBannerId(bannerDO.getId());
                refDOList.add(refDO);
                bannerMapper.insertMechanismBannerRefList(refDOList);
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }
    @Cacheable(prefix = "view",fieldKey = "#bannerDO.id",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Override
    public Result update(BannerDO bannerDO) {
        int count = 0;
        try {
            if(Constant.JUMP_TYPE_H5.equals(bannerDO.getJumpType())){
                bannerDO.setJumpUrl(portalDomain);
            }
            count = bannerMapper.update(bannerDO);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    @Cacheable(prefix = "view",fieldKey = "#id",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Override
    public Result delete(int id) {
        int count = 0;
        try {
            BannerDTO dto = bannerMapper.view(id);
            count = bannerMapper.delete(id);
            if (dto != null && StringUtils.isNotBlank(dto.getImageUrl())) {
                AliOSS.deleteObject(dto.getImageUrl());
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    @Cacheable(prefix = "view",fieldKey = "#id")
    @Override
    public BannerDTO view(int id) {
        BannerDTO dto = null;
        try {
            dto = bannerMapper.view(id);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return dto;
    }

    /**
     * 上下架:0下架 1上架
     *
     * @param id
     * @param status
     * @return
     */
    @Cacheable(prefix = "view",fieldKey = "#id",cacheOperation = Cacheable.CacheOperation.UPDATE)
    @Override
    public Result updateStatus(int id, int status) {
        int count = 0;
        try {
            count = bannerMapper.updateStatus(id,status);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }


    /**
     * 轮播图绑定到指定部机构
     * @param bindMechanismDTO
     * @return
     */
    @RepeatableCommit(timeout = 10)
    @Override
    public Result bindSpecifyMechanism(BindMechanismDTO bindMechanismDTO) {
        if(CollectionUtils.isEmpty(bindMechanismDTO.getMechanismIdList())){
            return  Result.fail("机构ID不能空");
        }
        Integer bannerId = bindMechanismDTO.getId();
        List<Integer> existedRefIdList =bannerMapper.getRefMechanismIdList(bindMechanismDTO.getId());
        ListOperateDTO<Integer> lo = ListUtil.getListOperateDTO(bindMechanismDTO.getMechanismIdList(),existedRefIdList);
        //新增
        if(CollectionUtils.isNotEmpty(lo.getAddList())){
            List<MechanismBannerRefDO> refDOList = Lists.newArrayList();
            lo.getAddList().stream().forEach(mid ->{
                MechanismBannerRefDO refDO = new MechanismBannerRefDO();
                refDO.setBannerId(bannerId);
                refDO.setMechanismId(mid);
                refDOList.add(refDO);
            });
            bannerMapper.insertMechanismBannerRefList(refDOList);
        }
        //删除
        if(CollectionUtils.isNotEmpty(lo.getDeleteList())){
            bannerMapper.disassociateMechanismRefList(bannerId,lo.getDeleteList());
        }
        return Result.ok(0);
    }

    /**
     * 轮播图绑定到全部机构
     * @param bannerId
     * @return
     */
    @RepeatableCommit(timeout = 10)
    @Override
    public Result bindAllMechanism(Integer bannerId) {
        List<Integer> mechanismIdList = bannerMapper.getOpenMechanismIdList();
        if(CollectionUtils.isNotEmpty(mechanismIdList)){
            BindMechanismDTO bindMechanismDTO =new BindMechanismDTO();
            bindMechanismDTO.setId(bannerId);
            bindMechanismDTO.setMechanismIdList(mechanismIdList);
            bindSpecifyMechanism(bindMechanismDTO);
        }
        return Result.ok(0);
    }

    /**
     * 取消轮播图关联机构
     * @param bannerId
     * @param mechanismId
     * @return
     */
    @Override
    public Result disassociate(Integer bannerId, Integer mechanismId) {
        bannerMapper.disassociate(bannerId,mechanismId);
        return Result.ok(0);
    }

    /**
     * 获取轮播图关联的机构列表
     * @param query
     * @return
     */
    @Override
    public PageInfo getRefMechanismList(BindMechanismQuery query) {
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null? Constant.PAGE_SIZE :query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            Page page = PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<MechanismDTO> list = bannerMapper.getRefMechanismList(query);
            PageInfo pageInfo = new PageInfo(list);
            return pageInfo;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return  null;
    }
}