package com.example.webapp.Service.Message;

import com.example.webapp.DO.MessageDO;
import com.example.webapp.DTO.BindMechanismDTO;
import com.example.webapp.DTO.MessageDTO;
import com.example.webapp.query.BindMechanismQuery;
import com.example.webapp.query.MessageQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

    /**
     * 机构端消息列表
     * @param query
     * @return
     */
    ResultPage mechList(MessageQuery query);

    /**
     * 运营端列表
     * @param query
     * @return
     */
    ResultPage list(MessageQuery query);

    /**
     * 新建
     * @return
     */
    Result insert(MessageDO messageDO);

    /**
     * 更新
     * @param messageDO
     * @return
     */
    Result update(MessageDO messageDO);

    /**
     * 删除
     * @param id
     * @return
     */
    Result delete(Integer id);

    /**
     * 详情
     * @param id
     * @return
     */
    MessageDTO view(Integer id);

    /**
     * 0待发布 1已发布
     * @param id
     * @param status
     * @return
     */
    Result updateStatus(int id, int status);

    /**
     * 消息绑定到指定部机构
     * @param bindMechanismDTO
     * @return
     */
    Result bindSpecifyMechanism(BindMechanismDTO bindMechanismDTO);

    /**
     * 消息绑定到全部机构
     * @param messageId
     * @return
     */
    Result bindAllMechanism(Integer messageId);

    /**
     * 取消消息关联机构
     * @param messageId
     * @param mechanismId
     * @return
     */
    Result disassociate(Integer messageId, Integer mechanismId);

    /**
     * 获取消息关联的机构列表
     * @param query
     * @return
     */
    PageInfo getRefMechanismList(BindMechanismQuery query);
}

