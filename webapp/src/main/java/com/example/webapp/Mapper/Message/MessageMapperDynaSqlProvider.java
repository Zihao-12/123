package com.example.webapp.Mapper.Message;

import com.example.webapp.DO.MechanismMessageRefDO;
import com.example.webapp.DO.MessageDO;
import com.example.webapp.query.BindMechanismQuery;
import com.example.webapp.query.MessageQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class
MessageMapperDynaSqlProvider {

    /**
     * 机构端消息列表
     * @param query
     * @return
     */
    public String mechList(final MessageQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" DISTINCT b.* ");
                FROM(" message b ");
                LEFT_OUTER_JOIN("  mechanism_message_ref mbr  on b.id=mbr.message_id  and mbr.is_delete=0 ");
                if (!StringUtils.isEmpty(query.getName())) {
                    WHERE(" b.name like #{name} ");
                }

                WHERE(" b.is_delete=0 ");
                WHERE(" mbr.mechanism_id=#{mechanismId} ");

                ORDER_BY(" b.create_time desc");
            }
        };
        return sql.toString();
    }

    /**
     * 运营端
     * @param query
     * @return
     */
    public String selectAll(final MessageQuery query) {
        SQL sql = new SQL() {
            {
                SELECT( "a.`id`,a.`name`,a.`details`,a.`courseware`,a.mechanism_id,a.`courseware_name`,a.`status`,a.`create_time`,a.`update_time`,a.`is_delete`" );
                FROM(" message a ");
                if (StringUtils.isNotBlank(query.getName())) {
                    WHERE(" a.name like #{name}");
                }
                WHERE(" a.mechanism_id=0 ");
                WHERE(" a.is_delete=0");
                ORDER_BY(" a.create_time DESC");
            }
        };
        return sql.toString();
    }

    public String update(final MessageDO messageDO) {
        return new SQL() {
            {
                UPDATE(" message ");
                SET("`name`=#{name}");
                SET("`details`=#{details}");
                SET("`courseware`=#{courseware}");
                SET("`courseware_name`=#{coursewareName}");
                SET("`status`=#{status}");
                WHERE("id=#{id}");            }
        }.toString();
    }

    /**
     * 批量保存机构消息
     * @param map
     * @return
     */
    public String insertMechanismMessageRefList(Map map) {
        List<MechanismMessageRefDO> insertList = (List<MechanismMessageRefDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO mechanism_message_ref ");
        sb.append("(mechanism_id ,message_id) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].mechanismId},#'{'list[{0}].messageId})");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }


    public String getRefMechanismList(final BindMechanismQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" DISTINCT m.* ,a.name provinceCn,b.name cityCn,d.name attributeCn  ");
                FROM(" mechanism m  ");
                LEFT_OUTER_JOIN("  mechanism_message_ref mbr on m.id =mbr.mechanism_id and mbr.is_delete=0 ");
                LEFT_OUTER_JOIN(" area a on a.id =m.province ");
                LEFT_OUTER_JOIN(" area b on b.id =m.city ");
                LEFT_OUTER_JOIN(" (select * from dictionary where type=1) d on d.value=m.attribute ");
                if (!StringUtils.isEmpty(query.getName())) {
                    WHERE(" m.name like #{name} ");
                }
                WHERE(" m.is_delete=0 ");
                WHERE(" mbr.message_id=#{id} ");

                ORDER_BY(" m.create_time desc");
            }
        };
        return sql.toString();
    }
}

