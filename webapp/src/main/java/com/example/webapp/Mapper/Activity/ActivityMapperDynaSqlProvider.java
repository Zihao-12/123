package com.example.webapp.Mapper.Activity;

import com.example.webapp.DO.*;
import com.example.webapp.query.ActivityQuery;
import com.example.webapp.query.ActivityRankingQuery;
import com.example.webapp.query.BindMechanismQuery;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.ExamStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class ActivityMapperDynaSqlProvider {


    public String selectAll(final ActivityQuery query) {
        SQL sql = new SQL() {
            {
                SELECT( "a.`id`,a.`name`,a.`introduction`,a.`cover`,a.`detail`,a.`type`,a.`age`,a.`shape`,a.`status`,a.`begin_time`,a.`end_time`,a.`is_delete`,a.`update_time`,a.`create_time`,a.`question_num`,a.`total_score`,a.`manual`" );
                FROM(" activity a ");
                if(query.getMechanismId() != null){
                    //查询推送给机构的活动
                    LEFT_OUTER_JOIN(" mechanism_activity_ref ma on ma.activity_id = a.id and ma.is_delete =0  ");
                    WHERE(" ma.mechanism_id=#{mechanismId}");
                }
                if(Constant.USER_JOINED.equals(query.getUserJoined())){
                    //查询用户参加过得活动
                    LEFT_OUTER_JOIN(" user_activity_ref ua on ua.activity_id = a.id and ua.is_delete =0  ");
                    WHERE(" ua.user_id=#{userId}");
                }

                if (StringUtils.isNotBlank(query.getName())) {
                    WHERE(" a.name like #{name}");
                }
                if (query.getStatus()!=null) {
                    WHERE(" a.status=#{status}");
                }

                if (query.getType()!=null) {
                    WHERE(" a.type=#{type}");
                }
                if (query.getShape()!=null) {
                    WHERE(" a.shape=#{shape}");
                }
                if (query.getAge()!=null) {
                    WHERE(" a.age=#{age}");
                }
                if(ExamStatusEnum.TO_START.getType().equals(query.getActivityStatus())){
                    // 待开始
                    WHERE(" a.`begin_time` > now()");
                }else if(ExamStatusEnum.START.getType().equals(query.getActivityStatus())){
                    //进行中
                    WHERE(" a.`begin_time` <= now() && a.`end_time` >= now()");
                }else if(ExamStatusEnum.OVER.getType().equals(query.getActivityStatus())){
                    //已结束
                    WHERE("a.`end_time` < now()");
                }

                WHERE(" a.is_delete=0");
                ORDER_BY(" a.create_time DESC");
            }
        };
        return sql.toString();
    }

    public String update(final ActivityDO activityDO) {
        return new SQL() {
            {
                UPDATE(" activity ");
                SET("`name`=#{name}");
                SET("`introduction`=#{introduction}");
                SET("`cover`=#{cover}");
                SET("`detail`=#{detail}");
                SET("`type`=#{type}");
                SET("`age`=#{age}");
                SET("`shape`=#{shape}");
                SET("`status`=#{status}");
                SET("`begin_time`=#{beginTime}");
                SET("`end_time`=#{endTime}");
                WHERE("id=#{id}");
            }

        }.toString();

    }
    public String updateOperation(final ActivityOperationRefDO activityOperationRefDO) {
        return new SQL() {
            {
                UPDATE(" activity_operation_ref ");
                SET("`join_times`=#{joinTimes}");
                SET("`join_frequency`=#{joinFrequency}");
                SET("`integral`=#{integral}");
                SET("`integral_times`=#{integralTimes}");
                SET("`integral_frequency`=#{integralFrequency}");
                SET("`library`=#{library}");
                SET("`country`=#{country}");
                SET("`lottery_close`=#{lotteryClose}");
                SET("`lottery_times`=#{lotteryTimes}");
                SET("`lottery_trigger_rules`=#{lotteryTriggerRules}");
                SET("`lottery_description`=#{lotteryDescription}");
                SET("`show_barrage`=#{showBarrage}");
                WHERE("id=#{id}");
            }
        }.toString();

    }

    /**
     * 批量保存机构活动
     * @param map
     * @return
     */
    public String insertMechanismActivityRefList(Map map) {
        List<MechanismActivityRefDO> insertList = (List<MechanismActivityRefDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO mechanism_activity_ref ");
        sb.append("(mechanism_id ,activity_id) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].mechanismId},#'{'list[{0}].activityId})");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }


    /**
     * 批量保存机构内容
     * @param map
     * @return
     */
    public String insertActivityContentRefList(Map map) {
        List<ActivityContentRefDO> insertList = (List<ActivityContentRefDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO activity_content_ref ");
        sb.append("(activity_id ,category_id,question_num) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].activityId},#'{'list[{0}].categoryId},#'{'list[{0}].questionNum})");
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
                LEFT_OUTER_JOIN("  mechanism_activity_ref mbr on m.id =mbr.mechanism_id and mbr.is_delete=0 ");
                LEFT_OUTER_JOIN(" area a on a.id =m.province ");
                LEFT_OUTER_JOIN(" area b on b.id =m.city ");
                LEFT_OUTER_JOIN(" (select * from dictionary where type=1) d on d.value=m.attribute ");
                if (!StringUtils.isEmpty(query.getName())) {
                    WHERE(" m.name like #{name} ");
                }
                WHERE(" m.is_delete=0 ");
                WHERE(" mbr.activity_id=#{id} ");

                ORDER_BY(" m.create_time desc");
            }
        };
        return sql.toString();
    }

    /**
     * 未完成活动不参与排名
     * @param query
     * @return
     */
    public String rankingList(final ActivityRankingQuery query) {
        SQL sql = new SQL() {
            {
                SELECT( " u.id userId,u.nick_name name,u.head_img,ua.score,ua.times,u.mechanism_id,m.`name` mechanismName " );
                FROM(" user_activity_ref ua ");
                LEFT_OUTER_JOIN(" `user` u on u.id = ua.user_id and ua.is_delete =0  ");
                LEFT_OUTER_JOIN("  mechanism m on m.id = u.mechanism_id ");
                WHERE(" ua.activity_id=#{activityId} and ua.`complete` =1 ");
                ORDER_BY("  ua.score DESC,ua.times ");
            }
        };
        return sql.toString();
    }

    /**
     * 批量保存活动奖品列表
     * @param map
     * @return
     */
    public String insertPrizeDetailList(Map map) {
        List<ActivityPrizeDetailDO> insertList = (List<ActivityPrizeDetailDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO activity_prize_detail ");
        sb.append("(activity_id ,prize_type,prize_name,prize_num,prize_weight,surplus_prize_num) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].activityId},#'{'list[{0}].prizeType},#'{'list[{0}].prizeName},#'{'list[{0}].prizeNum},#'{'list[{0}].prizeWeight},#'{'list[{0}].surplusPrizeNum})");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}

