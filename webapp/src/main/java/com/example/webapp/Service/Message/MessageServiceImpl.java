package com.example.webapp.Service.Message;

import com.example.webapp.DO.MechanismMessageRefDO;
import com.example.webapp.DO.MessageDO;
import com.example.webapp.DTO.BindMechanismDTO;
import com.example.webapp.DTO.MechanismDTO;
import com.example.webapp.DTO.MessageDTO;
import com.example.webapp.Mapper.MechanismOpen.MechanismOpenMapper;
import com.example.webapp.Mapper.Message.MessageMapper;
import com.example.webapp.annotation.RepeatableCommit;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.SourceTypeEnum;
import com.example.webapp.query.BindMechanismQuery;
import com.example.webapp.query.MessageQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.utils.ListOperateDTO;
import com.example.webapp.utils.ListUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

import static com.example.webapp.common.Constant.PAGE_SIZE;

/**
 * 消息管理表
 */
@EnableTransactionManagement
@Slf4j
@Service
public class MessageServiceImpl implements MessageService, Serializable {
    private static final long serialVersionUID = 4800994516532057532L;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MechanismOpenMapper mechanismOpenMapper;

    /**
     * 机构端消息列表
     * @param query
     * @return
     */
    @Override
    public ResultPage mechList(MessageQuery query) {
        PageInfo pageInfo = null;
        try {
            query.setPageNo(query.getPageNo() == null ? 0 : query.getPageNo());
            query.setPageSize(query.getPageSize() == null ? PAGE_SIZE : query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<MessageDTO> list = messageMapper.mechList(query);
            pageInfo = new PageInfo(list);
            if (CollectionUtils.isEmpty(list)) {
                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            }else {
                list.stream().forEach(m->{
                    m.setSource(SourceTypeEnum.YUN_YING.getDescription());
                    if(m.getMechanismId()> Constant.YUNYING_MECHANISM_ID){
                        m.setSource(SourceTypeEnum.ZI_JIANG.getDescription());
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
     * 运营端列表
     * @param query
     * @return
     */
    @Override
    public ResultPage list(MessageQuery query) {
        PageInfo pageInfo = null;
        try {
            query.setPageNo(query.getPageNo() == null ? 0 : query.getPageNo());
            query.setPageSize(query.getPageSize() == null ? PAGE_SIZE : query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<MessageDTO> list = messageMapper.selectAll(query);
            pageInfo = new PageInfo(list);
            if (CollectionUtils.isEmpty(list)) {
                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            }else {
                list.stream().forEach(m->{
                    m.setSource(SourceTypeEnum.YUN_YING.getDescription());
                    if(m.getMechanismId()>Constant.YUNYING_MECHANISM_ID){
                        m.setSource(SourceTypeEnum.ZI_JIANG.getDescription());
                    }
                });
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    @RepeatableCommit
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result insert(MessageDO messageDO) {
        try {
            messageMapper.insert(messageDO);
            if(messageDO.getMechanismId()>Constant.YUNYING_MECHANISM_ID){
                List<MechanismMessageRefDO> refDOList = Lists.newArrayList();
                MechanismMessageRefDO refDO =new MechanismMessageRefDO();
                refDO.setMechanismId(messageDO.getMechanismId());
                refDO.setMessageId(messageDO.getId());
                refDOList.add(refDO);
                messageMapper.insertMechanismMessageRefList(refDOList);
            }
        }catch (Exception e){
            log.error("save error >>>>>>");
            throw e;
        }
        return Result.ok(messageDO.getId());
    }

    @RepeatableCommit
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result update(MessageDO messageDO) {
        try {
            if(messageDO.getId() != null){
                messageMapper.update(messageDO);
            }
        }catch (Exception e){
            log.error("update error >>>>>>");
            throw e;
        }
        return Result.ok(messageDO.getId());
    }

    @Override
    public Result delete(Integer id) {
        try {
            messageMapper.delete(id);
        }catch (Exception e){
            log.error("delete error >>>>>>");
        }
        return Result.ok(id);
    }

    @Override
    public MessageDTO view(Integer id) {
        MessageDTO dto = null;
        try {
            dto = messageMapper.view(id);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return dto;

    }

    /**
     * 0待发布 1已发布
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public Result updateStatus(int id, int status) {
        int count = 0;
        try {
            count = messageMapper.updateStatus(id,status);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    /**
     * 消息绑定到指定部机构
     * @param bindMechanismDTO
     * @return
     */
    @RepeatableCommit(timeout = 10)
    @Override
    public Result bindSpecifyMechanism(BindMechanismDTO bindMechanismDTO) {
        if(CollectionUtils.isEmpty(bindMechanismDTO.getMechanismIdList())){
            return  Result.fail("机构ID不能空");
        }
        Integer messageId = bindMechanismDTO.getId();
        List<Integer> existedRefIdList =messageMapper.getRefMechanismIdList(bindMechanismDTO.getId());
        ListOperateDTO<Integer> lo = ListUtil.getListOperateDTO(bindMechanismDTO.getMechanismIdList(),existedRefIdList);
        //新增
        if(CollectionUtils.isNotEmpty(lo.getAddList())){
            List<MechanismMessageRefDO> refDOList = Lists.newArrayList();
            lo.getAddList().stream().forEach(mid ->{
                MechanismMessageRefDO refDO = new MechanismMessageRefDO();
                refDO.setMessageId(messageId);
                refDO.setMechanismId(mid);
                refDOList.add(refDO);
            });
            messageMapper.insertMechanismMessageRefList(refDOList);
        }
        //删除
        if(CollectionUtils.isNotEmpty(lo.getDeleteList())){
            messageMapper.disassociateMechanismRefList(messageId,lo.getDeleteList());
        }
        return Result.ok(0);
    }

    /**
     * 消息绑定到全部机构
     * @param messageId
     * @return
     */
    @RepeatableCommit(timeout = 10)
    @Override
    public Result bindAllMechanism(Integer messageId) {
        List<Integer> mechanismIdList = mechanismOpenMapper.getOpenMechanismIdList();
        if(CollectionUtils.isNotEmpty(mechanismIdList)){
            BindMechanismDTO bindMechanismDTO =new BindMechanismDTO();
            bindMechanismDTO.setId(messageId);
            bindMechanismDTO.setMechanismIdList(mechanismIdList);
            bindSpecifyMechanism(bindMechanismDTO);
        }
        return Result.ok(0);
    }

    /**
     * 取消消息关联机构
     * @param messageId
     * @param mechanismId
     * @return
     */
    @Override
    public Result disassociate(Integer messageId, Integer mechanismId) {
        messageMapper.disassociate(messageId,mechanismId);
        return Result.ok(0);
    }

    /**
     * 获取消息关联的机构列表
     * @param query
     * @return
     */
    @Override
    public PageInfo getRefMechanismList(BindMechanismQuery query) {
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null? PAGE_SIZE :query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            Page page = PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<MechanismDTO> list = messageMapper.getRefMechanismList(query);
            PageInfo pageInfo = new PageInfo(list);
            return pageInfo;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return  null;
    }
}

