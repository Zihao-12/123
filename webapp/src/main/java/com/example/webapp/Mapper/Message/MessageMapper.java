package com.example.webapp.Mapper.Message;

import com.example.webapp.DO.MechanismMessageRefDO;
import com.example.webapp.DO.MessageDO;
import com.example.webapp.DTO.MechanismDTO;
import com.example.webapp.DTO.MessageDTO;
import com.example.webapp.Query.BindMechanismQuery;
import com.example.webapp.Query.MessageQuery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface MessageMapper {

    /**
     * 机构端消息列表
     * @param query
     * @return
     */
    @SelectProvider(type= MessageMapperDynaSqlProvider.class,method="mechList")
    List<MessageDTO> mechList(MessageQuery query);

    @SelectProvider(type= MessageMapperDynaSqlProvider.class,method="selectAll")
    List<MessageDTO> selectAll(MessageQuery query);


    @Insert(" INSERT INTO `message`(`name`,`details`,`courseware`,`courseware_name`,`status`,mechanism_id) " +
            " VALUES (#{name},#{details},#{courseware},#{coursewareName},#{status},#{mechanismId})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(MessageDO messageDO);


    @UpdateProvider(type = MessageMapperDynaSqlProvider.class,method = "update")
    int update(MessageDO MessageDO);

    @Update("update message set is_delete=1 where id=#{id}")
    int delete(Integer id);

    @Select("select a.`id`,a.`name`,a.`details`,a.`courseware`,a.`courseware_name`,a.`status`,a.mechanism_id,a.`create_time`,a.`update_time`,a.`is_delete`" +
            " from message a  " +
            " where a.id=#{id}")
    MessageDTO view(Integer id);

    @Update("update message set status=#{status} where id=#{id}")
    int updateStatus(int id, int status);

    /**
     * 批量保存机构消息关联
     * @param mechanismMessageRefDOList
     * @return
     */
    @InsertProvider(type = MessageMapperDynaSqlProvider.class, method = "insertMechanismMessageRefList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertMechanismMessageRefList(List<MechanismMessageRefDO> mechanismMessageRefDOList);

    /**
     * 取消消息机构关联
     * @param messageId
     * @param mechanismId
     * @return
     */
    @Update("update mechanism_message_ref set is_delete=1 where mechanism_id = #{mechanismId} and message_id=#{messageId}")
    int disassociate(Integer messageId, Integer mechanismId);

    /**
     * 批量取消消息机构关联
     * @param messageId
     * @param mechanismIdList
     * @return
     */
    @Update("<script>"
            + "update mechanism_message_ref set is_delete=1 where   message_id=#{messageId} and mechanism_id in  "
            + "<foreach item='item' index='index' collection='mechanismIdList' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    int disassociateMechanismRefList(Integer messageId, List<Integer> mechanismIdList);


    /**
     * 获取消息关联的机构列表
     * @param query
     * @return
     */
    @SelectProvider(type= MessageMapperDynaSqlProvider.class,method="getRefMechanismList")
    List<MechanismDTO> getRefMechanismList(BindMechanismQuery query);

    /**
     * 获取关联机构ID
     * @param messageId
     * @return
     */
    @Select(" select DISTINCT mechanism_id  from mechanism_message_ref where is_delete =0 and message_id=#{messageId} ")
    List<Integer> getRefMechanismIdList(Integer messageId);
}
