package com.example.webapp.Service.content;

import com.example.webapp.DO.ContentDO;
import com.example.webapp.DO.ContentImageRefDO;
import com.example.webapp.DTO.ContentDTO;
import com.example.webapp.Mapper.content.ContentMapper;
import com.example.webapp.Query.ContentQuery;
import com.example.webapp.VO.ContentVO;
import com.example.webapp.common.Constant;
import com.example.webapp.common.redis.RedisKeyGenerator;
import com.example.webapp.common.redis.RedisUtils;
import com.example.webapp.enums.ContentTypeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.third.AliOSS;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Slf4j
public class ContentServiceImpl implements ContentService, Serializable {
    private static final long serialVersionUID = 4800994516532057532L;
    private static final String PORTAL_HT_DETAIL = "portal_ht_detail_";
    private static final String PORTAL_RELATIVE_DETAIL = "portal_relative_detail_";
    private static final String PORTAL_PRACTICE_BASE_DETAIL = "portal_practice_base_detail_";
    private static final String PORTAL_RESULTS_DETAIL = "portal_results_detail_";
    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public ResultPage list(ContentQuery query) {
        PageInfo pageInfo = null;
        try {
            query.setPageNo(query.getPageNo() == null ? 0 : query.getPageNo());
            query.setPageSize(query.getPageSize() == null ? Constant.PAGE_SIZE : query.getPageSize());
            if(StringUtils.isNotBlank(query.getTitle())){
                query.setTitle("%"+query.getTitle()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<ContentDTO> list = contentMapper.selectAll(query);
            pageInfo = new PageInfo(list);
            if (CollectionUtils.isEmpty(list)) {
                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            }else {
                list.stream().forEach(b->{
                    b.setTypeCn(ContentTypeEnum.getNameByType(b.getType()));
                });
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result insert(ContentDO contentDO) {
        int count = 0;
        try {
            count = contentMapper.insert(contentDO);
            if (CollectionUtils.isNotEmpty(contentDO.getImageUrlList())) {
                contentMapper.insertImageRef(contentDO.getId(),contentDO.getImageUrlList());
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result update(ContentDO contentDO) {
        int count = 0;
        try {
            count = contentMapper.update(contentDO);
            contentMapper.deleteImageRef(contentDO.getId());
            if (CollectionUtils.isNotEmpty(contentDO.getImageUrlList())) {
                contentMapper.insertImageRef(contentDO.getId(),contentDO.getImageUrlList());
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }


    /**
     * 0.50|&-&|content/953689289189752832.jpg|&-&|4冬奥志愿者宣传页设计.jpg
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result delete(int id) {
        int count = 0;
        try {
            List<ContentImageRefDO> imageDOList = contentMapper.viewImageRef(id);
            count = contentMapper.delete(id);
            contentMapper.deleteImageRef(id);
            if (CollectionUtils.isNotEmpty(imageDOList)) {
                List<String> objectKeys = new ArrayList<>();
                imageDOList.forEach(image->{
                    String[] arr = image.getImageUrl().split("\\|");
                    objectKeys.add(arr[Constant.IMAGE_INDEX]);
                });
                AliOSS.deleteObjectByKeys(objectKeys);
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    @Override
    public Result view(int id) {
        ContentDTO dto = null;
        try {
            dto = contentMapper.view(id);
            if(dto==null){
                dto = new ContentDTO();
            }
            if(StringUtils.isNotBlank(dto.getImageUrls())){
                dto.setImageUrlList(Arrays.asList(dto.getImageUrls().split(",")));
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(dto);

    }

    /**
     * 上下架
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public Result updateStatus(int id, int status) {
        int count = 0;
        try {
            count = contentMapper.updateStatus(id,status);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    /**
     * 是否置顶:0未置顶 1.置顶
     * @param id
     * @param top
     * @return
     */
    @Override
    public Result top(int id, int top) {
        int count = 0;
        try {
            count = contentMapper.top(id,top);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    /**
     * 是否推荐:0否 1.是
     * @param id
     * @param recommend
     * @return
     */
    @Override
    public Result recommend(int id, int recommend) {
        int count = 0;
        try {
            count = contentMapper.recommend(id,recommend);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }


    /**
     * 前台--列表(type=1通用新闻资讯,)
     * @param query
     * @return
     */
    @Override
    public ResultPage portalList(ContentQuery query) {
        try {
            String key = ContentQuery.getRedisKey(query.getType(),query.getCategoryId(),query.getPageNo(),query.getPageSize());
            ResultPage resultPage = (ResultPage) redisUtils.get(key);
            if (resultPage != null) {
                return resultPage;
            }
            PageInfo pageInfo = null;
            //使用分页插件,核心代码就这一行 #分页配置#
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<ContentVO> list = contentMapper.portalList(query);
            //处理图片地址
            list = handleImageUrl(list);
            pageInfo = new PageInfo(list);
            ResultPage page = ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            redisUtils.set(key,page, RedisUtils.TIME_MINUTE_10);
            return page;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            log.error(ExceptionUtils.getStackTrace(e));
            return ResultPage.fail(Constant.NO_DATA);
        }
    }

    /**
     * 处理图片地址
     * @param list
     * @return
     */
    private List<ContentVO> handleImageUrl(List<ContentVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        for (ContentVO contentVO : list) {
            if (StringUtils.isNotBlank(contentVO.getImageUrls())) {
                contentVO.setImageUrlList(Arrays.asList(contentVO.getImageUrls().split(",")));
            }
        }
        return list;
    }

    /**
     * 详情页 浏览量处理
     * @param id
     * @param key
     * @param pageViewKey
     * @return
     */
    private Result<ContentVO> portalDetail(int id, String key, String pageViewKey) {
        ContentVO detail = contentMapper.portalDetail(id);
        //更新浏览量
        Long count = redisUtils.hyperlologCount(pageViewKey + id);
        if (count!=null){
            int detailCount = detail.getPageView() == null ? 0 : detail.getPageView();
            int redisCount = count == null ? 0 : count.intValue();
            int pageView = detailCount + redisCount;
            contentMapper.updatePageView(id,pageView);
            detail.setPageView(pageView);
        }
        redisUtils.set(key,detail==null?new ContentVO():detail,RedisUtils.TIME_MINUTE_10);
        return Result.ok(detail);
    }

    /**
     * 前台--详情(通用新闻资讯)
     * @param id
     * @return
     */
    @Override
    public Result portalHtDetail(int id) {
        try {
            String key = RedisKeyGenerator.getKey(ContentServiceImpl.class,PORTAL_HT_DETAIL,id);
            ContentVO detail = (ContentVO) redisUtils.get(key);
            if (detail!=null) {
                return Result.ok(detail);
            }
            return portalDetail(id, key,Constant.REDIS_KEY_HT_VIEW);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return Result.fail(Constant.NO_DATA);
    }

    /**
     * 获取部分列表数据
     * @param query
     * @return
     */
    @Override
    public Result portalPortionList(ContentQuery query) {
        String key = ContentQuery.getPortionRedisKey(query.getType(),query.getPortionCount());
        List<ContentVO> list = (List<ContentVO>) redisUtils.get(key);
        if (CollectionUtils.isNotEmpty(list)) {
            return Result.ok(list);
        }
        list = contentMapper.portalPortionList(query);
        //处理图片地址
        list = handleImageUrl(list);
        redisUtils.set(key,CollectionUtils.isEmpty(list)?new ArrayList<ContentVO>():list,RedisUtils.TIME_MINUTE_10);
        return Result.ok(list);
    }
}